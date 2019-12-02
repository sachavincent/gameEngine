package guis.exceptions;

public class UnknownLanguageException extends RuntimeException {

    public UnknownLanguageException() {
        super("Unknown language.");
    }

    public UnknownLanguageException(String message) {
        super(message);
    }

    public UnknownLanguageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownLanguageException(Throwable cause) {
        super(cause);
    }

    protected UnknownLanguageException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
