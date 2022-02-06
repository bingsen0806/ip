package duke.main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import duke.command.AddDeadlineCommand;
import duke.command.AddEventCommand;
import duke.command.AddTodoCommand;
import duke.command.Command;
import duke.command.DeleteCommand;
import duke.command.ExitCommand;
import duke.command.FindCommand;
import duke.command.ListCommand;
import duke.command.MarkCommand;
import duke.exception.DukeException;

/**
 * Parser is a utility class with static methods used to parse
 * user input into Command and LocalDateTime objects.
 * the output on the screen.
 */
public class Parser {
    /**
     * Returns a Command object representing the user input.
     *
     * @param fullCommand Raw input from user.
     * @return A Command representing the user input.
     * @throws DukeException If user input is not a valid command or valid but in the wrong format.
     */
    static Command parseCommand(String fullCommand) throws DukeException {
        if (fullCommand.trim().compareToIgnoreCase("bye") == 0) {
            return new ExitCommand();
        } else if (fullCommand.equalsIgnoreCase("list")) {
            return new ListCommand();
        } else if (fullCommand.startsWith("mark")) {
            String inputWithoutCommand = fullCommand.replaceFirst("mark", "").trim();
            return parseMarkCommand(inputWithoutCommand, true);
        } else if (fullCommand.startsWith("unmark")) {
            String inputWithoutCommand = fullCommand.replaceFirst("unmark", "").trim();
            return parseMarkCommand(inputWithoutCommand, false);
        } else if (fullCommand.startsWith("todo")) {
            String inputWithoutCommand = fullCommand.replaceFirst("todo", "").trim();
            return parseTodoCommand(inputWithoutCommand);
        } else if (fullCommand.startsWith("deadline")) {
            String inputWithoutCommand = fullCommand.replaceFirst("deadline", "").trim();
            return parseDeadlineCommand(inputWithoutCommand);
        } else if (fullCommand.startsWith("event")) {
            String inputWithoutCommand = fullCommand.replaceFirst("event", "").trim();
            return parseEventCommand(inputWithoutCommand);
        } else if (fullCommand.startsWith("delete")) {
            String inputWithoutCommand = fullCommand.replaceFirst("delete", "").trim();
            return parseDeleteCommand(inputWithoutCommand);
        } else if (fullCommand.startsWith("find")) {
            String inputWithoutCommand = fullCommand.replaceFirst("find", "").trim();
            return parseFindCommand(inputWithoutCommand);
        } else {
            throw new DukeException(DukeException.ERROR_NO_COMMAND);
        }
    }

    /**
     * Returns a java.time.LocalDateTime object representing the input.
     *
     * @param input The input to be parsed, "yyyy/MM/dd HHmm" format.
     * @return The parsed LocalDateTime.
     * @throws DukeException If the input cannot be parsed based on the stated format.
     */
    static LocalDateTime parseDateTime(String input) throws DukeException {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HHmm");
            return LocalDateTime.parse(input, format);
        } catch (DateTimeParseException e) {
            throw new DukeException(DukeException.FORMAT_DATE);
        }
    }

    private static MarkCommand parseMarkCommand(String input, boolean isDone) throws DukeException {
        try {
            int taskId = Integer.parseInt(input) - 1;
            return new MarkCommand(taskId, isDone);
        } catch (NumberFormatException e) {
            throw new DukeException(DukeException.ERROR_PARSE_INT);
        }
    }

    private static AddTodoCommand parseTodoCommand(String input) throws DukeException {
        if (isEmptyString(input)) {
            throw new DukeException(DukeException.ERROR_TODO_NO_NAME);
        }
        return new AddTodoCommand(input);
    }

    private static AddDeadlineCommand parseDeadlineCommand(String input) throws DukeException {
        String[] splitInputs = input.split("/by");
        if (!isValidDeadlineEventSplit(splitInputs)) {
            throw new DukeException(DukeException.ERROR_WRONG_FORMAT + DukeException.FORMAT_DEADLINE);
        }
        String description = splitInputs[0].trim();
        String deadlineString = splitInputs[1].trim();
        LocalDateTime deadline = parseDateTime(deadlineString);
        return new AddDeadlineCommand(description, deadline);
    }

    private static AddEventCommand parseEventCommand(String input) throws DukeException {
        String[] splitInputs = input.split("/at");
        if (!isValidDeadlineEventSplit(splitInputs)) {
            throw new DukeException(DukeException.ERROR_WRONG_FORMAT + DukeException.FORMAT_EVENT);
        }
        String description = splitInputs[0].trim();
        String timeString = splitInputs[1].trim();
        LocalDateTime time = parseDateTime(timeString);
        return new AddEventCommand(description, time);
    }

    private static DeleteCommand parseDeleteCommand(String input) throws DukeException {
        try {
            int taskId = Integer.parseInt(input) - 1;
            return new DeleteCommand(taskId);
        } catch (NumberFormatException e) {
            throw new DukeException(DukeException.ERROR_PARSE_INT);
        }
    }

    private static FindCommand parseFindCommand(String input) throws DukeException {
        if (isEmptyString(input)) {
            throw new DukeException(DukeException.ERROR_FIND_NO_NAME);
        }
        return new FindCommand(input);
    }

    private static boolean isValidDeadlineEventSplit(String[] splitInput) {
        return splitInput.length == 2 && !isEmptyString(splitInput[0]) && !isEmptyString(splitInput[1]);
    }

    private static boolean isEmptyString(String s) {
        return s.matches("\\s*");
    }
}
