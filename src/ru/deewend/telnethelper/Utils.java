package ru.deewend.telnethelper;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    private static final Format DATE_FORMAT_1 =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static final Format DATE_FORMAT_2 = (Format) DATE_FORMAT_1.clone();

    private Utils() {
    }

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
            /* Ignoring this. */
        }
    }

    public static void reportSomeoneHas(String action, Socket socket, String comment) {
        Format formatter = (Thread.currentThread() == Main.getMainThread() ?
                DATE_FORMAT_1 : DATE_FORMAT_2);

        System.out.println("[" + formatter.format(new Date()) + "] Someone (" +
                socket.getInetAddress() + ") has " + action + ". " +
                (comment != null ? "(" + comment + ".)" : ""));
    }

    public static long delta(long timestamp) {
        return System.currentTimeMillis() - timestamp;
    }
}
