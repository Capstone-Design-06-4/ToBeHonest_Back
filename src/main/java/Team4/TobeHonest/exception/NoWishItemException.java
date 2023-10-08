package Team4.TobeHonest.exception;

public class NoWishItemException extends RuntimeException{
    public NoWishItemException() {
        super();
    }

    public NoWishItemException(String message) {
        super(message);
    }

    public NoWishItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoWishItemException(Throwable cause) {
        super(cause);
    }

    protected NoWishItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
