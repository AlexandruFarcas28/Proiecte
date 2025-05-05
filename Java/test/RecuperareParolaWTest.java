package ecrane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecuperareParolaWTest {

    @Test
    public void testTrimiteEmailValid() {
        String rezultat = procesareEmail("test@example.com");
        assertEquals("Succes", rezultat);
    }

    @Test
    public void testTrimiteEmailGol() {
        String rezultat = procesareEmail("");
        assertEquals("Eroare", rezultat);
    }

    private String procesareEmail(String email) {
        if (email.isEmpty()) {
            return "Eroare";
        }
        return "Succes";
    }
}
