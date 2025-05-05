package ecrane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InregistrareWTest {

    @Test
    public void testValidEmail() {
        boolean rezultat = isValidEmail("test@example.com");
        assertTrue(rezultat);
    }

    @Test
    public void testInvalidEmail() {
        boolean rezultat = isValidEmail("invalid-email");
        assertFalse(rezultat);
    }

    @Test
    public void testValidPassword() {
        boolean rezultat = isValidPassword("Passw0rd!2");
        assertTrue(rezultat);
    }

    @Test
    public void testInvalidPassword() {
        boolean rezultat = isValidPassword("weak");
        assertFalse(rezultat);
    }

    @Test
    public void testEmptyFields() {
        boolean rezultat = validateFields("", "", "");
        assertFalse(rezultat);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=(.*\\d.*){2})(?=(.*[a-zA-Z].*){4})(?=(.*[A-Z].*))(?=(.*[!@#$%^&*,.?]).*)[a-zA-Z0-9!@#$%^&*,.?]{8,}$";
        return password.matches(passwordRegex);
    }

    private boolean validateFields(String nume, String email, String parola) {
        return !nume.isEmpty() && !email.isEmpty() && !parola.isEmpty();
    }
}
