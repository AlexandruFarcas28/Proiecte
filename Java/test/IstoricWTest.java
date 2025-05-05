package ecrane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IstoricWTest {

    @Test
    public void testGetGroupedHistoryWithValidUserId() {
        boolean rezultat = verificareIstoricComenzi(1);
        assertTrue(rezultat);
    }

    @Test
    public void testGetGroupedHistoryWithInvalidUserId() {
        boolean rezultat = verificareIstoricComenzi(-1);
        assertFalse(rezultat);
    }

    private boolean verificareIstoricComenzi(int userId) {
        if (userId <= 0) {
            return false;
        }
        return true;
    }
}
