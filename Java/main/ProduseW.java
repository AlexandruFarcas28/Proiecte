package ecrane;

import proiect.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clasa responsabila pentru afisarea tuturor produselor din magazin
 */

public class ProduseW {

    /**
     * Afiseaza fereastra cu produsele disponibile.
     * @param primaryStage Scena principala a aplicatiei.
     * @param utilizatorAutentificat Utilizatorul autentificat.
     */
    public void display(Stage primaryStage, Utilizator utilizatorAutentificat) {
        /** Crearea unui nou stage pentru produse disponibile */
        Stage productStage = new Stage();
        productStage.setTitle("Produse Disponibile");

        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        productStage.getIcons().add(icon);

        /** Crearea unui TableView pentru afisarea produselor */
        TableView<Produs> table = new TableView<>();

        /** Definirea coloanelor pentru tabel */
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

        TableColumn<Produs, Integer> stockColumn = new TableColumn<>("Stoc");
        stockColumn.setMinWidth(100);
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        /** Adaugarea coloanelor la tabel */
        table.getColumns().addAll(idColumn, nameColumn, brandColumn, countryColumn, priceColumn, stockColumn);

        /** Popularea tabelului cu produse din baza de date */
        table.setItems(getProducts());

        /** Crearea unui buton pentru intoarcere */
        Button backButton = new Button("Inapoi");
        backButton.setOnAction(e -> {
            productStage.close(); 
            Main mainApp = new Main();
            mainApp.setUtilizatorAutentificat(utilizatorAutentificat); 
            mainApp.showMainMenu(primaryStage);
        });

        /** Configurarea layout-ului */
        VBox layout = new VBox(10);
        layout.getChildren().addAll(table, backButton);

        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 800, 600);
        productStage.setScene(scene);
        productStage.show();
    }

    /**
     * Obtine produsele din baza de date.
     * @return O lista observabila de produse.
     */
    private ObservableList<Produs> getProducts() {
        ObservableList<Produs> products = FXCollections.observableArrayList();
        try {
            String query = "SELECT id, nume, marca, taraOrigine, pret, stoc FROM produse";
            var rs = DatabaseHandler.getInstance().executeQuery(query);

            while (rs.next()) {
                products.add(new Produs(
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
        return products;
    }
}
