package ruzicka.ets.dto;

import java.util.List;

/**
 * @author ruzicka
 * Data Transfer Object (DTO) class for order request.
 */
public class OrderRequestDTO {
//--------------------------------------------------------------------------------
    private List<String> nazvy; // List of table names
    private List<Integer> quantities; // List of quantities for each table
    private String mail;

//--------------------------------------------------------------------------------
    // Getters and Setters
    public List<String> getNazvy() {
        return nazvy;
    }

    public void setNazvy(List<String> nazvy) {
        this.nazvy = nazvy;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }
}