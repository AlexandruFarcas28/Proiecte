package ecrane;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import proiect.DatabaseHandler;
import proiect.Utilizator;

import java.util.HashMap;
import java.util.Map;

/**
 * Clasa responsabila pentru afisarea comenzilor efectuate in trecut
 */

public class IstoricW {

    /**
     * Afiseaza fereastra cu istoricul comenzilor utilizatorului.
     * @param primaryStage Scena principala a aplicatiei.
     * @param utilizatorAutentificat Utilizatorul autentificat.
     */
    public void display(Stage primaryStage, Utilizator utilizatorAutentificat) {
    	/** Crearea unui nou stage pentru istoric comenzi */
        Stage historyStage = new Stage();
        historyStage.setTitle("Istoric Comenzi");
        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        historyStage.getIcons().add(icon);
        /** Crearea unui TreeTableView pentru a afisa comenzile grupate */
        TreeTableView<Map<String, Object>> treeTable = new TreeTableView<>();

        /** Definirea coloanelor pentru TreeTableView */
        TreeTableColumn<Map<String, Object>, String> dateColumn = new TreeTableColumn<>("Data Comandă");
        dateColumn.setMinWidth(150);
        dateColumn.setCellValueFactory(param -> {
            Object date = param.getValue().getValue().get("dataComanda");
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        TreeTableColumn<Map<String, Object>, String> nameColumn = new TreeTableColumn<>("Nume Produs");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(param -> {
            Object name = param.getValue().getValue().get("nume");
            return new SimpleStringProperty(name != null ? name.toString() : "");
        });

        TreeTableColumn<Map<String, Object>, String> brandColumn = new TreeTableColumn<>("Marcă");
        brandColumn.setMinWidth(150);
        brandColumn.setCellValueFactory(param -> {
            Object brand = param.getValue().getValue().get("marca");
            return new SimpleStringProperty(brand != null ? brand.toString() : "");
        });

        TreeTableColumn<Map<String, Object>, String> priceColumn = new TreeTableColumn<>("Preț");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(param -> {
            Object price = param.getValue().getValue().get("pret");
            return new SimpleStringProperty(price != null ? price.toString() : "");
        });

        TreeTableColumn<Map<String, Object>, String> quantityColumn = new TreeTableColumn<>("Cantitate");
        quantityColumn.setMinWidth(100);
        quantityColumn.setCellValueFactory(param -> {
            Object quantity = param.getValue().getValue().get("cantitate");
            return new SimpleStringProperty(quantity != null ? quantity.toString() : "");
        });

        treeTable.getColumns().addAll(dateColumn, nameColumn, brandColumn, priceColumn, quantityColumn);

        
        treeTable.setRoot(getGroupedHistory(utilizatorAutentificat.getId()));
        treeTable.setShowRoot(false);

        /** Crearea butonului pentru intoarcere */
        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> historyStage.close());

        VBox layout = new VBox(10, treeTable, backButton);
        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 800, 600);
        historyStage.setScene(scene);
        historyStage.show();
    }

    /**
     * Obtine istoricul comenzilor grupat pe date.
     * @param userId ID-ul utilizatorului.
     * @return Radacina arborelui cu istoricul comenzilor.
     */
    private TreeItem<Map<String, Object>> getGroupedHistory(int userId) {
        TreeItem<Map<String, Object>> root = new TreeItem<>(new HashMap<>());

        try {
            String query = """
                SELECT data_comanda, nume, marca, pret, cantitate
                FROM istoric_comenzi
                WHERE user_id = ?
                ORDER BY data_comanda
            """;
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, userId);
            var rs = stmt.executeQuery();

            Map<String, TreeItem<Map<String, Object>>> groupedHistory = new HashMap<>();

            while (rs.next()) {
                String dataComanda = rs.getString("data_comanda");

                /** Crearea unui nod pentru data comenzii, daca nu exista deja */
                if (!groupedHistory.containsKey(dataComanda)) {
                    Map<String, Object> dateNodeInfo = new HashMap<>();
                    dateNodeInfo.put("dataComanda", dataComanda);
                    dateNodeInfo.put("nume", ""); 
                    dateNodeInfo.put("marca", "");
                    dateNodeInfo.put("pret", "");
                    dateNodeInfo.put("cantitate", "");

                    TreeItem<Map<String, Object>> dateNode = new TreeItem<>(dateNodeInfo);
                    root.getChildren().add(dateNode);
                    groupedHistory.put(dataComanda, dateNode);
                }

                /** Crearea unui nod pentru fiecare produs */
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("dataComanda", ""); // Nu afișăm data pentru produsele individuale
                productInfo.put("nume", rs.getString("nume"));
                productInfo.put("marca", rs.getString("marca"));
                productInfo.put("pret", String.format("%.2f", rs.getDouble("pret")));
                productInfo.put("cantitate", String.valueOf(rs.getInt("cantitate")));

                /** Adaugarea produsului ca nod copil sub nodul corespunzator datei comenzii */
                groupedHistory.get(dataComanda).getChildren().add(new TreeItem<>(productInfo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }
}
