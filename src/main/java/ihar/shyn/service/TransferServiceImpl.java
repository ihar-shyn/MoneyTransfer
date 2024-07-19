package ihar.shyn.service;

import ihar.shyn.dao.AccountDao;
import ihar.shyn.exception.InvalidOperationException;
import ihar.shyn.model.Account;

import java.math.BigDecimal;

public class TransferServiceImpl implements TransferService {

    private final AccountDao accountDao;

    public TransferServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transferMoney(Long accountFrom, Long accountTo, BigDecimal amount) {
        if (accountFrom.equals(accountTo)) {
            throw new InvalidOperationException(accountFrom, accountTo, amount);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException(accountFrom, accountTo, amount);
        }


        Account sender = accountDao.getAccount(accountFrom);
        Account receiver = accountDao.getAccount(accountTo);

        Object innerLock = sender.getId() > receiver.getId() ? receiver : sender;
        Object outerLock = sender.getId() > receiver.getId() ? sender : receiver;

        synchronized (outerLock) {
            synchronized (innerLock) {
                BigDecimal senderBalanceBefore = sender.getBalance();
                BigDecimal receiverBalanceBefore = receiver.getBalance();
                try {
                    accountDao.withdraw(sender.getId(), amount);
                    accountDao.deposit(receiver.getId(), amount);
                } catch (Exception e) {
                    sender.setBalance(senderBalanceBefore);
                    receiver.setBalance(receiverBalanceBefore);
                    throw e;
                }
            }
        }





    }
}
