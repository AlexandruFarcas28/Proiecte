package ecrane;

import proiect.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * Clasa responsabila pentru ecranul de inregistrare
 */

public class InregistrareW {

	
    /**
     * Afiseaza fereastra de inregistrare pentru utilizatori.
     * @param primaryStage Scena principala a aplicatiei.
     */
    public void display(Stage primaryStage) {
    	/** Crearea unui nou stage pentru inregistrare */
        Stage registrationStage = new Stage();
        registrationStage.setTitle("Înregistrare Client");
        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        registrationStage.getIcons().add(icon);
        /** Crearea campurilor pentru nume utilizator, email si parola */
        Label lblNume = new Label("Nume utilizator:");
        TextField txtNume = new TextField();
        txtNume.setMaxWidth(200);
        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField();
        txtEmail.setMaxWidth(200);
        Label lblParola = new Label("Parolă:");
        PasswordField txtParola = new PasswordField();
        txtParola.setMaxWidth(200);
        /** Adaugarea unui buton pentru afisarea/ascunderea parolei */
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
        /** Configurarea unui layout pentru parola si butonul de toggle */
        HBox passwordBox = new HBox(5);
        passwordBox.getChildren().addAll(txtParola, btnTogglePassword);
        passwordBox.setPadding(new Insets(5));
        /** Crearea butoanelor pentru inregistrare si intoarcere */
        Button btnInregistrare = new Button("Înregistrare");
        Button btnBack = new Button("Înapoi");
        /** Eveniment pentru butonul de inregistrare */
        btnInregistrare.setOnAction(e -> {
            String nume = txtNume.getText();
            String email = txtEmail.getText();
            String parola = txtParola.getPromptText().isEmpty() ? txtParola.getText() : txtParola.getPromptText();

            /** Validare câmpuri goale*/
            if (nume.isEmpty() || email.isEmpty() || parola.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Toate câmpurile sunt obligatorii.");
                return;
            }

            /** Validare email */
            if (!isValidEmail(email)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Adresa de email nu este validă. Asigură-te că are formatul corect (ex: user@domain.com).");
                return;
            }

            /** Validare parolă*/
            if (!isValidPassword(parola)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Parola trebuie să conțină cel puțin 8 caractere, dintre care:\n- Cel puțin 2 cifre,\n- Cel puțin 4 litere (1 literă mare),\n- Cel puțin un caracter special (!@#$%^&*, etc).");
                return;
            }

            /** Verifică dacă emailul este deja înregistrat*/
            if (isEmailAlreadyRegistered(email)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Această adresă de email este deja utilizată.");
                return;
            }

            /** Crearea contului*/
            boolean succes = registerNewClient(nume, email, parola);
            if (succes) {
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Contul a fost creat cu succes!");
                registrationStage.close();
                new InitialW().display(primaryStage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut crea contul. Încercați din nou.");
            }
        });
        /** Eveniment pentru butonul de intoarcere */
        btnBack.setOnAction(e -> {
            registrationStage.close();
            new InitialW().display(primaryStage);
        });
        /** Configurarea layout-ului */
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblNume, txtNume, lblEmail, txtEmail, lblParola, passwordBox, btnInregistrare, btnBack);
        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 300, 400);
        registrationStage.setScene(scene);
        registrationStage.show();
    }

    /**
     * Verifica daca emailul este deja inregistrat in baza de date.
     * @param email Adresa de email a utilizatorului.
     * @return True daca emailul este deja inregistrat, altfel false.
     */
    private boolean isEmailAlreadyRegistered(String email) {
        try {
            String query = "SELECT COUNT(*) AS count FROM utilizatori WHERE email = ?";
            var rs = DatabaseHandler.getInstance().executePreparedQuery(query, email);
            return rs.next() && rs.getInt("count") > 0;
        } catch (Exception e) {
            System.err.println("Eroare la verificarea emailului: " + e.getMessage());
        }
        return false;
    }

    /**
     * Creeaza un nou client in baza de date.
     * @param nume Numele utilizatorului.
     * @param email Adresa de email a utilizatorului.
     * @param parola Parola utilizatorului.
     * @return True daca utilizatorul a fost creat cu succes, altfel false.
     */
    private boolean registerNewClient(String nume, String email, String parola) {
        try {
            String query = "INSERT INTO utilizatori (nume_utilizator, parola, rol, email) VALUES (?, ?, 'client', ?)";
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setString(1, nume);
            stmt.setString(2, parola);
            stmt.setString(3, email);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Eroare la crearea contului: " + e.getMessage());
        }
        return false;
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

    /**
     * Valideaza formatul emailului folosind o expresie regulata.
     * @param email Adresa de email de validat.
     * @return True daca emailul este valid, altfel false.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Valideaza parola conform cerintelor specificate.
     * @param password Parola de validat.
     * @return True daca parola este valida, altfel false.
     */
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=(.*\\d.*){2})(?=(.*[a-zA-Z].*){4})(?=(.*[A-Z].*))(?=(.*[!@#$%^&*,.?]).*)[a-zA-Z0-9!@#$%^&*,.?]{8,}$";
        return password.matches(passwordRegex);
    }
}
