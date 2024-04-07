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
import ru.otus.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    private final static BigDecimal SOURCE_BALANCE_AFTER_OPERATION = new BigDecimal(9);
    private final static BigDecimal DEST_BALANCE_AFTER_OPERATION = BigDecimal.ONE;
    private final static BigDecimal OPERATION_SUM = BigDecimal.ONE;

    private final Account sourceAccount = Account.builder()
            .id(1L)
            .amount(BigDecimal.TEN)
            .agreementId(1L)
            .build();
    private final Account destinationAccount = Account.builder()
            .id(2L)
            .agreementId(1L)
            .amount(BigDecimal.ZERO)
            .build();

    private final Iterable<Account> expectedAccountList = List.of(sourceAccount, destinationAccount);

    private final Agreement agreement = new Agreement(1L, "dummy");

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    public void testTransfer() {
        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(1L, 2L, OPERATION_SUM);

        assertEquals(SOURCE_BALANCE_AFTER_OPERATION, sourceAccount.getAmount());
        assertEquals(DEST_BALANCE_AFTER_OPERATION, destinationAccount.getAmount());
    }

    @Test
    public void testSourceNotFound() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        AccountException result = assertThrows(AccountException.class, () ->
                accountServiceImpl.makeTransfer(1L, 2L, BigDecimal.TEN)
        );
        assertEquals("No source account", result.getLocalizedMessage());
    }


    @Test
    public void testTransferWithVerify() {
        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(SOURCE_BALANCE_AFTER_OPERATION);

        ArgumentMatcher<Account> destinationMatcher =
                argument -> argument.getId().equals(2L) && argument.getAmount().equals(DEST_BALANCE_AFTER_OPERATION);

        accountServiceImpl.makeTransfer(1L, 2L, OPERATION_SUM);

        verify(accountDao).save(argThat(sourceMatcher));
        verify(accountDao).save(argThat(destinationMatcher));
    }

    @Test
    void charge() {
        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(SOURCE_BALANCE_AFTER_OPERATION);

        accountServiceImpl.charge(1L, OPERATION_SUM);

        verify(accountDao).save(argThat(sourceMatcher));
    }

    @Test
    void getALLAccounts() {
        when(accountDao.findAll()).thenReturn(expectedAccountList);
        var expectedAll = accountServiceImpl.getAccounts();
        assertThat(expectedAll).isEqualTo(expectedAccountList);
    }

    @Test
    void getALLAccountsById() {
        when(accountDao.findByAgreementId(1L)).thenReturn(expectedAccountList);
        var expectedById = accountServiceImpl.getAccounts(agreement);
        assertThat(expectedById).isEqualTo(expectedAccountList);
    }

    @Test
    void addAccount() {
        ArgumentMatcher<Account> savedAccountMatcher =
                account -> account.getAgreementId().equals(1L)
                        && account.getNumber().equals("111")
                        && account.getType().equals(0)
                        && account.getAmount().equals(BigDecimal.TEN);

        accountServiceImpl.addAccount(agreement, "111", 0, BigDecimal.TEN);

        verify(accountDao).save(argThat(savedAccountMatcher));
    }
}
