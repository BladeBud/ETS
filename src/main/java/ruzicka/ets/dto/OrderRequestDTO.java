package ruzicka.ets.dto;

/**
 * @author czech
 * @since 2024-10-14
 */

public class OrderRequestDTO {
    private Integer idzak;
    private Integer adresa;
    private Integer quantity;
    private String email;

    // Getters and Setters
    public Integer getIdzak() {
        return idzak;
    }

    public void setIdzak(Integer idzak) {
        this.idzak = idzak;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
