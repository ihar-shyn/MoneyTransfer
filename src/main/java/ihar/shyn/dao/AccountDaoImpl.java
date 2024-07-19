package ihar.shyn.dao;

import ihar.shyn.exception.AccountAlreadyExistException;
import ihar.shyn.exception.InvalidAccountIdException;
import ihar.shyn.exception.NegativeBalanceException;
import ihar.shyn.exception.NoSuchAccountException;
import ihar.shyn.model.Account;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccountDaoImpl implements AccountDao{

    private final ConcurrentMap<Long, Account> storage = new ConcurrentHashMap<>();

    @Override
    public Account createAccount(Long accountId, BigDecimal initialBalance) {
        if (accountId < 1L) {
            throw new InvalidAccountIdException(accountId);
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeBalanceException(accountId);
        }

        synchronized (this) {
            if (storage.containsKey(accountId)) {
                throw new AccountAlreadyExistException(accountId);
            }
            Account account = new Account(accountId, initialBalance);
            storage.put(accountId, account);
            return account;
        }
    }

    @Override
    public Account getAccount(Long id) {
        Account account = storage.get(id);
        if (account == null) {
            throw new NoSuchAccountException(id);
        }

        return account;
    }

    @Override
    public void withdraw(Long accountId, BigDecimal amount) {
        if (!storage.containsKey(accountId)) {
            throw new NoSuchAccountException(accountId);
        }

        Account account = storage.get(accountId);
        BigDecimal accountBalance = account.getBalance();
        if (accountBalance.compareTo(amount) < 0) {
            throw new NegativeBalanceException(accountId);
        }
        account.setBalance(accountBalance.subtract(amount));
    }

    @Override
    public void deposit(Long accountId, BigDecimal amount) {
        if (!storage.containsKey(accountId)) {
            throw new NoSuchAccountException(accountId);
        }

        Account account = storage.get(accountId);
        BigDecimal accountBalance = account.getBalance();
        account.setBalance(accountBalance.add(amount));
    }
}