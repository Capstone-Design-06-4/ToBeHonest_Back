package Team4.TobeHonest.exception;

public class DuplicateFriendException extends RuntimeException{

    public DuplicateFriendException() {
        super();
    }

    public DuplicateFriendException(String message) {
        super(message);
    }

    public DuplicateFriendException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateFriendException(Throwable cause) {
        super(cause);
    }

    protected DuplicateFriendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
