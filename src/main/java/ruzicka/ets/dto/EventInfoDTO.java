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
    private String adresa;
    private Integer avaiablequantity;
    private Integer idzakaznik;
    private Integer cena;
//----------------------------------------------------------------------------------------------------------------------
    // Getters and Setters
    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public Integer getAvaiablequantity() {
        return avaiablequantity;
    }

    public void setAvaiablequantity(Integer avaiablequantity) {
        this.avaiablequantity = avaiablequantity;
    }

    public Integer getIdzakaznik() {
        return idzakaznik;
    }

    public void setIdzakaznik(Integer idzakaznik) {
        this.idzakaznik = idzakaznik;
    }

    public Integer getCena() {
        return cena;
    }

    public void setCena(Integer cena) {
        this.cena = cena;
    }
}