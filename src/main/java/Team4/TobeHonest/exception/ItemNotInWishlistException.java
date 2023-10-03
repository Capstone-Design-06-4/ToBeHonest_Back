package Team4.TobeHonest.exception;

public class ItemNotInWishlistException extends RuntimeException{

    public ItemNotInWishlistException() {
        super();
    }

    public ItemNotInWishlistException(String message) {
        super(message);
    }

    public ItemNotInWishlistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotInWishlistException(Throwable cause) {
        super(cause);
    }

    protected ItemNotInWishlistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
