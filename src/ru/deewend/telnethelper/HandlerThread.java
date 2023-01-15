package ru.deewend.telnethelper;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HandlerThread extends Thread {
    public static final int MAX_ACTIVE_CONNECTIONS_COUNT = 100;
    public static final int TICK_RATE = 25;
    public static final int TICK_INTERVAL_MS = 1000 / TICK_RATE;
    public static final int READ_TIMEOUT = 420_000;

    private final List<SocketHolder> clientList;

    public HandlerThread() {
        setName("HandlerThread");

        this.clientList = new ArrayList<>();
    }

    public synchronized boolean addClient(Socket socket) throws IOException {
        if (clientList.size() >= MAX_ACTIVE_CONNECTIONS_COUNT) {
            return false;
        }
        clientList.add(new SocketHolder(socket));

        return true;
    }

    private synchronized void tick() {
        int clientListSize = clientList.size();
        for (int i = clientListSize - 1; i >= 0; i--) {
            SocketHolder holder = clientList.get(i);
            long currentTimeMillis = System.currentTimeMillis();
            try {
                if (currentTimeMillis - holder.lastTestedIfAlive >= 1500) {
                    holder.outputStream.write('\0');
                    holder.outputStream.flush();
                    holder.serverOutputStream.write('\0');
                    holder.serverOutputStream.flush();

                    holder.lastTestedIfAlive = currentTimeMillis;
                }
                if (
                        holder.thisIsMainMenu &&
                        holder.cachedConnectionsCountPreviouslySent != clientListSize
                ) {
                    byte[] packet = holder.cachedOriginalMainMenuPacket;
                    int position = holder.cachedMainMenuInjectPosition;
                    int delta = packet.length - position;

                    holder.outputStream.write(packet, 0, position);
                    inject(holder);
                    holder.outputStream.write(packet, position, delta);

                    holder.cachedConnectionsCountPreviouslySent = clientListSize;
                }

                int availableFromClient, availableFromServer;
                if ((availableFromClient = holder.inputStream.available()) > 0) {
                    byte[] packet = new byte[availableFromClient];
                    //noinspection ResultOfMethodCallIgnored
                    holder.inputStream.read(packet);
                    holder.serverOutputStream.write(packet);
                    holder.serverOutputStream.flush();
                    holder.lastReadTimestamp = currentTimeMillis;
                } else {
                    if (Utils.delta(holder.lastReadTimestamp) >= READ_TIMEOUT) {
                        close(holder);

                        continue;
                    }
                }

                if ((availableFromServer = holder.serverInputStream.available()) > 0) {
                    byte[] packet = new byte[availableFromServer];
                    //noinspection ResultOfMethodCallIgnored
                    holder.serverInputStream.read(packet);

                    int initialLength = packet.length;
                    boolean needToSendGameTitle = false;

                    int position = analyzeServerPacket(packet);
                    if (position != -1) {
                        initialLength = position;
                        needToSendGameTitle = true;
                    }

                    holder.outputStream.write(packet, 0, initialLength);
                    if (needToSendGameTitle) {
                        inject(holder);

                        if (!holder.thisIsMainMenu) {
                            holder.thisIsMainMenu = true;
                            holder.lastSentMainMenuData = currentTimeMillis;
                            holder.cachedOriginalMainMenuPacket = packet;
                            holder.cachedMainMenuInjectPosition = initialLength;
                        }
                    } else {
                        if (holder.thisIsMainMenu) {
                            holder.thisIsMainMenu = false;
                            holder.cachedOriginalMainMenuPacket = null; // free memory
                        }
                    }
                    if (initialLength < packet.length) {
                        holder.outputStream.write(packet,
                                initialLength, (packet.length - initialLength));
                    }
                    holder.outputStream.flush();
                    holder.lastServerReadTimestamp = currentTimeMillis;
                } else {
                    if (Utils.delta(holder.lastServerReadTimestamp) >= READ_TIMEOUT) {
                        close(holder);
                    }
                }
            } catch (IOException e) {
                close(holder);
            }
        }
    }

    private synchronized void inject(SocketHolder holder) throws IOException {
        byte[] toInject = String.format(
                Messages.ACTIVE_CONNECTIONS_COUNT_STRING_FORMAT,
                clientList.size()
        ).getBytes(StandardCharsets.US_ASCII);

        holder.outputStream.write(toInject);
    }

    /**
     * Returns the position at which we should insert
     * the information about the current active connections count.
     *
     * Handling the situation when the game title string is chunked
     * between two or more packets is NOT supported.
     *
     * @param packet Packet we've just received from the origin game server.
     * @return The position.
     */
    private int analyzeServerPacket(byte[] packet) {
        int checkedSize = 0;
        for (int i = 0; i < packet.length; i++) {
            if (packet[i] == Messages.GAME_TITLE_STRING[checkedSize]) {
                checkedSize++;
                if (checkedSize == Messages.GAME_TITLE_STRING.length) {
                    return (i + 1);
                }
            } else {
                checkedSize = 0;
            }
        }

        return -1;
    }

    @Override
    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait", "finally"})
    public void run() {
        try {
            while (true) {
                long start = System.currentTimeMillis();
                tick();
                long delta = Utils.delta(start);
                if (delta < TICK_INTERVAL_MS) {
                    Thread.sleep(TICK_INTERVAL_MS - delta);
                } else {
                    Thread.yield();
                }
            }
        } catch (Throwable t) {
            System.err.println("An exception/error has occurred, " +
                    "printing the stacktrace...");
            t.printStackTrace();
        } finally {
            System.err.println("HandlerThread has died, terminating the tunnel...");

            System.exit(-1);
        }
    }

    private void close(SocketHolder holder) { // do not change this param to index int
        // to prevent issues in case this method gets called multiple times with the same i
        Utils.close(holder.socket);
        Utils.close(holder.serverSocket);
        synchronized (this) {
            clientList.remove(holder);
        }

        Utils.reportSomeoneHas("left", holder.socket, null);
    }
}
