package ruzicka.ets.dto;

/**
 * @author czech
 * @since 2024-09-30
 */
public class OrderResponseDTO {

    private Integer idzakzanika;
    private Integer adresa;
    private Integer avaiablequantity;
    private Double cena;

    public Integer getIdzakzanika() {
        return idzakzanika;
    }

    public void setIdzakzanika(Integer idzakzanika) {
        this.idzakzanika = idzakzanika;
    }

    public Integer getAdresa() {
        return adresa;
    }

    public void setAdresa(Integer adresa) {
        this.adresa = adresa;
    }

    public Integer getAvaiablequantity() {
        return avaiablequantity;
    }

    public void setAvaiablequantity(Integer avaiablequantity) {
        this.avaiablequantity = avaiablequantity;
    }

    public Double getCena() {
        return cena;
    }

    public void setCena(Double cena) {
        this.cena = cena;
    }
}
