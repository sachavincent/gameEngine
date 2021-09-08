package util.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import resources.ResourceManager;
import resources.ResourceManager.Resource;

public class RemoveResourceCommand extends Command {

    private static final String[] VALID_RESOURCE = new String[]{"FISH", "WHEAT", "BREAD", "GOLD"};

    private static final String[] LOCAL_ALIAS               = new String[]{"rmresource", "rr"};
    private static final String[] LOCAL_PARAMETERS          = new String[]{"resource", "amount"};
    private static final String[] LOCAL_OPTIONAL_PARAMETERS = new String[]{ };

    private static final LocalExecuteCommandCallback LOCAL_CALLBACK = (resourceName, amount) -> {
        if (!isResourceValid(resourceName))
            return 1;

        Resource resource;
        try {
            resource = Resource.valueOf(resourceName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return 2;
        }

        if (!amount.matches("^\\d+$"))
            return 3;

        int intAmount = Integer.parseInt(amount);
        if (intAmount <= 0)
            return 4;

        ResourceManager.removeFromResource(resource, intAmount);
        return 0;
    };

    public RemoveResourceCommand() {
        super(LOCAL_ALIAS, LOCAL_PARAMETERS, LOCAL_OPTIONAL_PARAMETERS, LOCAL_CALLBACK);
    }

    @Override
    public String getMessageFromResult(int result) {
        List<String> parameterValues = new ArrayList<>(this.parameters.values());
        switch (result) {
            case -1:
                return "Syntax: " + getSyntax();
            case 0:
                return "Removed " + parameterValues.get(1) + " " + parameterValues.get(0).toUpperCase() +
                        " successfully!";
            case 1:
                return "Unsupported resource: " + parameterValues.get(0) + "!";
            case 2:
                return "Unknown resource: " + parameterValues.get(0) + "!";
            case 3:
                return "Incorrect amount: " + parameterValues.get(0) + "!";
            case 4:
                return "The amount should be greater than 0!";
        }
        return "";
    }

    @Override
    protected Command copy() {
        return new RemoveResourceCommand();
    }

    private static boolean isResourceValid(String resourceName) {
        return Arrays.stream(VALID_RESOURCE).anyMatch(resourceName::equalsIgnoreCase);
    }

    @FunctionalInterface
    interface LocalExecuteCommandCallback extends ExecuteCommandCallback {

        @Override
        default int onExecute(String... params) {
            if (params.length == 2)
                return onExecute(params[0], params[1]);

            return -1;
        }

        int onExecute(String p1, String p2);
    }
}
