package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    // --- makeTransfer

    @Test
    public void testTransfer() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        assertEquals(new BigDecimal(90), sourceAccount.getAmount());
        assertEquals(new BigDecimal(20), destinationAccount.getAmount());
    }

    @Test
    public void testSourceNotFound() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        AccountException result = assertThrows(AccountException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));
            }
        });
        assertEquals("No source account", result.getLocalizedMessage());
    }


    @Test
    public void testTransferWithVerify() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));
        sourceAccount.setId(1L);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));
        destinationAccount.setId(2L);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(new BigDecimal(90));

        ArgumentMatcher<Account> destinationMatcher =
                argument -> argument.getId().equals(2L) && argument.getAmount().equals(new BigDecimal(20));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        verify(accountDao).save(argThat(sourceMatcher));
        verify(accountDao).save(argThat(destinationMatcher));
    }

    @Test
    public void testEnoughAmounts() {
        final Account account = new Account();
        account.setAmount(BigDecimal.ZERO);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(account));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(account));

        assertFalse(accountServiceImpl.makeTransfer(1L, 2L, BigDecimal.ZERO));
        assertFalse(accountServiceImpl.makeTransfer(1L, 2L, BigDecimal.TEN));
    }

    // --- getAccounts, getAccounts(Agreement)

    @Test
    public void testAccountsGotten() {
        final Agreement agreement = new Agreement();
        agreement.setId(1L);

        final Account account1 = new Account();
        account1.setId(1L);
        account1.setAgreementId(agreement.getId());

        final Account account2 = new Account();
        account2.setId(2L);
        account2.setAgreementId(2L);

        final List<Account> accounts = List.of(account1, account2);

        when(accountDao.findAll()).thenReturn(accounts);
        when(accountDao.findByAgreementId(eq(agreement.getId()))).thenReturn(List.of(account1));

        assertEquals(2, accountServiceImpl.getAccounts().size());

        final List<Account> list = accountServiceImpl.getAccounts(agreement);
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).getAgreementId());
    }

    // --- charge

    @Test
    public void testChargeSuccessful() {
        final Account account = new Account();
        account.setId(1L);
        account.setAmount(BigDecimal.ZERO);

        when(accountDao.findById(eq(account.getId()))).thenReturn(Optional.of(account));

        final ArgumentMatcher<Account> destinationMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(BigDecimal.TEN.negate());

        assertTrue(accountServiceImpl.charge(account.getId(), BigDecimal.TEN));

        verify(accountDao).save(argThat(destinationMatcher));
    }

    // --- addAccount

    @Test
    public void testCreateAccountSuccessful() {
        final Agreement agreement = new Agreement();
        agreement.setId(1L);

        final Account account = new Account();
        account.setId(1L);
        account.setAgreementId(agreement.getId());
        account.setNumber("number");
        account.setType(1);
        account.setAmount(BigDecimal.TEN);

        final ArgumentMatcher<Account> accountMatcher =
                argument -> argument.getId().equals(0L)
                        && argument.getAgreementId().equals(agreement.getId())
                        && argument.getNumber().equals(account.getNumber())
                        && argument.getType().equals(account.getType())
                        && argument.getAmount().equals(account.getAmount());

        when(accountDao.save(any(Account.class))).thenReturn(account);

        final Account result = accountServiceImpl.addAccount(
                agreement,
                account.getNumber(),
                account.getType(),
                account.getAmount()
        );

        verify(accountDao).save(argThat(accountMatcher));

        assertEquals(1L, result.getId());
    }
}
