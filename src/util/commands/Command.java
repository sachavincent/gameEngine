package util.commands;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import services.Service;
import util.Utils;

public abstract class Command {

    public static final Set<Command> COMMANDS = new HashSet<>();

    public static final char   COMMAND_SYMBOL             = '/';
    public static final char   COMMAND_DELIMITER          = ' ';
    public static final char   COMMAND_OPTIONAL_OPEN      = '[';
    public static final char   COMMAND_OPTIONAL_CLOSE     = ']';
    public static final char   COMMAND_OPTIONAL_SEPARATOR = ',';
    public static final String COMMAND_POSITION_REGEX     = "^\\([0-9]+,\\s*[0-9]+\\)$";

    protected final List<String> alias;

    // Name -> value
    protected final Map<String, String>    parameters;
    protected final Map<String, String>    optionalParameters;
    protected final ExecuteCommandCallback onExecuteCommandCallback;

    public Command(String[] alias, String[] parameterNames, ExecuteCommandCallback onExecuteCallback) {
        this.alias = Arrays.asList(alias);
        this.parameters = new LinkedHashMap<>();
        this.optionalParameters = new LinkedHashMap<>();
        Arrays.asList(parameterNames).forEach(paramName -> {
            this.parameters.put(paramName, "");
        });
        this.onExecuteCommandCallback = onExecuteCallback;

        COMMANDS.add(this);
    }

    public Command(String[] alias, String[] parameterNames, String[] optionalParameterNames,
            ExecuteCommandCallback onExecuteCallback) {
        this.alias = Arrays.asList(alias);
        this.parameters = new LinkedHashMap<>();
        this.optionalParameters = new LinkedHashMap<>();
        Arrays.asList(parameterNames).forEach(paramName -> {
            this.parameters.put(paramName, "");
        });
        Arrays.asList(optionalParameterNames).forEach(paramName -> {
            this.optionalParameters.put(paramName, null);
        });
        this.onExecuteCommandCallback = onExecuteCallback;

        COMMANDS.add(this);
    }

    public int execute() {
        if (this.parameters.values().stream().anyMatch(String::isEmpty))
            return -1;

        return new Service<Integer>(integer -> {
            System.out.println("Done: " + integer);
        }) {
            @Override
            protected Integer execute() {
                return onExecuteCommandCallback.onExecute(
                        Stream.concat(parameters.values().stream(), optionalParameters.values().stream())
                                .toArray(String[]::new));
            }
        }.execute();
    }

    public String getSyntax() {
        String mainPart = "/" + alias.get(0) + " " +
                this.parameters.keySet().stream().map(param -> "<" + param + ">").collect(Collectors.joining(" "));
        String optionalPart = " [" +
                this.optionalParameters.keySet().stream().map(Utils::formatText).collect(Collectors.joining(", ")) +
                "]";
        if (this.optionalParameters.isEmpty())
            return mainPart;

        return mainPart + optionalPart;
    }

    public void setParameters(String[] parameters) {
        int parametersSize = this.parameters.keySet().size();

        if (parameters.length < parametersSize)
            return;
        if (parameters.length > parametersSize + this.optionalParameters.keySet().size())
            return;

        int i = 0;
        Map<String, String> parametersCopy = new LinkedHashMap<>(this.parameters);
        for (Entry<String, String> entry : parametersCopy.entrySet())
            entry.setValue(parameters[i++]);

        if (parameters.length > parametersSize) { // Optional parameters used
            StringBuilder optionalParametersBuilder = new StringBuilder();
            for (int j = i; j < parameters.length; j++)
                optionalParametersBuilder.append(parameters[j]);

            String optionalParameters = optionalParametersBuilder.toString().trim();
            if (optionalParameters.isEmpty())
                return;

            if (optionalParameters.charAt(0) != COMMAND_OPTIONAL_OPEN ||
                    optionalParameters.charAt(optionalParameters.length() - 1) != COMMAND_OPTIONAL_CLOSE)
                return; // Wrong syntax
            optionalParameters = optionalParameters.substring(1, optionalParameters.length() - 1); // Removing brackets
            String[] optionalParametersList = optionalParameters.split(String.valueOf(COMMAND_OPTIONAL_SEPARATOR));
            List<String> optionalParametersListNew = new ArrayList<>();
            for (int j = 0; j < optionalParametersList.length; j++) {
                String optionalParameter = optionalParametersList[j];
//                long nbOpenedParentheses = optionalParameter.codePoints().filter(ch -> ch == '(').count();
//                long nbClosedParentheses = optionalParameter.codePoints().filter(ch -> ch == ')').count();
//                if(nbOpenedParentheses == nbClosedParentheses)
//                for (long j = nbOpenedParentheses; j < nbClosedParentheses; j++) {
//
//                }
                if (!optionalParameter.contains("("))
                    optionalParametersListNew.add(optionalParameter);
                else if (j < optionalParametersList.length - 1)
                    optionalParametersListNew.add(optionalParameter + "," + optionalParametersList[++j]);
            }

            optionalParametersList = optionalParametersListNew.toArray(new String[0]);
            Map<String, String> optionalParametersCopy = new LinkedHashMap<>();
            for (String optionalParameter : optionalParametersList) {
                if (!optionalParameter.contains("="))
                    return;
                String[] split = optionalParameter.split("=");
                if (split.length != 2)
                    return;
                String key = split[0].trim().toLowerCase(Locale.ROOT);
                String value = split[1].trim().toLowerCase(Locale.ROOT);
                if (!this.optionalParameters.containsKey(key) || key.isEmpty() || value.isEmpty())
                    return;
                optionalParametersCopy.put(key, value);
            }
            this.optionalParameters.entrySet().forEach(entry -> {
                if (optionalParametersCopy.containsKey(entry.getKey()))
                    entry.setValue(optionalParametersCopy.get(entry.getKey()));
            });
        }
        this.parameters.clear();
        this.parameters.putAll(parametersCopy);
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public List<String> getAlias() {
        return this.alias;
    }

    /**
     * Translates error code into message
     *
     * @param result error code
     * case -1: incorrect nb of parameters
     * case 0: successfull
     * @return relevant message:
     */
    public abstract String getMessageFromResult(int result);

    public ExecuteCommandCallback getOnExecuteCommandCallback() {
        return this.onExecuteCommandCallback;
    }

    protected abstract Command copy();

    public interface ExecuteCommandCallback {

        default int onExecute(String... params) {
            throw new UnsupportedOperationException("");
        }
    }
}