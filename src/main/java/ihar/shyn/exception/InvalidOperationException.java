package ihar.shyn.exception;

import java.math.BigDecimal;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(Long accFrom, Long accTo, BigDecimal amount) {
        super("Can't transfer from account" + accFrom + " to account " + accTo + " sum = " + amount);
    }
}
