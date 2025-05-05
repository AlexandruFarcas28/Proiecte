package proiect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHandler {

    private static DatabaseHandler instance;
    private Connection connection;

    /**
     * Constructor privat pentru implementarea Singleton.
     */
    private DatabaseHandler() {
        try {
            String url = "jdbc:sqlite:Baza_de_date_Proiect.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Conexiune la baza de date realizata cu succes!");
        } catch (SQLException e) {
            System.err.println("Eroare la conectarea bazei de date: " + e.getMessage());
        }
    }

    /**
     * Returneaza instanta unica a clasei DatabaseHandler.
     * @return Instanta DatabaseHandler.
     */
    public static DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    /**
     * Returneaza conexiunea curenta la baza de date.
     * @return Conexiunea curenta.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Executa o interogare parametrizata.
     * @param query Interogarea SQL.
     * @param parameter Parametrul interogarii.
     * @return Rezultatul interogarii.
     */
    public ResultSet executePreparedQuery(String query, String parameter) {
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, parameter);
            return stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Eroare la executarea interogarii: " + e.getMessage());
            return null;
        }
    }

    /**
     * Executa o interogare fara parametri.
     * @param query Interogarea SQL.
     * @return Rezultatul interogarii.
     */
    public ResultSet executeQuery(String query) {
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Eroare la executarea interogarii: " + e.getMessage());
            return null;
        }
    }

    /**
     * Actualizeaza parola unui utilizator.
     * @param userId ID-ul utilizatorului.
     * @param newPassword Noua parola.
     * @return True daca actualizarea a reusit, altfel false.
     */
    public boolean updatePassword(int userId, String newPassword) {
        try {
            String query = "UPDATE utilizatori SET parola = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea parolei: " + e.getMessage());
            return false;
        }
    }

    /**
     * Creeaza un cont pentru un nou client.
     * @param nume Numele utilizatorului.
     * @param email Adresa de email.
     * @param parola Parola utilizatorului.
     * @return True daca inregistrarea a reusit, altfel false.
     */
    public boolean registerNewClient(String nume, String email, String parola) {
        try {
            String query = "INSERT INTO utilizatori (nume_utilizator, parola, rol, email) VALUES (?, ?, 'client', ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nume);
            stmt.setString(2, parola);
            stmt.setString(3, email);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Cont creat cu succes pentru utilizatorul: " + nume);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Eroare SQL la crearea contului: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Eroare neasteptata la crearea contului: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Adauga un produs nou in baza de date.
     * @param nume Numele produsului.
     * @param marca Marca produsului.
     * @param taraOrigine Tara de origine a produsului.
     * @param pret Pretul produsului.
     * @param stoc Cantitatea disponibila in stoc.
     * @return True daca produsul a fost adaugat, altfel false.
     */
    public boolean addProduct(String nume, String marca, String taraOrigine, double pret, int stoc) {
        try {
            String query = "INSERT INTO produse (nume, marca, taraOrigine, pret, stoc) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nume);
            stmt.setString(2, marca);
            stmt.setString(3, taraOrigine);
            stmt.setDouble(4, pret);
            stmt.setInt(5, stoc);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la adaugarea produsului: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Sterge un produs din baza de date.
     * @param id ID-ul produsului.
     * @return True daca produsul a fost sters, altfel false.
     */
    public boolean deleteProduct(int id) {
        try {
            String query = "DELETE FROM produse WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la stergerea produsului: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtine un produs din baza de date folosind ID-ul sau.
     * @param id ID-ul produsului.
     * @return Obiectul Produs daca este gasit, altfel null.
     */
    public Produs getId(int id) {
        try {
            String query = "SELECT * FROM produse WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Produs(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getString("marca"),
                        rs.getString("taraOrigine"),
                        rs.getDouble("pret"),
                        rs.getInt("stoc")
                );
            }
        } catch (SQLException e) {
            System.err.println("Eroare la cautarea produsului: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualizeaza informatiile unui produs in baza de date.
     * @param id ID-ul produsului.
     * @param nume Numele nou al produsului.
     * @param marca Marca noua a produsului.
     * @param taraOrigine Tara de origine noua.
     * @param pret Pretul nou.
     * @param stoc Stocul nou.
     * @return True daca actualizarea a reusit, altfel false.
     */
    public boolean updateProduct(int id, String nume, String marca, String taraOrigine, double pret, int stoc) {
        try {
            String query = "UPDATE produse SET nume = ?, marca = ?, taraOrigine = ?, pret = ?, stoc = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nume);
            stmt.setString(2, marca);
            stmt.setString(3, taraOrigine);
            stmt.setDouble(4, pret);
            stmt.setInt(5, stoc);
            stmt.setInt(6, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Eroare la actualizarea produsului: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inchide conexiunea la baza de date.
     */
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Eroare la inchiderea conexiunii: " + e.getMessage());
        }
    }
}
