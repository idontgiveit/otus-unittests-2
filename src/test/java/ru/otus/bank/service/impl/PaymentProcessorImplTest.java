package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessorImplTest {

    @Mock
    AccountServiceImpl accountService;

    // --- constructor

    @InjectMocks
    PaymentProcessorImpl paymentProcessor;

    // --- makeTransfer

    @Test
    public void testTransfer() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        Account sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setType(0);
        sourceAccount.setAmount(BigDecimal.TEN);
        sourceAccount.setAgreementId(sourceAgreement.getId());

        Account destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setType(0);
        destinationAccount.setAmount(BigDecimal.ZERO);
        destinationAccount.setAgreementId(destinationAgreement.getId());

        when(accountService.getAccounts(argThat(agreement -> agreement != null && agreement.getId() == 1L)))
                .thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(agreement -> agreement != null && agreement.getId() == 2L)))
                .thenReturn(List.of(destinationAccount));

        when(accountService.makeTransfer(sourceAgreement.getId(), destinationAgreement.getId(), BigDecimal.ONE))
                .thenReturn(true);

        assertTrue(paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                0, 0, BigDecimal.ONE));
    }

    // --- makeTransferWithComission

    @Test
    public void testWithCommission() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        Account sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setType(0);
        sourceAccount.setAmount(BigDecimal.TEN);
        sourceAccount.setAgreementId(sourceAgreement.getId());

        Account destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setType(0);
        destinationAccount.setAmount(BigDecimal.ZERO);
        destinationAccount.setAgreementId(destinationAgreement.getId());

        when(accountService.getAccounts(argThat(agreement -> agreement != null && agreement.getId() == 1L)))
                .thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(agreement -> agreement != null && agreement.getId() == 2L)))
                .thenReturn(List.of(destinationAccount));

        BigDecimal commission = new BigDecimal("0.05");
        BigDecimal transfer = new BigDecimal(5);

        when(accountService.charge(sourceAccount.getId(), transfer.negate().multiply(commission)))
                .thenReturn(true);

        when(accountService.makeTransfer(sourceAgreement.getId(), destinationAgreement.getId(), transfer))
                .thenReturn(true);

        assertTrue(paymentProcessor.makeTransferWithComission(sourceAgreement, destinationAgreement,
                0, 0, transfer, commission));
    }

}
