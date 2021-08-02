package guis.exceptions;

public class IllegalGuiConstraintException extends RuntimeException {

    public IllegalGuiConstraintException() {
        super("Illegal constraint type for Gui");
    }

    public IllegalGuiConstraintException(String message) {
        super(message);
    }

    public IllegalGuiConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalGuiConstraintException(Throwable cause) {
        super(cause);
    }
}
