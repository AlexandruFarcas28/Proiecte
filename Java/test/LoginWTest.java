package ecrane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginWTest {

    @Test
    public void testAutentificareUtilizatorValid() {
        boolean rezultat = autentificareUtilizator("user", "password");
        assertTrue(rezultat);
    }

    @Test
    public void testAutentificareUtilizatorInvalid() {
        boolean rezultat = autentificareUtilizator("user", "wrongpassword");
        assertFalse(rezultat);
    }

    @Test
    public void testAutentificareUtilizatorGol() {
        boolean rezultat = autentificareUtilizator("", "");
        assertFalse(rezultat);
    }

    private boolean autentificareUtilizator(String username, String password) {
        if (username.equals("user") && password.equals("password")) {
            return true;
        }
        return false;
    }
}
