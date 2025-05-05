package proiect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Manager extends Utilizator {

    private Connection connection;

    /**
     * Constructor pentru clasa Manager.
     * @param id ID-ul managerului.
     * @param numeUtilizator Numele utilizatorului.
     * @param parola Parola utilizatorului.
     * @param email Adresa de email a utilizatorului.
     * @param connection Conexiunea la baza de date.
     */
    public Manager(int id, String numeUtilizator, String parola, String email, Connection connection) {
        super(id, numeUtilizator, parola, "manager", email);
        this.connection = connection;
    }

    /**
     * Adauga un produs in baza de date.
     * @param produs Produsul care trebuie adaugat.
     */
    public void adaugaProdus(Produs produs) {
        try {
            String sql = "INSERT INTO products (name, brand, country, price, stock) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, produs.getNume());
            stmt.setString(2, produs.getMarca());
            stmt.setString(3, produs.getTaraOrigine());
            stmt.setDouble(4, produs.getPret());
            stmt.setInt(5, produs.getStock());
            stmt.executeUpdate();
            System.out.println("Produs adaugat cu succes: " + produs.getNume());
        } catch (Exception e) {
            System.err.println("Eroare la adaugarea produsului: " + e.getMessage());
        }
    }

    /**
     * Sterge un produs din baza de date folosind ID-ul sau.
     * @param idProdus ID-ul produsului care trebuie sters.
     */
    public void stergeProdus(int idProdus) {
        try {
            String sql = "DELETE FROM products WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idProdus);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produs sters cu succes!");
            } else {
                System.out.println("Produsul cu ID " + idProdus + " nu a fost gasit.");
            }
        } catch (Exception e) {
            System.err.println("Eroare la stergerea produsului: " + e.getMessage());
        }
    }

    /**
     * Actualizeaza informatiile unui produs in baza de date.
     * @param produs Produsul cu informatiile actualizate.
     */
    public void actualizeazaProdus(Produs produs) {
        try {
            String sql = "UPDATE products SET name = ?, brand = ?, country = ?, price = ?, stock = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, produs.getNume());
            stmt.setString(2, produs.getMarca());
            stmt.setString(3, produs.getTaraOrigine());
            stmt.setDouble(4, produs.getPret());
            stmt.setInt(5, produs.getStock());
            stmt.setInt(6, produs.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Produs actualizat cu succes: " + produs.getNume());
            } else {
                System.out.println("Produsul cu ID " + produs.getId() + " nu a fost gasit.");
            }
        } catch (Exception e) {
            System.err.println("Eroare la actualizarea produsului: " + e.getMessage());
        }
    }

    /**
     * Vizualizeaza toate produsele din baza de date.
     */
    public void vizualizareProduse() {
        try {
            String sql = "SELECT * FROM products";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Lista produselor:");
            while (rs.next()) {
                Produs produs = new Produs(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getString("country"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
                System.out.println(produs);
            }
        } catch (Exception e) {
            System.err.println("Eroare la vizualizarea produselor: " + e.getMessage());
        }
    }
}
