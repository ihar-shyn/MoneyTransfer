package ihar.shyn.service;

import ihar.shyn.dao.AccountDao;
import ihar.shyn.dao.AccountDaoImpl;
import ihar.shyn.exception.InvalidOperationException;
import ihar.shyn.exception.NegativeBalanceException;
import ihar.shyn.exception.NoSuchAccountException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransferServiceTest {

    TransferService service;
    AccountDao accountDao;

    @BeforeEach
    public void init() {
        accountDao = new AccountDaoImpl();
        service = new TransferServiceImpl(accountDao);
    }

    @AfterEach
    public void clean() {
        accountDao = null;
        service = null;
    }

    @Test
    void whenMakeTransfer_thenMakeOnlyValidTransfer() {
        long accountId1 = accountDao.createAccount(new BigDecimal("300")).getId();
        long accountId2 = accountDao.createAccount(new BigDecimal("500")).getId();

        Assertions.assertThrows(InvalidOperationException.class, () -> service.transferMoney(accountId1, accountId1, new BigDecimal("100")));
        service.transferMoney(accountId1, accountId2, new BigDecimal("100"));
        Assertions.assertEquals(accountDao.getAccount(accountId1).getBalance(), new BigDecimal("200"));
        Assertions.assertEquals(accountDao.getAccount(accountId2).getBalance(), new BigDecimal("600"));
        Assertions.assertThrows(NegativeBalanceException.class, () -> service.transferMoney(accountId2, accountId1, new BigDecimal("1000")));
        Assertions.assertEquals(accountDao.getAccount(accountId1).getBalance(), new BigDecimal("200"));
        Assertions.assertEquals(accountDao.getAccount(accountId2).getBalance(), new BigDecimal("600"));
        Assertions.assertThrows(NoSuchAccountException.class, () -> service.transferMoney(accountId1, -1L, new BigDecimal("100")));
    }

    @Test
    void whenMakeTransfersSimultaneously_thenResultBalanceIsCorrect() throws InterruptedException {
        long account1ID = accountDao.createAccount(new BigDecimal("1000")).getId();
        long account2Id = accountDao.createAccount(new BigDecimal("1000")).getId();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                service.transferMoney(account1ID, account2Id, new BigDecimal("1"));
            }
        });

        executor.submit(() -> {
            for (int i = 0; i < 500; i++) {
                service.transferMoney(account2Id, account1ID, new BigDecimal("1"));
            }
        });

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        Assertions.assertEquals(accountDao.getAccount(account1ID).getBalance(), new BigDecimal("500"));
        Assertions.assertEquals(accountDao.getAccount(account2Id).getBalance(), new BigDecimal("1500"));
    }

}
