package net.kronos.rkon.core;

/** An interface for specifying how you want incoming commands to be handled by the server
 * @see RconServer */
@FunctionalInterface
public interface IRconHandler {
    /** Specifies how commands coming into the server should be handled.
     * @param message The message sent to the server, usually in the form of a command.
     * @return The return string sent back after handling the command. */
    String handle(String message);
}
