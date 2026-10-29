package seedu.clublogistics.logic;

/** Contains messages shared by commands, parsers, and the equipment view. */
public class Messages {
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    /** Formats a snapshot of the visible and total equipment counts. */
    public static String formatEquipmentCount(int shown, int total) {
        return "Showing " + shown + " of " + total + " equipment records.";
    }
}
