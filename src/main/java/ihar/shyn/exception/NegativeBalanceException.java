package ihar.shyn.exception;

public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(Long id) {
        super("Account balance can't be negative on account with id = " + id);
    }
}
