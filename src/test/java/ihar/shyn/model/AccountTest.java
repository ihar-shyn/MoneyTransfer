package ihar.shyn.model;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class AccountTest {

    @Test
    public void whenCompareAccount_thenCompareItById() {
        Account account1 = new Account(1L, new BigDecimal("300"));
        Account account2 = new Account(2L, new BigDecimal("300"));

        Assertions.assertNotEquals(account1, account2);

        Account account1Copy = new Account(1L, new BigDecimal("300"));
        account1.setBalance(new BigDecimal("400"));
        Assertions.assertEquals(account1, account1Copy);
    }
}
