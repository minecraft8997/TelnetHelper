package ru.deewend.telnethelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketHolder {
    public final long creationTimestamp;
    public long lastTestedIfAlive;

    public final Socket socket;
    public final InputStream inputStream;
    public final OutputStream outputStream;
    public long lastReadTimestamp;

    public /* final */ Socket serverSocket;
    public final InputStream serverInputStream;
    public final OutputStream serverOutputStream;
    public long lastServerReadTimestamp;

    public SocketHolder(Socket socket) throws IOException {
        this.creationTimestamp = System.currentTimeMillis();
        this.lastTestedIfAlive = creationTimestamp;

        // Client --> Proxy
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.lastReadTimestamp = creationTimestamp;

        try {
            // Proxy --> Origin Server

            // serverSocket is just a general Socket, not a ServerSocket
            this.serverSocket = new Socket("127.0.0.1", 2426);
            this.serverInputStream = serverSocket.getInputStream();
            this.serverOutputStream = serverSocket.getOutputStream();
            this.lastServerReadTimestamp = creationTimestamp;
        } catch (Throwable t) {
            if (serverSocket != null) {
                Utils.close(serverSocket);
            }
            throw t;
        }
    }
}
