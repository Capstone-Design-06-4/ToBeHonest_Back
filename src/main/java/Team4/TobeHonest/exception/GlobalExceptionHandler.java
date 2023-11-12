package Team4.TobeHonest.exception;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.security.SignatureException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateFriendException.class)
    public ResponseEntity<String> handleDuplicateException(DuplicateFriendException e) {
        log.info("DuplicateFriendException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<String> handleDuplicateMemberException(DuplicateMemberException e) {
        log.info("DuplicateMemberException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateWishItemException.class)
    public ResponseEntity<String> handleDuplicateWishItemException(DuplicateWishItemException e) {
        log.info("DuplicateWishItemException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ItemNotInWishlistException.class)
    public ResponseEntity<String> handleItemNotInWishlistException(ItemNotInWishlistException e) {
        log.info("ItemNotInWishlistException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NoItemException.class)
    public ResponseEntity<String> handleNoItemException(NoItemException e) {
        log.info("NoItemException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


    @ExceptionHandler(NoMemberException.class)
    public ResponseEntity<String> handleNoMemberException(NoMemberException e) {
        log.info("NoMemberException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


    @ExceptionHandler(NoPointsException.class)
    public ResponseEntity<String> handleNoPointsException(NoPointsException e) {
        log.info("NoPointsException : {}", e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/members/points/add"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }


    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<String> handleNotEnoughStockException(NotEnoughStockException e) {
        log.info("NotEnoughStockException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


    @ExceptionHandler(NoSuchFriendException.class)
    public ResponseEntity<String> handleNoSuchFriendException(NoSuchFriendException e) {
        log.info("NoSuchFriendException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NotValidWishItemException.class)
    public ResponseEntity<String> handleNotValidWishItemException(NotValidWishItemException e) {
        log.info("NotValidWishItemException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NoWishItemException.class)
    public ResponseEntity<String> handleNoWishItemException(NoWishItemException e) {
        log.info("NoWishItemException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        log.info("IllegalStateException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<String> handleNotValidTokenException(SignatureException e) {
        log.info("로그인하쇼: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }



}






