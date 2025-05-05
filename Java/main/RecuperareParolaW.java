package ecrane;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * Clasa responsabila pentru recuperarea parolei unui utilizator
 */

public class RecuperareParolaW {

    /**
     * Afiseaza fereastra pentru recuperarea parolei.
     */
    public void display() {
        /** Crearea unui nou stage pentru recuperare parola */
        Stage resetStage = new Stage();
        resetStage.setTitle("Recuperare Parola");

        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        resetStage.getIcons().add(icon);

        /** Crearea campului pentru introducerea adresei de email */
        Label lblEmail = new Label("Introduceti adresa de email:");
        TextField txtEmail = new TextField();
        txtEmail.setMaxWidth(200);

        /** Crearea butonului pentru trimiterea cererii de recuperare parola */
        Button btnTrimite = new Button("Trimite");

        /** Eveniment pentru butonul de trimitere */
        btnTrimite.setOnAction(e -> {
            String email = txtEmail.getText();
            if (email.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Adresa de email nu poate fi goala.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Instructiuni pentru recuperarea parolei au fost trimise la " + email);
                resetStage.close();
            }
        });

        /** Configurarea layout-ului */
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblEmail, txtEmail, btnTrimite);

        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 300, 200);
        resetStage.setScene(scene);
        resetStage.show();
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
