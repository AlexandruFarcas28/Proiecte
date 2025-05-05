package proiect;

/**
 * Clasa Produs reprezinta un obiect pentru stocarea informatiilor despre un produs.
 */
public class Produs {
    private int id;
    private String nume;
    private String marca;
    private String taraOrigine;
    private double pret;
    private int stock;

    /**
     * Constructor pentru clasa Produs.
     * @param id ID-ul produsului.
     * @param nume Numele produsului.
     * @param marca Marca produsului.
     * @param taraOrigine Tara de origine a produsului.
     * @param pret Pretul produsului.
     * @param stock Stocul disponibil al produsului.
     */
    public Produs(int id, String nume, String marca, String taraOrigine, double pret, int stock) {
        this.id = id;
        this.nume = nume;
        this.marca = marca;
        this.taraOrigine = taraOrigine;
        this.pret = pret;
        this.stock = stock;
    }

    /**
     * Returneaza ID-ul produsului.
     * @return ID-ul produsului.
     */
    public int getId() {
        return id;
    }

    /**
     * Returneaza numele produsului.
     * @return Numele produsului.
     */
    public String getNume() {
        return nume;
    }

    /**
     * Returneaza marca produsului.
     * @return Marca produsului.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Returneaza tara de origine a produsului.
     * @return Tara de origine a produsului.
     */
    public String getTaraOrigine() {
        return taraOrigine;
    }

    /**
     * Returneaza pretul produsului.
     * @return Pretul produsului.
     */
    public double getPret() {
        return pret;
    }

    /**
     * Returneaza stocul disponibil al produsului.
     * @return Stocul produsului.
     */
    public int getStock() {
        return stock;
    }

    /**
     * Seteaza ID-ul produsului.
     * @param id Noul ID al produsului.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Seteaza numele produsului.
     * @param nume Noul nume al produsului.
     */
    public void setNume(String nume) {
        this.nume = nume;
    }

    /**
     * Seteaza marca produsului.
     * @param marca Noua marca a produsului.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Seteaza tara de origine a produsului.
     * @param taraOrigine Noua tara de origine.
     */
    public void setTaraOrigine(String taraOrigine) {
        this.taraOrigine = taraOrigine;
    }

    /**
     * Seteaza pretul produsului.
     * @param pret Noul pret al produsului.
     */
    public void setPret(double pret) {
        this.pret = pret;
    }

    /**
     * Seteaza stocul disponibil al produsului.
     * @param stock Noul stoc al produsului.
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Suprascrie metoda toString pentru a returna detalii despre produs.
     * @return Un string ce contine informatiile produsului.
     */
    @Override
    public String toString() {
        return "Produs{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", marca='" + marca + '\'' +
                ", taraOrigine='" + taraOrigine + '\'' +
                ", pret=" + pret +
                ", stoc=" + stock +
                '}';
    }
}