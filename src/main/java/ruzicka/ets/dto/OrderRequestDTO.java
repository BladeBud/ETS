package ruzicka.ets.dto;

public class OrderRequestDTO {
    private Integer adresa;
    private Integer quantity;
    private Integer zakaznikId;

    // Getters and Setters
    public Integer getAdresa() {
        return adresa;
    }

    public void setAdresa(Integer adresa) {
        this.adresa = adresa;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getZakaznikId() {
        return zakaznikId;
    }

    public void setZakaznikId(Integer zakaznikId) {
        this.zakaznikId = zakaznikId;
    }
}
