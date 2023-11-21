package Team4.TobeHonest.exception;

public class NotValidWishItemException extends RuntimeException{
    public NotValidWishItemException() {
        super();
    }

    public NotValidWishItemException(String message) {
        super(message);
    }

    public NotValidWishItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidWishItemException(Throwable cause) {
        super(cause);
    }

    protected NotValidWishItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
