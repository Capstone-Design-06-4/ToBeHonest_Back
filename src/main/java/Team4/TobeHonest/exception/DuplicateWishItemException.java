package Team4.TobeHonest.exception;

public class DuplicateWishItemException extends RuntimeException{
    public DuplicateWishItemException() {
        super();
    }

    public DuplicateWishItemException(String message) {
        super(message);
    }

    public DuplicateWishItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateWishItemException(Throwable cause) {
        super(cause);
    }

    protected DuplicateWishItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
