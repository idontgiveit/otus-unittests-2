package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.otus.bank.dao.AgreementDao;
import ru.otus.bank.entity.Agreement;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class AgreementServiceImplTest {
    private final static String NAME = "test";
    private final Agreement agreement = new Agreement(10L, NAME);
    private final AgreementDao dao = Mockito.mock(AgreementDao.class);

    AgreementServiceImpl agreementServiceImpl;

    @BeforeEach
    public void init() {
        agreementServiceImpl = new AgreementServiceImpl(dao);
    }

    @Test
    public void testFindByName() {
        when(dao.findByName(NAME)).thenReturn(Optional.of(agreement));

        var result = agreementServiceImpl.findByName(NAME);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(10, agreement.getId());
    }

    @Test
    public void testFindByNameWithCaptor() {
        var captor = ArgumentCaptor.forClass(String.class);

        when(dao.findByName(captor.capture())).thenReturn(Optional.of(agreement));

        var result = agreementServiceImpl.findByName(NAME);

        Assertions.assertEquals(NAME, captor.getValue());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(10, agreement.getId());
    }

    @Test
    public void testAddArgument() {
        var captor = ArgumentCaptor.forClass(Agreement.class);

        when(dao.save(captor.capture())).thenReturn(agreement);

        var actualSavedAgreement = agreementServiceImpl.addAgreement(NAME);

        Assertions.assertEquals(10, actualSavedAgreement.getId());
        Assertions.assertEquals(NAME, actualSavedAgreement.getName());
    }
}
