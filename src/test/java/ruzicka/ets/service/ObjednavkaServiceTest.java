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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * @author czech
 * @since 2024-10-14
 */
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
    void testCreateOrder_AddressAlreadyReserved() {
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setAdresa(1);
        orderRequest.setQuantity(5);

        // Mock the behavior of the repository
        when(objednavkaRepository.findByAdresaAndStatus(anyInt(), any(String.class)))
                .thenReturn(Collections.singletonList(new Objednavka()));

        boolean result = objednavkaService.createOrder(orderRequest);

        assertFalse(result);
    }

    @Test
    void testCreateOrder_QuantityNotAvailable() {
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setAdresa(1);
        orderRequest.setQuantity(5);

        // Mock the behavior of the repository
        when(objednavkaRepository.findByAdresaAndStatus(anyInt(), any(String.class)))
                .thenReturn(Collections.emptyList());
        when(mistoRepository.findByAdresaAndAvailableQuantity(anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        boolean result = objednavkaService.createOrder(orderRequest);

        assertFalse(result);
    }

    @Test
    void testCreateOrder_Success() {
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setAdresa(1);
        orderRequest.setQuantity(5);

        misto availableMisto = new misto();

        // Mock the behavior of the repository
        when(objednavkaRepository.findByAdresaAndStatus(anyInt(), any(String.class)))
                .thenReturn(Collections.emptyList());
        when(mistoRepository.findByAdresaAndAvailableQuantity(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(availableMisto));
        when(objednavkaRepository.save(any(Objednavka.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = objednavkaService.createOrder(orderRequest);

        assertTrue(result);
    }
}