package ihar.shyn.dao;

import ihar.shyn.exception.AccountAlreadyExistException;
import ihar.shyn.exception.InvalidAccountIdException;
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
    public void whenCreateAccount_thenCreateOnlyValidAccount() throws NegativeBalanceException, InvalidAccountIdException, AccountAlreadyExistException {
        Assertions.assertThrows(InvalidAccountIdException.class, () -> accountDao.createAccount(-1L, new BigDecimal("300")));
        Assertions.assertThrows(NegativeBalanceException.class, () -> accountDao.createAccount(1L, new BigDecimal("-300")));
        Account account1 = accountDao.createAccount(1L, new BigDecimal("300"));
        Assertions.assertNotNull(account1);
        Assertions.assertThrows(AccountAlreadyExistException.class, () -> accountDao.createAccount(1L, new BigDecimal("400")));
        Account account2 = accountDao.createAccount(2L, new BigDecimal("0"));
        Assertions.assertNotNull(account2);
    }

    @Test
    public void whenGettingAccount_thenGetOnlyExistingAccount()
            throws AccountAlreadyExistException, NegativeBalanceException, InvalidAccountIdException, NoSuchAccountException {
        Account account1 = accountDao.createAccount(1L, new BigDecimal("300"));
        Assertions.assertEquals(accountDao.getAccount(1L), account1);
        Assertions.assertThrows(NoSuchAccountException.class, () -> accountDao.getAccount(2L));
    }

    @Test
    public void whenWithdraw_thenMakeOnlyValidWithdraw() throws AccountAlreadyExistException, NegativeBalanceException, InvalidAccountIdException, NoSuchAccountException {
        accountDao.createAccount(1L, new BigDecimal("300"));
        accountDao.withdraw(1L, new BigDecimal("200"));
        Assertions.assertEquals(accountDao.getAccount(1L).getBalance(), new BigDecimal("100"));
        Assertions.assertThrows(NegativeBalanceException.class, () -> accountDao.withdraw(1L, new BigDecimal("200")));
    }

    @Test
    public void whenDeposit_thenMakeOnlyValidDeposit() throws AccountAlreadyExistException, NegativeBalanceException, InvalidAccountIdException, NoSuchAccountException {
        accountDao.createAccount(1L, new BigDecimal("300"));
        accountDao.deposit(1L, new BigDecimal("200"));
        Assertions.assertEquals(accountDao.getAccount(1L).getBalance(), new BigDecimal("500"));
    }

}
