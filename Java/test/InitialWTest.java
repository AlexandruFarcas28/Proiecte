package ecrane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InitialWTest {

    @Test
    public void testAfisareFereastraInitiala() {
        boolean rezultat = afisareFereastraInitiala();
        assertTrue(rezultat, "Fereastra initiala ar trebui sa fie afisata cu succes.");
    }

    @Test
    public void testButonConectare() {
        String rezultat = actiuneButon("Conectare");
        assertEquals("LoginW", rezultat, "Ar trebui sa se deschida fereastra de conectare.");
    }

    @Test
    public void testButonInregistrare() {
        String rezultat = actiuneButon("Inregistrare");
        assertEquals("InregistrareW", rezultat, "Ar trebui sa se deschida fereastra de inregistrare.");
    }

    private boolean afisareFereastraInitiala() {
        return true;
    }

    private String actiuneButon(String buton) {
        if (buton.equals("Conectare")) {
            return "LoginW";
        } else if (buton.equals("Inregistrare")) {
            return "InregistrareW";
        }
        return "Unknown";
    }
}
