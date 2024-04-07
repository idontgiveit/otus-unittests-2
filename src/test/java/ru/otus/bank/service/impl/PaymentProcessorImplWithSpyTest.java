package ru.otus.bank.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PaymentProcessorImplWithSpyTest {
    private final Agreement sourceAgreement = new Agreement(1L, "dummy");
    private final Agreement destinationAgreement = new Agreement(2L, "dummy");
    private final Account sourceAccount = Account.builder().id(10L).type(0).amount(BigDecimal.TEN).build();
    private final Account destinationAccount = Account.builder().id(20L).type(0).amount(BigDecimal.ZERO).build();

    @Mock
    AccountDao accountDao;

    @Spy
    @InjectMocks
    AccountServiceImpl accountService;

    @InjectMocks
    PaymentProcessorImpl paymentProcessor;

    @BeforeEach
    public void init() {
        paymentProcessor = new PaymentProcessorImpl(accountService);
        doReturn(List.of(sourceAccount)).when(accountService).getAccounts(argThat(argument ->
                argument != null && argument.getId() == 1L)
        );

        doReturn(List.of(destinationAccount)).when(accountService).getAccounts(argThat(argument ->
                argument != null && argument.getId() == 2L)
        );
        when(accountDao.findById(10L)).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(20L)).thenReturn(Optional.of(destinationAccount));
    }

    @Test
    public void testTransfer() {
        paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                0, 0, BigDecimal.ONE);

        assertEquals(new BigDecimal(9), sourceAccount.getAmount());
        assertEquals(BigDecimal.ONE, destinationAccount.getAmount());
    }

    @Test
    public void testTransferWithCommission() {
        paymentProcessor.makeTransferWithCommission(sourceAgreement, destinationAgreement,
                0, 0, BigDecimal.ONE, new BigDecimal("0.1"));
        assertEquals(new BigDecimal("8.9"), sourceAccount.getAmount());
        assertEquals(new BigDecimal(1), destinationAccount.getAmount());
    }
}
