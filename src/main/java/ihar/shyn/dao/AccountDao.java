package ihar.shyn.dao;

import ihar.shyn.model.Account;
import java.math.BigDecimal;

public interface AccountDao {

    Account createAccount(Long accountId, BigDecimal initialBalance);

    Account getAccount(Long id);

    void withdraw(Long accountId, BigDecimal amount);

    void deposit(Long accountId, BigDecimal amount);
}
