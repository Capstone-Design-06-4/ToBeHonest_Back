package Team4.TobeHonest.exception;

public class NoSuchFriendException extends RuntimeException{
    public NoSuchFriendException() {
        super();
    }

    public NoSuchFriendException(String message) {
        super(message);
    }

    public NoSuchFriendException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchFriendException(Throwable cause) {
        super(cause);
    }

    protected NoSuchFriendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}


