package ecrane;

import proiect.*;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * 
 * Clasa responsabila pentru modificarea unui produs
 */

public class ModificareProdusW {

    /**
     * Afiseaza fereastra pentru modificarea unui produs.
     * @param primaryStage Scena principala a aplicatiei.
     * @param utilizatorAutentificat Utilizatorul autentificat.
     */
    public void display(Stage primaryStage, Utilizator utilizatorAutentificat) {
        /** Crearea unui nou stage pentru modificare produs */
        Stage modifyProductStage = new Stage();
        modifyProductStage.setTitle("Modificare Produs");

        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        modifyProductStage.getIcons().add(icon);

        /** Crearea campurilor pentru datele produsului */
        Label lblId = new Label("Introduceti ID-ul produsului:");
        TextField txtId = new TextField();
        txtId.setMaxWidth(200);
        Label lblNume = new Label("Nume Produs:");
        TextField txtNume = new TextField();
        txtNume.setMaxWidth(200);
        Label lblMarca = new Label("Marca:");
        TextField txtMarca = new TextField();
        txtMarca.setMaxWidth(200);
        Label lblTara = new Label("Tara de Origine:");
        TextField txtTara = new TextField();
        txtTara.setMaxWidth(200);
        Label lblPret = new Label("Pret:");
        TextField txtPret = new TextField();
        txtPret.setMaxWidth(200);
        Label lblStoc = new Label("Stoc:");
        TextField txtStoc = new TextField();
        txtStoc.setMaxWidth(200);

        /** Crearea butoanelor pentru actiuni */
        Button btnCauta = new Button("Cauta Produs");
        Button btnModifica = new Button("Modifica Produs");
        Button btnBack = new Button("Inapoi");

        /** Eveniment pentru butonul de cautare produs */
        btnCauta.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                Produs produs = DatabaseHandler.getInstance().getId(id);

                if (produs != null) {
                    txtNume.setText(produs.getNume());
                    txtMarca.setText(produs.getMarca());
                    txtTara.setText(produs.getTaraOrigine());
                    txtPret.setText(String.valueOf(produs.getPret()));
                    txtStoc.setText(String.valueOf(produs.getStock()));
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Produsul cu acest ID nu exista.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "ID-ul trebuie sa fie un numar valid.");
            }
        });

        /** Eveniment pentru butonul de modificare produs */
        btnModifica.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String nume = txtNume.getText();
                String marca = txtMarca.getText();
                String tara = txtTara.getText();
                double pret = Double.parseDouble(txtPret.getText());
                int stoc = Integer.parseInt(txtStoc.getText());

                boolean succes = DatabaseHandler.getInstance().updateProduct(id, nume, marca, tara, pret, stoc);

                if (succes) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Produsul a fost modificat cu succes!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut modifica produsul.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Pretul si stocul trebuie sa fie numere valide.");
            }
        });

        /** Eveniment pentru butonul de intoarcere */
        btnBack.setOnAction(e -> {
            modifyProductStage.close();
            Main mainApp = new Main();
            mainApp.setUtilizatorAutentificat(utilizatorAutentificat);
        });

        /** Configurarea layout-ului */
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(lblId, txtId, btnCauta, lblNume, txtNume, lblMarca, txtMarca, lblTara, txtTara, lblPret, txtPret, lblStoc, txtStoc, btnModifica, btnBack);

        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 400, 600);
        btnCauta.setFocusTraversable(true);
        btnModifica.setFocusTraversable(true);
        btnBack.setFocusTraversable(true);

        modifyProductStage.setScene(scene);
        modifyProductStage.show();
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
