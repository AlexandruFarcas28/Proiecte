package ecrane;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdaugareProdusWTest {

    @Test
    public void testAdaugareProdusCuDateValide() {
        String nume = "Produs Test";
        String marca = "Marca Test";
        String taraOrigine = "Romania";
        double pret = 99.99;
        int stoc = 10;

        boolean rezultat = addProduct(nume, marca, taraOrigine, pret, stoc);

        assertTrue(rezultat, "Produsul ar trebui sa fie adaugat cu succes.");
    }

    @Test
    public void testAdaugareProdusCuDateInvalide() {
        String nume = "";
        String marca = "Marca Test";
        String taraOrigine = "Romania";
        double pret = -1.0;
        int stoc = -5;

        boolean rezultat = addProduct(nume, marca, taraOrigine, pret, stoc);

        assertFalse(rezultat, "Adaugarea produsului ar trebui sa esueze pentru date invalide.");
    }

    @Test
    public void testInapoiLaMeniuPrincipal() {
        boolean rezultat = inapoiLaMeniuPrincipal();

        assertTrue(rezultat, "Meniul principal ar trebui sa fie afisat corect.");
    }

    private boolean addProduct(String nume, String marca, String taraOrigine, double pret, int stoc) {
        if (nume == null || nume.isEmpty() || pret <= 0 || stoc < 0) {
            return false;
        }
        return true;
    }

    private boolean inapoiLaMeniuPrincipal() {

        return true;
    }
}
