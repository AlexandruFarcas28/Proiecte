package ecrane;

import proiect.*;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * Clasa responsabila pentru afisarea scenei de Logare in aplicatie
 */

public class LoginW {

    /**
     * Afiseaza fereastra de autentificare.
     * @param primaryStage Scena principala a aplicatiei.
     */
    public void display(Stage primaryStage) {
        /** Crearea unui nou stage pentru autentificare */
        Stage loginStage = new Stage();
        loginStage.setTitle("Autentificare");

        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        loginStage.getIcons().add(icon);

        /** Crearea campurilor pentru nume utilizator si parola */
        Label lblNume = new Label("Nume utilizator:");
        TextField txtNume = new TextField();
        txtNume.setMaxWidth(200);

        Label lblParola = new Label("Parola:");
        PasswordField txtParola = new PasswordField();
        txtParola.setMaxWidth(200);

        /** Buton pentru afisarea/ascunderea parolei */
        Button btnTogglePassword = new Button("👁");
        btnTogglePassword.setStyle("-fx-font-size: 10px; -fx-padding: 2px;");
        btnTogglePassword.setOnAction(e -> {
            if (txtParola.getPromptText().isEmpty()) {
                txtParola.setPromptText(txtParola.getText());
                txtParola.clear();
                btnTogglePassword.setText("🔒");
            } else {
                txtParola.setText(txtParola.getPromptText());
                txtParola.setPromptText("");
                btnTogglePassword.setText("👁");
            }
        });

        /** Layout pentru campul de parola si butonul de toggle */
        HBox passwordBox = new HBox(5);
        passwordBox.getChildren().addAll(txtParola, btnTogglePassword);
        passwordBox.setPadding(new Insets(5));

        /** Crearea butoanelor pentru autentificare, recuperare parola si intoarcere */
        Button btnLogin = new Button("Autentificare");
        Button btnRecuperareParola = new Button("Recuperare Parola");
        Button btnBack = new Button("Inapoi");

        /** Eveniment pentru butonul de autentificare */
        btnLogin.setOnAction(e -> {
            String numeUtilizator = txtNume.getText();
            String parola = txtParola.getPromptText().isEmpty() ? txtParola.getText() : txtParola.getPromptText();
            Utilizator utilizator = autentificare(numeUtilizator, parola);

            if (utilizator != null) {
                Main mainApp = new Main();
                mainApp.setUtilizatorAutentificat(utilizator);
                loginStage.close();
                mainApp.showMainMenu(primaryStage);

                /** Setarea iconitei pentru scena principala */
                Image iconi = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
                primaryStage.getIcons().add(iconi);
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Numele de utilizator sau parola sunt incorecte.");
            }
        });

        /** Eveniment pentru butonul de recuperare parola */
        btnRecuperareParola.setOnAction(e -> {
            new RecuperareParolaW().display();
        });

        /** Eveniment pentru butonul de intoarcere */
        btnBack.setOnAction(e -> {
            loginStage.close();
            new InitialW().display(primaryStage);
        });

        /** Configurarea layout-ului */
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblNume, txtNume, lblParola, passwordBox, btnLogin, btnRecuperareParola, btnBack);

        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 300, 400);
        loginStage.setScene(scene);
        loginStage.show();
    }

    /**
     * Autentifica utilizatorul folosind numele de utilizator si parola.
     * @param numeUtilizator Numele utilizatorului.
     * @param parola Parola utilizatorului.
     * @return Un obiect de tip Utilizator daca autentificarea reuseste, altfel null.
     */
    private Utilizator autentificare(String numeUtilizator, String parola) {
        try {
            String query = "SELECT * FROM utilizatori WHERE nume_utilizator = ?";
            var rs = DatabaseHandler.getInstance().executePreparedQuery(query, numeUtilizator);
            if (rs.next() && rs.getString("parola").equals(parola)) {
                return new Utilizator(
                        rs.getInt("id"),
                        rs.getString("nume_utilizator"),
                        rs.getString("parola"),
                        rs.getString("rol"),
                        rs.getString("email")
                );
            }
        } catch (Exception e) {
            System.err.println("Eroare la autentificare: " + e.getMessage());
        }
        return null;
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
