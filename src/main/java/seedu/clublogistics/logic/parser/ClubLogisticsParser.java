package seedu.clublogistics.logic.parser;

import static seedu.clublogistics.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.clublogistics.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.clublogistics.logic.commands.AddEquipmentCommand;
import seedu.clublogistics.logic.commands.ClearCommand;
import seedu.clublogistics.logic.commands.Command;
import seedu.clublogistics.logic.commands.ExitCommand;
import seedu.clublogistics.logic.commands.FilterCommand;
import seedu.clublogistics.logic.commands.FindCommand;
import seedu.clublogistics.logic.commands.HelpCommand;
import seedu.clublogistics.logic.commands.IssueCommand;
import seedu.clublogistics.logic.commands.ListCommand;
import seedu.clublogistics.logic.commands.RemoveCommand;
import seedu.clublogistics.logic.commands.ReturnCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class ClubLogisticsParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        return switch (commandWord) {
            case AddEquipmentCommand.COMMAND_WORD -> new AddEquipmentCommandParser().parse(arguments);
            case ClearCommand.COMMAND_WORD -> new ClearCommandParser().parse(arguments);
            case FindCommand.COMMAND_WORD -> new FindCommandParser().parse(arguments);
            case FilterCommand.COMMAND_WORD -> new FilterCommandParser().parse(arguments);
            case RemoveCommand.COMMAND_WORD -> new RemoveCommandParser().parse(arguments);
            case ReturnCommand.COMMAND_WORD -> new ReturnCommandParser().parse(arguments);
            case ListCommand.COMMAND_WORD -> new ListCommandParser().parse(arguments);
            case ExitCommand.COMMAND_WORD -> new ExitCommandParser().parse(arguments);
            case HelpCommand.COMMAND_WORD -> new HelpCommandParser().parse(arguments);
            case IssueCommand.COMMAND_WORD -> new IssueCommandParser().parse(arguments);
            default -> {
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        };
    }

}
