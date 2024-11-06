package ruzicka.ets.dto;

/**
 * @author czech
 * @since 2024-09-30
 */

/**
 * Data Transfer Object (DTO) class for event information.
 */
public class EventInfoDTO {
//----------------------------------------------------------------------------------------------------------------------
    private String nazev;
    private Integer avaiablequantity;
    private Integer cena;
//----------------------------------------------------------------------------------------------------------------------
    // Getters and Setters
    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public Integer getAvaiablequantity() {
        return avaiablequantity;
    }

    public void setAvaiablequantity(Integer avaiablequantity) {
        this.avaiablequantity = avaiablequantity;
    }

    public Integer getCena() {
        return cena;
    }

    public void setCena(Integer cena) {
        this.cena = cena;
    }
}