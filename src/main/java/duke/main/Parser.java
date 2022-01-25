package duke.main;

import duke.command.AddDeadlineCommand;
import duke.command.AddEventCommand;
import duke.command.AddTodoCommand;
import duke.command.Command;
import duke.command.DeleteCommand;
import duke.command.ExitCommand;
import duke.command.ListCommand;
import duke.command.MarkCommand;
import duke.exception.DukeException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {
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
        } else {
            throw new DukeException(DukeException.ERROR_NO_COMMAND);
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
        if (input.matches("\\s*")) {
            throw new DukeException(DukeException.ERROR_TODO_NO_NAME);
        }
        return new AddTodoCommand(input);
    }

    private static AddDeadlineCommand parseDeadlineCommand(String input) throws DukeException {
        String[] splitInput = input.split("/by");
        if (!validateDeadlineEventSplit(splitInput)) {
            throw new DukeException(DukeException.ERROR_WRONG_FORMAT + "\n" + DukeException.FORMAT_DEADLINE);
        }
        String description = splitInput[0].trim();
        String deadlineString = splitInput[1].trim();
        LocalDateTime deadline = parseDateTime(deadlineString);
        return new AddDeadlineCommand(description, deadline);
    }

    private static AddEventCommand parseEventCommand(String input) throws DukeException {
        String[] splitInput = input.split("/at");
        if (!validateDeadlineEventSplit(splitInput)) {
            throw new DukeException(DukeException.ERROR_WRONG_FORMAT + "\n" + DukeException.FORMAT_EVENT);
        }
        String description = splitInput[0].trim();
        String timeString = splitInput[1].trim();
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

    static LocalDateTime parseDateTime(String input) throws DukeException {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HHmm");
            LocalDateTime dateTime = LocalDateTime.parse(input, format);
            return dateTime;
        } catch (DateTimeParseException e) {
            throw new DukeException(DukeException.FORMAT_DATE);
        }
    }

    private static boolean validateDeadlineEventSplit(String[] splitInput) throws DukeException {
        if (splitInput.length != 2 || splitInput[0].matches("\\s*") || splitInput[1].matches("\\s*")) {
            return false;
        }
        return true;
    }
}
