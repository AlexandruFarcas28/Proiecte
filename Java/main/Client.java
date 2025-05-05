package proiect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Client extends Utilizator {
    private Connection connection;

    /**
     * Constructor pentru clasa Client.
     * @param id ID-ul clientului.
     * @param numeUtilizator Numele utilizatorului.
     * @param parola Parola utilizatorului.
     * @param email Adresa de email a utilizatorului.
     * @param connection Conexiunea la baza de date.
     */
    public Client(int id, String numeUtilizator, String parola, String email, Connection connection) {
        super(id, numeUtilizator, parola, "client", email);
        this.connection = connection;
    }

    /**
     * Adauga un produs in cosul clientului.
     * @param produs Produsul care trebuie adaugat in cos.
     */
    public void adaugaInCos(Produs produs) {
        try {
            String sql = "INSERT INTO cart (user_id, product_id) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, getId());
            stmt.setInt(2, produs.getId());
            stmt.executeUpdate();
            System.out.println("Produs adaugat in cos: " + produs.getNume());
        } catch (Exception e) {
            System.err.println("Eroare la adaugarea produsului in cos: " + e.getMessage());
        }
    }

    /**
     * Vizualizeaza produsele din cosul clientului.
     */
    public void vizualizareCos() {
        try {
            String sql = """
                    SELECT p.id, p.nume, p.marca, p.taraOrigine, p.pret, p.stoc 
                    FROM produse p 
                    JOIN cart c ON p.id = c.product_id 
                    WHERE c.user_id = ?
                    """;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, getId());
            ResultSet rs = stmt.executeQuery();

            boolean esteGol = true;
            System.out.println("Produse in cos:");
            while (rs.next()) {
                esteGol = false;
                System.out.println("Produs{id=" + rs.getInt("id") + ", nume='" + rs.getString("nume") +
                        "', marca='" + rs.getString("marca") + "', taraOrigine='" + rs.getString("taraOrigine") +
                        "', pret=" + rs.getDouble("pret") + ", stoc=" + rs.getInt("stoc") + "}");
            }
            if (esteGol) {
                System.out.println("Cosul este gol.");
            }
        } catch (Exception e) {
            System.err.println("Eroare la vizualizarea cosului: " + e.getMessage());
        }
    }

    /**
     * Cumpara toate produsele din cosul clientului.
     */
    public void cumpara() {
        try {
            String sqlSelect = "SELECT product_id FROM cart WHERE user_id = ?";
            PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect);
            stmtSelect.setInt(1, getId());
            ResultSet rs = stmtSelect.executeQuery();

            List<Integer> produseCumparate = new ArrayList<>();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String updateStockSql = "UPDATE produse SET stoc = stoc - 1 WHERE id = ? AND stoc > 0";
                PreparedStatement stmtUpdate = connection.prepareStatement(updateStockSql);
                stmtUpdate.setInt(1, productId);
                int rowsUpdated = stmtUpdate.executeUpdate();
                if (rowsUpdated > 0) {
                    produseCumparate.add(productId);
                } else {
                    System.out.println("Produsul cu ID " + productId + " nu mai este in stoc.");
                }
            }

            if (!produseCumparate.isEmpty()) {
                System.out.println("Produsele urmatoare au fost cumparate:");
                for (int idProdus : produseCumparate) {
                    System.out.println("Produs cu ID " + idProdus + " a fost cumparat.");
                }

                String clearCartSql = "DELETE FROM cart WHERE user_id = ?";
                PreparedStatement stmtClear = connection.prepareStatement(clearCartSql);
                stmtClear.setInt(1, getId());
                stmtClear.executeUpdate();

                System.out.println("Cosul a fost golit.");
            } else {
                System.out.println("Cosul este gol. Nu exista produse de cumparat.");
            }
        } catch (Exception e) {
            System.err.println("Eroare la cumparare: " + e.getMessage());
        }
    }

    /**
     * Cauta produse in functie de un criteriu specific.
     * @param criteriu Criteriul de cautare (ex. nume, marca).
     * @param valoare Valoarea pentru criteriul specificat.
     * @return O lista de produse care corespund criteriului.
     */
    public List<Produs> cautareDupaCriteriu(String criteriu, String valoare) {
        List<Produs> rezultate = new ArrayList<>();
        try {
            String sql = "SELECT * FROM produse WHERE " + criteriu + " LIKE ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, "%" + valoare + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produs produs = new Produs(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getString("marca"),
                        rs.getString("taraOrigine"),
                        rs.getDouble("pret"),
                        rs.getInt("stoc")
                );
                rezultate.add(produs);
            }

            if (rezultate.isEmpty()) {
                System.out.println("Nu s-au gasit produse pentru criteriul specificat.");
            } else {
                System.out.println("Rezultate pentru criteriul '" + criteriu + "' cu valoarea '" + valoare + "':");
                rezultate.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("Eroare la cautare: " + e.getMessage());
        }
        return rezultate;
    }

    /**
     * Cauta produse care au un pret mai mic sau egal cu valoarea specificata.
     * @param pretMaxim Pretul maxim pentru cautare.
     * @return O lista de produse care corespund criteriului de pret.
     */
    public List<Produs> cautareDupaPret(double pretMaxim) {
        List<Produs> rezultate = new ArrayList<>();
        try {
            String sql = "SELECT * FROM produse WHERE pret <= ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, pretMaxim);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produs produs = new Produs(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getString("marca"),
                        rs.getString("taraOrigine"),
                        rs.getDouble("pret"),
                        rs.getInt("stoc")
                );
                rezultate.add(produs);
            }

            if (rezultate.isEmpty()) {
                System.out.println("Nu s-au gasit produse cu pretul mai mic sau egal cu " + pretMaxim);
            } else {
                System.out.println("Produse cu pretul mai mic sau egal cu " + pretMaxim + ":");
                rezultate.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("Eroare la cautare dupa pret: " + e.getMessage());
        }
        return rezultate;
    }
}
