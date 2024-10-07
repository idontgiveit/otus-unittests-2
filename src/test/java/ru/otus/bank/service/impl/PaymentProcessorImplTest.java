package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;
import ru.otus.bank.service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessorImplTest {
    @Mock
    AccountDao accountDao;

    @Mock
    AccountService accountService;

    @InjectMocks
    PaymentProcessorImpl paymentProcessor;

    @Test
    public void testTransfer() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        Account sourceAccount = new Account();
        sourceAccount.setAmount(BigDecimal.TEN);
        sourceAccount.setType(0);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(BigDecimal.ZERO);
        destinationAccount.setType(0);

        when(accountService.getAccounts(argThat(new ArgumentMatcher<Agreement>() {
            @Override
            public boolean matches(Agreement argument) {
                return argument != null && argument.getId() == 1L;
            }
        }))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(new ArgumentMatcher<Agreement>() {
            @Override
            public boolean matches(Agreement argument) {
                return argument != null && argument.getId() == 2L;
            }
        }))).thenReturn(List.of(destinationAccount));

        paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                0, 0, BigDecimal.ONE);

    }

    @Test
    public void makeTransferWithComissionTest() {
        AccountServiceImpl accountServiceImpl = new AccountServiceImpl(accountDao);
        PaymentProcessorImpl paymentProcessorImpl = new PaymentProcessorImpl(accountServiceImpl);

        Long srcAccId = 1L;
        Long dstAccId = 2L;

        Long srcAgrId = 10L;
        Long dstAgrId = 20L;

        int sourceType = 1;
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(1000));
        sourceAccount.setType(sourceType);
        sourceAccount.setId(srcAccId);
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(srcAgrId);

        int destType = 2;
        Account destAccount = new Account();
        destAccount.setAmount(new BigDecimal(2000));
        destAccount.setType(destType);
        destAccount.setId(dstAccId);
        Agreement destAgreement = new Agreement();
        destAgreement.setId(dstAgrId);

        BigDecimal amount = new BigDecimal(500);
        BigDecimal comissionPercent = new BigDecimal(0.1);

        when(accountDao.findByAgreementId(srcAgrId)).thenReturn(List.of(sourceAccount));
        when(accountDao.findByAgreementId(dstAgrId)).thenReturn(List.of(destAccount));

        when(accountDao.findById(srcAccId)).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(dstAccId)).thenReturn(Optional.of(destAccount));

        paymentProcessorImpl.makeTransferWithComission(sourceAgreement, destAgreement, sourceType, destType, amount, comissionPercent);

        assertEquals(new BigDecimal(450), sourceAccount.getAmount());
    }

}