package ru.otus.bank.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.otus.bank.dao.AgreementDao;
import ru.otus.bank.entity.Agreement;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AgreementServiceImplTest {

    private AgreementDao dao = mock(AgreementDao.class);

    AgreementServiceImpl agreementServiceImpl;

    // --- constructor

    @BeforeEach
    public void init() {
        agreementServiceImpl = new AgreementServiceImpl(dao);
    }

    // --- findByName

    @Test
    public void testFindByName() {
        String name = "test";
        Agreement agreement = new Agreement();
        agreement.setId(10L);
        agreement.setName(name);

        when(dao.findByName(name)).thenReturn(
                Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(name);

        assertTrue(result.isPresent());
        assertEquals(10, agreement.getId());
    }

    @Test
    public void testFindByNameWithCaptor() {
        String name = "test";
        Agreement agreement = new Agreement();
        agreement.setId(10L);
        agreement.setName(name);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(dao.findByName(captor.capture())).thenReturn(
                Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(name);

        assertEquals("test", captor.getValue());
        assertTrue(result.isPresent());
        assertEquals(10, agreement.getId());
    }

    // --- addAgreement

    @Test
    public void testCreateAgreement() {
        String name = "test";
        Agreement agreement = new Agreement();
        agreement.setId(11L);
        agreement.setName(name);

        when(dao.save(any(Agreement.class))).thenReturn(agreement);

        Agreement result = agreementServiceImpl.addAgreement(name);

        assertNotNull(result);
        assertEquals(11L, result.getId());
    }

}
