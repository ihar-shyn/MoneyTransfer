package ihar.shyn.exception;

public class AccountAlreadyExistException extends RuntimeException {
    public AccountAlreadyExistException(Long id) {
        super("Account with id = " + id + " is already exists");
    }
}
