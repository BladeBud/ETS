package ruzicka.ets.dto;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author czech
 * @since 2024-10-14
 */

/**
 * Data Transfer Object for creating an order request.
 * This class holds the necessary details required for creating an order
 * such as banking details, address, quantity, email, and price.
 */
public class OrderRequestDTO {
//--------------------------------------------------------------------------------------------------------------------
    @Value("${banking.details}")
    private String bankingDetails;
    private Integer idzak;
    private Integer adresa;
    private Integer quantity;
    private String email;
    private int cena;
//----------------------------------------------------------------------------------------------------------------------

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

    public String getBankingDetails() {
        return bankingDetails;
    }

    public OrderRequestDTO setBankingDetails(String bankingDetails) {
        this.bankingDetails = bankingDetails;
        return this;
    }

    public int getCena() {
        return cena;
    }

    public OrderRequestDTO setCena(int cena) {
        this.cena = cena;
        return this;
    }
}
