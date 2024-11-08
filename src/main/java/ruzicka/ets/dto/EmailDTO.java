package ruzicka.ets.dto;

/**
 * @author ruzicka
 * @since 2024-11-08
 */
public class EmailDTO {
//----------------------------------------------------------------------------------------------------------------------
    private String email;

    public EmailDTO() {
    }

    public EmailDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
