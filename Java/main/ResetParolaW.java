package ecrane;

import proiect.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * Clasa responsabila pentru resetarea parolei utilizatorului
 */

public class ResetParolaW {

    private Utilizator utilizatorCurent;

    /**
     * Constructor pentru fereastra de resetare parola.
     * @param utilizatorCurent Utilizatorul autentificat.
     */
    public ResetParolaW(Utilizator utilizatorCurent) {
        this.utilizatorCurent = utilizatorCurent;
    }

    /**
     * Afiseaza fereastra pentru resetarea parolei.
     * @param primaryStage Scena principala a aplicatiei.
     */
    public void display(Stage primaryStage) {
        /** Crearea unui nou stage pentru resetare parola */
        Stage resetStage = new Stage();
        resetStage.setTitle("Resetare Parola");

        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        resetStage.getIcons().add(icon);

        /** Crearea campurilor pentru parola veche si parola noua */
        Label lblParolaVeche = new Label("Parola veche:");
        PasswordField txtParolaVeche = new PasswordField();
        Button btnToggleOldPassword = createToggleButton(txtParolaVeche);

        Label lblParolaNoua = new Label("Parola noua:");
        PasswordField txtParolaNoua = new PasswordField();
        Button btnToggleNewPassword = createToggleButton(txtParolaNoua);

        Label lblConfirmareParola = new Label("Confirmare parola noua:");
        PasswordField txtConfirmareParola = new PasswordField();
        Button btnToggleConfirmPassword = createToggleButton(txtConfirmareParola);

        /** Crearea butonului pentru resetare parola */
        Button btnResetare = new Button("Resetare parola");
        btnResetare.setOnAction(e -> {
            String parolaVeche = txtParolaVeche.getText().trim();
            String parolaNoua = txtParolaNoua.getText().trim();
            String confirmareParola = txtConfirmareParola.getText().trim();

            /** Validarea campurilor */
            if (parolaVeche.isEmpty() || parolaNoua.isEmpty() || confirmareParola.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Toate campurile sunt obligatorii.");
                return;
            }

            if (!utilizatorCurent.getParola().equals(parolaVeche)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Parola veche este incorecta.");
                return;
            }

            if (!parolaNoua.equals(confirmareParola)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Parola noua si confirmarea nu se potrivesc.");
                return;
            }

            boolean succes = DatabaseHandler.getInstance().updatePassword(utilizatorCurent.getId(), parolaNoua);
            if (succes) {
                utilizatorCurent.setParola(parolaNoua);
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Parola a fost resetata cu succes!");
                resetStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut actualiza parola. Incercati din nou.");
            }
        });

        /** Crearea butonului pentru intoarcere */
        Button btnBack = new Button("Inapoi");
        btnBack.setOnAction(e -> {
            resetStage.close();
        });

        /** Configurarea layout-ului */
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                lblParolaVeche, createPasswordBox(txtParolaVeche, btnToggleOldPassword),
                lblParolaNoua, createPasswordBox(txtParolaNoua, btnToggleNewPassword),
                lblConfirmareParola, createPasswordBox(txtConfirmareParola, btnToggleConfirmPassword),
                btnResetare, btnBack
        );

        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 300, 400);
        resetStage.setScene(scene);
        resetStage.show();
    }

    /**
     * Creeaza un buton pentru afisarea/ascunderea parolei.
     * @param txtPassword Campul de parola asociat butonului.
     * @return Butonul creat.
     */
    private Button createToggleButton(PasswordField txtPassword) {
        Button btnTogglePassword = new Button("👁");
        btnTogglePassword.setStyle("-fx-font-size: 10px; -fx-padding: 2px;");
        btnTogglePassword.setOnAction(e -> {
            if (txtPassword.getPromptText().isEmpty()) {
                txtPassword.setPromptText(txtPassword.getText());
                txtPassword.clear();
            } else {
                txtPassword.setText(txtPassword.getPromptText());
                txtPassword.setPromptText("");
            }
        });
        return btnTogglePassword;
    }

    /**
     * Creeaza un layout pentru campul de parola si butonul de toggle.
     * @param txtPassword Campul de parola.
     * @param btnTogglePassword Butonul de toggle.
     * @return Un HBox continand campul si butonul.
     */
    private HBox createPasswordBox(PasswordField txtPassword, Button btnTogglePassword) {
        HBox passwordBox = new HBox(5);
        passwordBox.getChildren().addAll(txtPassword, btnTogglePassword);
        return passwordBox;
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
