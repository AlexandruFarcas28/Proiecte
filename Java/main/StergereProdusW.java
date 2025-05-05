package ecrane;

import proiect.*;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * Clasa responsabila pentru stergerea unui produs din magazin
 */


public class StergereProdusW {

    /**
     * Afiseaza fereastra pentru stergerea unui produs.
     * @param primaryStage Scena principala a aplicatiei.
     * @param utilizatorAutentificat Utilizatorul autentificat.
     */
    public void display(Stage primaryStage, Utilizator utilizatorAutentificat) {
        /** Crearea unui nou stage pentru stergerea unui produs */
        Stage deleteStage = new Stage();
        deleteStage.setTitle("Stergere Produs");

        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        deleteStage.getIcons().add(icon);

        /** Crearea campului pentru ID-ul produsului */
        Label lblId = new Label("ID Produs:");
        TextField txtId = new TextField();
        txtId.setMaxWidth(200);

        /** Crearea butoanelor pentru stergere si intoarcere */
        Button btnSterge = new Button("Sterge");
        Button btnInapoi = new Button("Inapoi");

        /** Eveniment pentru butonul de stergere produs */
        btnSterge.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                boolean succes = DatabaseHandler.getInstance().deleteProduct(id);
                if (succes) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Produsul a fost sters cu succes!");
                    deleteStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut sterge produsul.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "ID-ul trebuie sa fie un numar valid.");
            }
        });

        /** Eveniment pentru butonul de intoarcere */
        btnInapoi.setOnAction(e -> {
            deleteStage.close();
            Main mainApp = new Main(); 
            mainApp.setUtilizatorAutentificat(utilizatorAutentificat); 
            mainApp.showMainMenu(primaryStage);
        });

        /** Configurarea layout-ului */
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblId, txtId, btnSterge, btnInapoi);

        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 300, 200);
        btnSterge.setFocusTraversable(true);
        btnInapoi.setFocusTraversable(true);

        deleteStage.setScene(scene);
        deleteStage.show();
    }

    /**
     * Afiseaza un mesaj de alerta.
     * @param type Tipul alertei (INFORMATION, ERROR, etc.).
     * @param title Titlul alertei.
     * @param message Mesajul alertei.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
