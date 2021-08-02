package util.exceptions;

public class MTLFileException extends RuntimeException {

    public MTLFileException() {
    }

    public MTLFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public MTLFileException(Throwable cause) {
        super(cause);
    }

    public MTLFileException(String message, Throwable cause, boolean enableSuppression,
                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MTLFileException(String line) {
        super("Error in MTL file at line=" + line + "!");
    }
}
