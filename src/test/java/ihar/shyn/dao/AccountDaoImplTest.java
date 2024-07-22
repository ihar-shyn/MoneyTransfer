package ihar.shyn.dao;

import ihar.shyn.exception.NegativeBalanceException;
import ihar.shyn.exception.NoSuchAccountException;
import ihar.shyn.model.Account;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

public class AccountDaoImplTest {

    private static AccountDao accountDao;

    @BeforeEach
    public void init() {
        accountDao = new AccountDaoImpl();
    }

    @Test
    public void whenCreateAccount_thenCreateOnlyValidAccount() throws NegativeBalanceException {
        Assertions.assertThrows(NegativeBalanceException.class, () -> accountDao.createAccount(new BigDecimal("-300")));
        Account account1 = accountDao.createAccount(new BigDecimal("300"));
        Assertions.assertNotNull(account1);
        Account account2 = accountDao.createAccount(new BigDecimal("0"));
        Assertions.assertNotNull(account2);
    }

    @Test
    public void whenGettingAccount_thenGetOnlyExistingAccount()
            throws NegativeBalanceException, NoSuchAccountException {
        Account account1 = accountDao.createAccount(new BigDecimal("300"));
        Assertions.assertEquals(accountDao.getAccount(account1.getId()), account1);
        Assertions.assertThrows(NoSuchAccountException.class, () -> accountDao.getAccount(2L));
    }

    @Test
    public void whenWithdraw_thenMakeOnlyValidWithdraw() throws NegativeBalanceException, NoSuchAccountException {
        Account account = accountDao.createAccount(new BigDecimal("300"));
        accountDao.withdraw(account.getId(), new BigDecimal("200"));
        Assertions.assertEquals(account.getBalance(), new BigDecimal("100"));
        Assertions.assertThrows(NegativeBalanceException.class, () -> accountDao.withdraw(account.getId(), new BigDecimal("200")));
    }

    @Test
    public void whenDeposit_thenMakeOnlyValidDeposit() throws  NegativeBalanceException, NoSuchAccountException {
        Account account = accountDao.createAccount(new BigDecimal("300"));
        accountDao.deposit(account.getId(), new BigDecimal("200"));
        Assertions.assertEquals(account.getBalance(), new BigDecimal("500"));
    }

}
