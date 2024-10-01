package ruzicka.ets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ruzicka.ets.db.misto;
import ruzicka.ets.dto.EventInfoDTO;
import ruzicka.ets.repository.MistoRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author czech
 * @since 2024-09-30
 */



@RestController
public class OrderController {


    @Autowired
    private MistoRepository mistoRepository;

    @GetMapping("/event-info")
    public List<EventInfoDTO> getEventInfo(@RequestParam Integer adresa, @RequestParam Integer quantityavaiable) {
        List<misto> mistoList = mistoRepository.findByAdresaAndAvaiablequantity(adresa, quantityavaiable);
        return mistoList.stream().map(misto -> {
            EventInfoDTO response = new EventInfoDTO();
            response.setAdresa(misto.getAdresa());
            response.setAvaiablequantity(misto.getAvaiablequantity());
//            response.setIdzakaznik(objednavka.getIdzakaznik());
            response.setCena(misto.getIdtypmista().getCena());
            return response;
        }).collect(Collectors.toList());
    }
}
