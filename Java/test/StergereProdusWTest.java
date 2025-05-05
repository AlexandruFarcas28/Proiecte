package ecrane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StergereProdusWTest {

    @Test
    public void testStergereProdusValid() {
        String rezultat = procesareStergereProdus("123");
        assertEquals("Succes", rezultat);
    }

    @Test
    public void testStergereProdusInvalid() {
        String rezultat = procesareStergereProdus("abc");
        assertEquals("Eroare", rezultat);
    }

    @Test
    public void testStergereProdusNegasit() {
        String rezultat = procesareStergereProdus("999");
        assertEquals("Eroare", rezultat);
    }

    private String procesareStergereProdus(String idInput) {
        try {
            int id = Integer.parseInt(idInput);
            if (id == 123) {
                return "Succes";
            }
            return "Eroare";
        } catch (NumberFormatException e) {
            return "Eroare";
        }
    }
}
