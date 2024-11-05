package ruzicka.ets.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.repository.ObjednavkaRepository;
import ruzicka.ets.dto.OrderRequestDTO;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ObjednavkaServiceTest {

    @Mock
    private ObjednavkaRepository objednavkaRepository;

    @Mock
    private MistoRepository mistoRepository;

    @InjectMocks
    private ObjednavkaService objednavkaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOrdersByZakaznikId() {
        Integer zakaznikId = 1;
        List<Objednavka> objednavky = Collections.singletonList(new Objednavka());
        when(objednavkaRepository.findByIdzakaznik_Idzakaznik(zakaznikId)).thenReturn(objednavky);

        List<Objednavka> result = objednavkaService.findOrdersByZakaznikId(zakaznikId);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(objednavkaRepository, times(1)).findByIdzakaznik_Idzakaznik(zakaznikId);
    }

    @Test
    void testFindOrderById() {
        Integer orderId = 1;
        Objednavka objednavka = new Objednavka();
        when(objednavkaRepository.findById(orderId)).thenReturn(java.util.Optional.of(objednavka));

        Objednavka result = objednavkaService.findOrderById(orderId);
        assertNotNull(result);
        verify(objednavkaRepository, times(1)).findById(orderId);
    }

    @Test
    void testSave() {
        Objednavka objednavka = new Objednavka();
        when(objednavkaRepository.save(objednavka)).thenReturn(objednavka);

        Objednavka result = objednavkaService.save(objednavka);
        assertNotNull(result);
        verify(objednavkaRepository, times(1)).save(objednavka);
    }

}