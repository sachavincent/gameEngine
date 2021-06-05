package util.commands;

import static util.commands.Command.COMMAND_DELIMITER;

import java.util.Arrays;

public class CommandManager {

    static {
        new SpawnCommand();
        new AddPeopleCommand();
        new AddResourceCommand();
        new RemoveResourceCommand();
    }

    public static boolean isTextCommand(String text) {
        return text != null && !text.isEmpty() && text.charAt(0) == Command.COMMAND_SYMBOL;
    }

    public static String[] parseCommand(String command) {
        if (!isTextCommand(command))
            return new String[0];
        command = command.substring(1); // Removes command symbol

        String[] parts = command.split(String.valueOf(COMMAND_DELIMITER));

        return parts;
    }

    public static Command getCommandFromTokens(String[] tokens) {
        if (tokens == null || tokens.length == 0)
            return null;

        Command command = CommandManager.getCommandFromName(tokens[0]);
        if (command == null)
            return null;

        command.setParameters(Arrays.copyOfRange(tokens, 1, tokens.length));
        return command;
    }

    private static Command getCommandFromName(String commandName) {
        return Command.COMMANDS.stream().filter(command -> command.getAlias().contains(commandName)).findAny()
                .map(Command::copy).orElse(null);
    }
}
