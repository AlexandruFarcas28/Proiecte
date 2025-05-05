package proiect;

/**
 * Clasa Utilizator reprezinta un model pentru un utilizator in sistem.
 * Contine informatii precum ID-ul, numele de utilizator, parola, rolul si email-ul.
 */
public class Utilizator {
    private int id;
    private String numeUtilizator;
    private String parola;
    private String rol;
    private String email;

    /**
     * Constructor pentru initializarea unui obiect Utilizator.
     * 
     * @param id            ID-ul utilizatorului
     * @param numeUtilizator Numele de utilizator
     * @param parola        Parola utilizatorului
     * @param rol           Rolul utilizatorului
     * @param email         Email-ul utilizatorului
     */
    public Utilizator(int id, String numeUtilizator, String parola, String rol, String email) {
        this.id = id;
        this.numeUtilizator = numeUtilizator;
        this.parola = parola;
        this.rol = rol;
        this.email = email;
    }

    /**
     * Returneaza ID-ul utilizatorului.
     * 
     * @return ID-ul utilizatorului
     */
    public int getId() {
        return id;
    }

    /**
     * Seteaza ID-ul utilizatorului.
     * 
     * @param id ID-ul utilizatorului
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returneaza numele de utilizator.
     * 
     * @return Numele de utilizator
     */
    public String getNumeUtilizator() {
        return numeUtilizator;
    }

    /**
     * Seteaza numele de utilizator.
     * 
     * @param numeUtilizator Numele de utilizator
     */
    public void setNumeUtilizator(String numeUtilizator) {
        this.numeUtilizator = numeUtilizator;
    }

    /**
     * Returneaza parola utilizatorului.
     * 
     * @return Parola utilizatorului
     */
    public String getParola() {
        return parola;
    }

    /**
     * Seteaza parola utilizatorului.
     * 
     * @param parola Parola utilizatorului
     */
    public void setParola(String parola) {
        this.parola = parola;
    }

    /**
     * Returneaza rolul utilizatorului.
     * 
     * @return Rolul utilizatorului
     */
    public String getRol() {
        return rol;
    }

    /**
     * Seteaza rolul utilizatorului.
     * 
     * @param rol Rolul utilizatorului
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Returneaza email-ul utilizatorului.
     * 
     * @return Email-ul utilizatorului
     */
    public String getEmail() {
        return email;
    }

    /**
     * Seteaza email-ul utilizatorului.
     * 
     * @param email Email-ul utilizatorului
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Metoda pentru resetarea parolei utilizatorului.
     * 
     * @param parolaVeche Parola curenta a utilizatorului
     * @param parolaNoua  Noua parola pe care utilizatorul doreste sa o seteze
     * @return True daca parola a fost schimbata cu succes, false in caz contrar
     */
    public boolean reseteazaParola(String parolaVeche, String parolaNoua) {
        if (this.parola.equals(parolaVeche)) {
            this.parola = parolaNoua;
            return true;
        } else {
            System.out.println("Parola veche este incorecta.");
            return false;
        }
    }

    /**
     * Returneaza detalii despre utilizator intr-un format usor de citit.
     * 
     * @return Un string ce contine detaliile utilizatorului (ID, nume utilizator, rol si email)
     */
    public String detaliiUtilizator() {
        return "ID: " + id + ", Nume Utilizator: " + numeUtilizator + ", Rol: " + rol + ", Email: " + email;
    }
}
