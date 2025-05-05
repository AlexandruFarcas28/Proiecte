package ecrane;

import proiect.*;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clasa CautareW este responsabila pentru afisarea unui ecran de adaugare a unui produs in aplicatie in functie de criteriul/criteriile de cautare ales/alese.
 */

public class CautareW {

    public void display(Stage primaryStage, Utilizator utilizatorAutentificat) {
        /** Crearea unui nou stage pentru cautarea produselor */
        Stage searchStage = new Stage();
        searchStage.setTitle("Cautare Produse");
        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        searchStage.getIcons().add(icon);

        /** Crearea tabelului pentru afisarea produselor */
        TableView<Produs> table = new TableView<>();

        TableColumn<Produs, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setMinWidth(50);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Produs, String> nameColumn = new TableColumn<>("Nume Produs");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));

        TableColumn<Produs, String> brandColumn = new TableColumn<>("Marca");
        brandColumn.setMinWidth(150);
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("marca"));

        TableColumn<Produs, String> countryColumn = new TableColumn<>("Tara de Origine");
        countryColumn.setMinWidth(150);
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("taraOrigine"));

        TableColumn<Produs, Double> priceColumn = new TableColumn<>("Pret");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pret"));

        TableColumn<Produs, Integer> quantityColumn = new TableColumn<>("Cantitate");
        quantityColumn.setMinWidth(100);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        table.getColumns().addAll(idColumn, nameColumn, brandColumn, countryColumn, priceColumn, quantityColumn);

        /** Crearea campurilor pentru cautare si configurarea acestora */
        TextField searchField = new TextField();
        searchField.setPromptText("Introduceti criteriul de cautare");

        ComboBox<String> criteriaBox = new ComboBox<>();
        criteriaBox.getItems().addAll("Nume", "Marca", "Tara Origine");
        criteriaBox.setValue("nume");

        Label priceRangeLabel = new Label("Interval Pret in RON:");

        TextField minPriceField = new TextField();
        minPriceField.setPromptText("Pret minim");
        minPriceField.setText("1"); 

        TextField maxPriceField = new TextField();
        maxPriceField.setPromptText("Pret maxim");
        DecimalFormat df = new DecimalFormat("#.##");
        /** Setarea pretului maxim din baza de date */
        double maxPrice = getMaxPriceFromDatabase();
        maxPriceField.setText(df.format(maxPrice));

        /** Configurarea butonului de cautare */
        Button searchButton = new Button("Cauta");
        searchButton.setOnAction(e -> {
            String criteria = criteriaBox.getValue();
            String value = searchField.getText();
            double minPrice;
            double maxPriceInput;

            try {
                minPrice = Double.parseDouble(minPriceField.getText());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Atentie", "Introduceti un pret minim valid.");
                return;
            }

            try {
                maxPriceInput = Double.parseDouble(maxPriceField.getText());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Atentie", "Introduceti un pret maxim valid.");
                return;
            }

            if (minPrice > maxPriceInput) {
                showAlert(Alert.AlertType.WARNING, "Atentie", "Pretul minim nu poate fi mai mare decat pretul maxim.");
                return;
            }

            /** Actualizarea tabelului cu produsele gasite */
            table.setItems(searchProducts(criteria, value, minPrice, maxPriceInput));
        });

        TextField quantityField = new TextField();
        quantityField.setPromptText("Cantitate");
        quantityField.setMaxWidth(100);

        Button addToCartButton = new Button("Adauga in Cos");
        addToCartButton.setOnAction(e -> {
            Produs selectedProduct = table.getSelectionModel().getSelectedItem();
            String quantityText = quantityField.getText();

            if (selectedProduct != null) {
                try {
                    int quantity = Integer.parseInt(quantityText);
                    if (quantity <= 0) {
                        showAlert(Alert.AlertType.WARNING, "Atentie", "Cantitate invalida.");
                        return;
                    }
                    boolean success = addToCart(utilizatorAutentificat.getId(), selectedProduct, quantity);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Succes", "Produsul a fost adaugat in cos.");
                        table.setItems(searchProducts(criteriaBox.getValue(), searchField.getText(),
                                Double.parseDouble(minPriceField.getText()), Double.parseDouble(maxPriceField.getText()))); // Refresh tabel
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut adauga produsul in cos.");
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.WARNING, "Atentie", "Introduceti o cantitate valida.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Atentie", "Selectati un produs pentru a-l adauga in cos.");
            }
        });

        Button backButton = new Button("Inapoi");
        backButton.setOnAction(e -> searchStage.close());

        HBox priceBox = new HBox(10, new Label("Min:"), minPriceField, new Label("Max:"), maxPriceField);
        HBox actionsBox = new HBox(10, quantityField, addToCartButton);
        VBox layout = new VBox(10, criteriaBox, searchField, priceRangeLabel, priceBox, searchButton, table, actionsBox, backButton);

        /** Configurarea si afisarea scenei */
        Scene scene = new Scene(layout, 800, 600);
        searchStage.setScene(scene);
        searchStage.show();
    }

    /**
     * Cauta produsele in baza de date pe baza criteriilor date.
     * @param criteria Criteriul de cautare (nume, marca, etc.).
     * @param value Valoarea criteriului de cautare.
     * @param minPrice Pretul minim al produselor.
     * @param maxPrice Pretul maxim al produselor.
     * @return O lista de produse care corespund criteriilor.
     */
    private ObservableList<Produs> searchProducts(String criteria, String value, double minPrice, double maxPrice) {
        ObservableList<Produs> searchResults = FXCollections.observableArrayList();
        try {
            String query = "SELECT id, nume, marca, taraOrigine, pret, stoc FROM produse WHERE " + criteria.toLowerCase() + " LIKE ? AND pret BETWEEN ? AND ?";
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setString(1, "%" + value + "%");
            stmt.setDouble(2, minPrice);
            stmt.setDouble(3, maxPrice);

            var rs = stmt.executeQuery();
            while (rs.next()) {
                searchResults.add(new Produs(
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getString("marca"),
                        rs.getString("taraOrigine"),
                        rs.getDouble("pret"),
                        rs.getInt("stoc")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    /**
     * Adauga un produs in cosul utilizatorului.
     * @param userId ID-ul utilizatorului care adauga in cos.
     * @param produs Produsul care trebuie adaugat.
     * @param quantity Cantitatea produsului.
     * @return True daca produsul a fost adaugat cu succes, altfel false.
     */
    private boolean addToCart(int userId, Produs produs, int quantity) {
        try {
            var conn = DatabaseHandler.getInstance().getConnection();
            conn.setAutoCommit(false);

            String checkStockQuery = "SELECT stoc FROM produse WHERE id = ?";
            var stockStmt = conn.prepareStatement(checkStockQuery);
            stockStmt.setInt(1, produs.getId());
            var stockRs = stockStmt.executeQuery();
            if (stockRs.next()) {
                int currentStock = stockRs.getInt("stoc");
                if (quantity > currentStock) {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Stoc insuficient pentru produsul selectat.");
                    conn.rollback();
                    return false;
                }
            }

            String updateStockQuery = "UPDATE produse SET stoc = stoc - ? WHERE id = ?";
            var updateStockStmt = conn.prepareStatement(updateStockQuery);
            updateStockStmt.setInt(1, quantity);
            updateStockStmt.setInt(2, produs.getId());
            updateStockStmt.executeUpdate();

            String query = "INSERT INTO cos_cumparaturi (user_id, produs_id, nume, marca, taraOrigine, pret, cantitate) VALUES (?, ?, ?, ?, ?, ?, ?)";
            var stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, produs.getId());
            stmt.setString(3, produs.getNume());
            stmt.setString(4, produs.getMarca());
            stmt.setString(5, produs.getTaraOrigine());
            stmt.setDouble(6, produs.getPret());
            stmt.setInt(7, quantity);
            stmt.executeUpdate();

            String deleteProductQuery = "DELETE FROM produse WHERE stoc = 0";
            var deleteStmt = conn.prepareStatement(deleteProductQuery);
            deleteStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        /** Crearea unei alerte */
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Obtine pretul maxim al produselor din baza de date.
     * @return Pretul maxim al unui produs.
     */
    private double getMaxPriceFromDatabase() {
        double maxPrice = 0;
        try {
            String query = "SELECT MAX(pret) AS max_price FROM produse";
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maxPrice = rs.getDouble("max_price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxPrice;
    }
}
