package net.kronos.rkon.core;

/** An interface for specifying how you want incoming commands to be handled by the server
 * @see RconServer */
public interface IRconHandler {
    /** Specifies how commands coming into the server should be handled. */
    String handle(String message);
}
