package ruzicka.ets.dto;

/**
 * Data Transfer Object for returning order details in response to API calls.
 * This class encapsulates the necessary information about an order, including
 * the customer's ID, the price of the order, and the associated bank account number.
 */
public class OrderResponseDTO {
//----------------------------------------------------------------------------------------------------------------------
    private Integer idzak;
    private Integer cena;
    private String cisloBankovnihoUctu;
//----------------------------------------------------------------------------------------------------------------------
    // Constructor
    public OrderResponseDTO(Integer idzak, Integer cena, String cisloBankovnihoUctu) {
        this.idzak = idzak;
        this.cena = cena;
        this.cisloBankovnihoUctu = cisloBankovnihoUctu;
    }
//----------------------------------------------------------------------------------------------------------------------
    // Getters
    public Integer getIdzak() {
        return idzak;
    }

    public Integer getCena() {
        return cena;
    }

    public String getCisloBankovnihoUctu() {
        return cisloBankovnihoUctu;
    }
}