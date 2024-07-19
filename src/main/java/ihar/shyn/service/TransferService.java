package ihar.shyn.service;

import java.math.BigDecimal;

public interface TransferService {

    void transferMoney(Long accountFrom, Long accountTo, BigDecimal amount);
}
