package ruzicka.ets.dto;

public class OrderRequestDTO {
    private String nazev;
    private Integer quantity;
    private Integer zakaznikId;
    private String mail;

    // Getters and Setters
    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
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
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
       return mail;
    }
}
