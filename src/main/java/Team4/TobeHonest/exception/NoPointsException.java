package Team4.TobeHonest.exception;

public class NoPointsException extends RuntimeException{
    public NoPointsException() {
        super();
    }

    public NoPointsException(String message) {
        super(message);
    }

    public NoPointsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPointsException(Throwable cause) {
        super(cause);
    }

    protected NoPointsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
