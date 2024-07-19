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
import java.util.concurrent.Executor;
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
        accountDao.createAccount(1L, new BigDecimal("300"));
        accountDao.createAccount(2L, new BigDecimal("500"));

        Assertions.assertThrows(InvalidOperationException.class, () -> service.transferMoney(1L, 1L, new BigDecimal("100")));
        service.transferMoney(1L, 2L, new BigDecimal("100"));
        Assertions.assertEquals(accountDao.getAccount(1L).getBalance(), new BigDecimal("200"));
        Assertions.assertEquals(accountDao.getAccount(2L).getBalance(), new BigDecimal("600"));
        Assertions.assertThrows(NegativeBalanceException.class, () -> service.transferMoney(2L, 1L, new BigDecimal("1000")));
        Assertions.assertEquals(accountDao.getAccount(1L).getBalance(), new BigDecimal("200"));
        Assertions.assertEquals(accountDao.getAccount(2L).getBalance(), new BigDecimal("600"));
        Assertions.assertThrows(NoSuchAccountException.class, () -> service.transferMoney(1L, 3L, new BigDecimal("100")));
    }

    @Test
    void whenMakeTransfersSimultaneously_thenResultBalanceIsCorrect() throws InterruptedException {
        accountDao.createAccount(1L, new BigDecimal("1000"));
        accountDao.createAccount(2L, new BigDecimal("1000"));
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                service.transferMoney(1L, 2L, new BigDecimal("1"));
            }
        });

        executor.submit(() -> {
            for (int i = 0; i < 500; i++) {
                service.transferMoney(2L, 1L, new BigDecimal("1"));
            }
        });

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        Assertions.assertEquals(accountDao.getAccount(1L).getBalance(), new BigDecimal("500"));
        Assertions.assertEquals(accountDao.getAccount(2L).getBalance(), new BigDecimal("1500"));


    }

}
