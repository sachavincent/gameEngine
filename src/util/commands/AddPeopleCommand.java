package util.commands;

import engineTester.Game;
import java.util.ArrayList;
import java.util.List;

public class AddPeopleCommand extends Command {

    private static final int MAX_PEOPLE = 50;

    private static final String[] LOCAL_ALIAS               = new String[]{"addpeople", "ap"};
    private static final String[] LOCAL_PARAMETERS          = new String[]{"amount of people"};
    private static final String[] LOCAL_OPTIONAL_PARAMETERS = new String[]{ };

    private static final LocalExecuteCommandCallback LOCAL_CALLBACK = amount -> {
        if (!amount.matches("^\\d+$"))
            return 1;

        int intAmount = Integer.parseInt(amount);
        if (intAmount <= 0 || intAmount > MAX_PEOPLE)
            return 2;


        for (int i = 0; i < intAmount; i++)
            if (!Game.getInstance().addPerson())
                return 3;

        return 0;
    };

    public AddPeopleCommand() {
        super(LOCAL_ALIAS, LOCAL_PARAMETERS, LOCAL_OPTIONAL_PARAMETERS, LOCAL_CALLBACK);
    }

    @Override
    public String getMessageFromResult(int result) {
        List<String> parameterValues = new ArrayList<>(this.parameters.values());
        switch (result) {
            case -1:
                return "Syntax: " + getSyntax();
            case 0:
                return "Added " + parameterValues.get(0) + " FARMERS successfully!";
            case 1:
                return "Incorrect amount: " + parameterValues.get(0) + "!";
            case 2:
                return "The amount should be between 1 and " + MAX_PEOPLE + "!";
            case 3:
                return "An error occured during this command's execution!";
        }
        return "";
    }

    @Override
    protected Command copy() {
        return new AddPeopleCommand();
    }


    @FunctionalInterface
    interface LocalExecuteCommandCallback extends ExecuteCommandCallback {

        @Override
        default int onExecute(String... params) {
            if (params.length == 1)
                return onExecute(params[0]);

            return -1;
        }

        int onExecute(String p1);
    }
}
