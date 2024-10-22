package ruzicka.ets.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ruzicka.ets.db.Objednavka;
import ruzicka.ets.db.misto;
import ruzicka.ets.dto.OrderRequestDTO;
import ruzicka.ets.repository.MistoRepository;
import ruzicka.ets.repository.ObjednavkaRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ObjednavkaServiceTest {

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
    void testCreateOrder_Success() {
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setAdresa(1);
        orderRequest.setQuantity(1);

        misto availableMisto = new misto();
        when(mistoRepository.findByAdresaAndAvailableQuantity(any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.singletonList(availableMisto));

        when(objednavkaRepository.save(any(Objednavka.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Objednavka result = objednavkaService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals("R", result.getStatus());
        verify(objednavkaRepository, times(1)).save(any(Objednavka.class));
    }

    @Test
    void testCreateOrder_AddressReserved() {
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setAdresa(1);
        orderRequest.setQuantity(1);

        when(objednavkaRepository.findByIdmisto_AdresaAndStatus(any(Integer.class), eq("R")))
                .thenReturn(Collections.singletonList(new Objednavka()));

        Objednavka result = objednavkaService.createOrder(orderRequest);

        assertNull(result);
        verify(objednavkaRepository, never()).save(any(Objednavka.class));
    }

    @Test
    void testCreateOrder_QuantityNotAvailable() {
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setAdresa(1);
        orderRequest.setQuantity(1);

        when(mistoRepository.findByAdresaAndAvailableQuantity(any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.emptyList());

        Objednavka result = objednavkaService.createOrder(orderRequest);

        assertNull(result);
        verify(objednavkaRepository, never()).save(any(Objednavka.class));
    }
}