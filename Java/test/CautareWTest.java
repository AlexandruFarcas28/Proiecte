
package ecrane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CautareWTest {

    @Test
    public void testSearchProductsValidInput() {
        boolean rezultat = procesareCautareProduse("nume", "produs", 10.0, 100.0);
        assertTrue(rezultat);
    }

    @Test
    public void testSearchProductsInvalidPriceRange() {
        boolean rezultat = procesareCautareProduse("nume", "produs", 100.0, 10.0);
        assertFalse(rezultat);
    }

    @Test
    public void testSearchProductsEmptyValue() {
        boolean rezultat = procesareCautareProduse("nume", "", 10.0, 100.0);
        assertTrue(rezultat);
    }

    private boolean procesareCautareProduse(String criteria, String value, double minPrice, double maxPrice) {
        if (minPrice > maxPrice) {
            return false;
        }
        return true;
    }
}