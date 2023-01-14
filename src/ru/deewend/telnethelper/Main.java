package ru.deewend.telnethelper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final Thread MAIN_THREAD = Thread.currentThread();

    private final HandlerThread handlerThread;
    private Socket beingRegistered;

    private Main() {
        this.handlerThread = new HandlerThread();
        this.handlerThread.setDaemon(true);
    }

    public static void main(String[] args) throws IOException {
        (new Main()).run();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() throws IOException {
        handlerThread.start();

        ServerSocket listeningSocket = new ServerSocket(2425);
        System.out.println("Listening on port 2425...");

        while (true) {
            beingRegistered = listeningSocket.accept();
            beingRegistered.setTcpNoDelay(true);

            boolean successfullyAdded;
            try {
                successfullyAdded = handlerThread.addClient(beingRegistered);
            } catch (IOException e) {
                reportErrorAndClose(true);

                continue;
            }
            if (successfullyAdded) {
                Utils.reportSomeoneHas("joined", beingRegistered, null);
            } else {
                reportErrorAndClose(false);
            }
        }
    }

    private void reportErrorAndClose(boolean unknownError) throws IOException {
        OutputStream os = beingRegistered.getOutputStream();
        os.write(unknownError ?
                Messages.UNKNOWN_ERROR_KICK_MESSAGE :
                Messages.OVERLOADED_KICK_MESSAGE);
        os.flush();
        beingRegistered.close();

        Utils.reportSomeoneHas("been kicked", beingRegistered,
                (unknownError ? "Unknown error" : "The server is overloaded"));
    }

    public static Thread getMainThread() {
        return MAIN_THREAD;
    }
}
