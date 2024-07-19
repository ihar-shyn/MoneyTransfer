package ihar.shyn.exception;

public class InvalidAccountIdException extends RuntimeException{
    public InvalidAccountIdException(Long id) {
        super("Invalid account id - " + id);
    }
}
