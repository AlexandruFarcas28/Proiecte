package ecrane;

import proiect.*;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * Clasa AdaugareProdusW este responsabila pentru afisarea unui ecran pentru afisarea tuturor produselor din magazin.
 */
public class AdaugareProdusW {

    /**
     * Metoda principala care afiseaza fereastra de adaugare produs.
     *
     * @param primaryStage         Scena principala a aplicatiei.
     * @param utilizatorAutentificat Utilizatorul autentificat care foloseste aplicatia.
     */
    public void display(Stage primaryStage, Utilizator utilizatorAutentificat) {
        /** Crearea unui nou stage pentru adaugarea produsului */
        Stage addStage = new Stage();
        addStage.setTitle("Adaugare Produs");

        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");
        addStage.getIcons().add(icon);

        /** Crearea si configurarea elementelor grafice */
        Label lblNume = new Label("Nume Produs:");
        TextField txtNume = new TextField();
        txtNume.setMaxWidth(200);

        Label lblMarca = new Label("Marca:");
        TextField txtMarca = new TextField();
        txtMarca.setMaxWidth(200);

        Label lblTaraOrigine = new Label("Tara de Origine:");
        TextField txtTaraOrigine = new TextField();
        txtTaraOrigine.setMaxWidth(200);

        Label lblPret = new Label("Pret:");
        TextField txtPret = new TextField();
        txtPret.setMaxWidth(200);

        Label lblStoc = new Label("Stoc:");
        TextField txtStoc = new TextField();
        txtStoc.setMaxWidth(200);

        Button btnAdauga = new Button("Adauga");
        Button btnInapoi = new Button("Inapoi");

        /** Eveniment pentru butonul de adaugare */
        btnAdauga.setOnAction(e -> {
            String nume = txtNume.getText();
            String marca = txtMarca.getText();
            String taraOrigine = txtTaraOrigine.getText();
            double pret = Double.parseDouble(txtPret.getText());
            int stoc = Integer.parseInt(txtStoc.getText());

            /** Apel catre metoda de adaugare produs */
            boolean succes = DatabaseHandler.getInstance().addProduct(nume, marca, taraOrigine, pret, stoc);
            if (succes) {
                showAlert(Alert.AlertType.INFORMATION, "Succes", "Produsul a fost adaugat cu succes!");
                addStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut adauga produsul.");
            }
        });

        /** Eveniment pentru butonul de intoarcere la meniul principal */
        btnInapoi.setOnAction(e -> {
            addStage.close();
            Main mainApp = new Main(); 
            mainApp.setUtilizatorAutentificat(utilizatorAutentificat); 
            mainApp.showMainMenu(primaryStage); 
        });

        /** Configurare layout */
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblNume, txtNume, lblMarca, txtMarca, lblTaraOrigine, txtTaraOrigine, lblPret, txtPret, lblStoc, txtStoc, btnAdauga, btnInapoi);

        Scene scene = new Scene(layout, 300, 550);

        /** Setarea scenei si afisarea ferestrei */
        addStage.setScene(scene);
        addStage.show();
    }

    /**
     * Afiseaza o alerta de tip pop-up.
     *
     * @param type    Tipul alertei (INFORMATION, ERROR, etc.).
     * @param title   Titlul ferestrei de alerta.
     * @param message Mesajul afisat in alerta.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        /** Crearea unei alerte */
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
