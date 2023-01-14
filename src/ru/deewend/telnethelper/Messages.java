package ru.deewend.telnethelper;

import java.nio.charset.StandardCharsets;

public class Messages {
    public static final byte[] UNKNOWN_ERROR_KICK_MESSAGE = ("Whoops, looks like " +
            "we've faced with some issues registering your connection. Please try " +
            "again later :(").getBytes(StandardCharsets.US_ASCII);

    public static final byte[] OVERLOADED_KICK_MESSAGE = ("Reached the limit of " +
            "max concurrent connections count (100 out of 100), please try again " +
            "in a few seconds\r\n").getBytes(StandardCharsets.US_ASCII);

    public static final byte[] GAME_TITLE_STRING =
            "Welcome to the Telnet Battleship!".getBytes(StandardCharsets.US_ASCII);

    public static final String ACTIVE_CONNECTIONS_COUNT_STRING_FORMAT = " At this " +
            "moment there are %d (out of " + HandlerThread.MAX_ACTIVE_CONNECTIONS_COUNT +
            ") active connections.";

    private Messages() {
    }
}
