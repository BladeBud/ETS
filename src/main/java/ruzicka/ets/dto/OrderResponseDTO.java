package ruzicka.ets.dto;

/**
 * Data Transfer Object for returning order details in response to API calls.
 * This class encapsulates the necessary information about an order, including
 * the customer's ID, the price of the order, and the associated bank account number.
 */
public class OrderResponseDTO {
//----------------------------------------------------------------------------------------------------------------------
    private final Integer idobjednavka;
    private final Integer cena;
    private final String cisloBankovnihoUctu;
    private final String iban;
//----------------------------------------------------------------------------------------------------------------------
    // Constructor
    public OrderResponseDTO(Integer idobjednavka, Integer cena, String cisloBankovnihoUctu, String iban) {
        this.idobjednavka = idobjednavka;
        this.cena = cena;
        this.cisloBankovnihoUctu = cisloBankovnihoUctu;
        this.iban = iban;
    }
//----------------------------------------------------------------------------------------------------------------------
    // Getters
    public Integer getIdobjednavka() {
        return idobjednavka;
    }

    public Integer getCena() {
        return cena;
    }

    public String getCisloBankovnihoUctu() {
        return cisloBankovnihoUctu;
    }

    public String getIban() {
        return iban;
    }
}