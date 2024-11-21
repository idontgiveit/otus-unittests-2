package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Agreement;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplWithSpyTest {
    @Spy
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    public void testGetAccountsUsingSpy() {
        Agreement agreement = new Agreement();
        agreement.setId(1L);
        agreement.setName("testName");

        accountServiceImpl.addAccount(agreement, "1", 1, new BigDecimal(100));
        assertEquals(1L, accountServiceImpl.getAccounts().get(0).getAgreementId());
    }

}
