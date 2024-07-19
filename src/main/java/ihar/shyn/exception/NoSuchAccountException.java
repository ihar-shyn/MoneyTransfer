package ihar.shyn.exception;

public class NoSuchAccountException extends RuntimeException {
    public NoSuchAccountException(Long id) {
        super("Account with id = " + id + " not exists");
    }
}
