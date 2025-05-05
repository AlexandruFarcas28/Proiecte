package proiect;

import ecrane.*;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private Utilizator utilizatorAutentificat;

    /**
     * Punctul de intrare al aplicatiei.
     * @param args Argumentele liniei de comanda.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Porneste aplicatia si afiseaza fereastra initiala.
     * @param primaryStage Scena principala a aplicatiei.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplicatie Gestionare Produse");
        new InitialW().display(primaryStage);
    }

    /**
     * Seteaza utilizatorul autentificat in sesiunea curenta.
     * @param utilizator Obiectul utilizator autentificat.
     */
    public void setUtilizatorAutentificat(Utilizator utilizator) {
        this.utilizatorAutentificat = utilizator;
    }

    /**
     * Afiseaza meniul principal in functie de rolul utilizatorului autentificat.
     * @param primaryStage Scena principala a aplicatiei.
     */
    public void showMainMenu(Stage primaryStage) {
        if (utilizatorAutentificat == null) {
            System.err.println("Eroare: Utilizatorul nu este autentificat!");
            new InitialW().display(primaryStage);
            return;
        }

        if ("manager".equals(utilizatorAutentificat.getRol())) {
            showManagerMenu(primaryStage);
        } else {
            showClientMenu(primaryStage);
        }
    }

    /**
     * Afiseaza meniul pentru utilizatorul cu rol de client.
     * @param primaryStage Scena principala a aplicatiei.
     */
    private void showClientMenu(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        Button btnVizualizareProduse = new Button("Vizualizare Produse");
        Button btnCautare = new Button("Cautare Produse");
        Button btnCos = new Button("Vizualizare Cos");
        Button btnResetParola = new Button("Resetare Parola");
        Button btnIstoric = new Button("Istoric Comenzi");

        btnVizualizareProduse.setPrefWidth(200);
        btnCautare.setPrefWidth(200);
        btnCos.setPrefWidth(200);
        btnResetParola.setPrefWidth(200);
        btnIstoric.setPrefWidth(200);

        btnVizualizareProduse.setOnAction(e -> new ProduseW().display(primaryStage, utilizatorAutentificat));
        btnCautare.setOnAction(e -> new CautareW().display(primaryStage, utilizatorAutentificat));
        btnCos.setOnAction(e -> new CosW().display(primaryStage, utilizatorAutentificat));
        btnResetParola.setOnAction(e -> new ResetParolaW(utilizatorAutentificat).display(primaryStage));
        btnIstoric.setOnAction(e -> new IstoricW().display(primaryStage, utilizatorAutentificat));

        layout.getChildren().addAll(btnVizualizareProduse, btnCautare, btnCos, btnResetParola, btnIstoric);

        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Afiseaza meniul pentru utilizatorul cu rol de manager.
     * @param primaryStage Scena principala a aplicatiei.
     */
    private void showManagerMenu(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        Button btnVizualizareProduse = new Button("Vizualizare Produse");
        Button btnCautare = new Button("Cautare Produse");
        Button btnCos = new Button("Vizualizare Cos");
        Button btnResetParola = new Button("Resetare Parola");
        Button btnIstoric = new Button("Istoric Comenzi");

        btnVizualizareProduse.setPrefWidth(200);
        btnCautare.setPrefWidth(200);
        btnCos.setPrefWidth(200);
        btnResetParola.setPrefWidth(200);
        btnIstoric.setPrefWidth(200);

        btnVizualizareProduse.setOnAction(e -> new ProduseW().display(primaryStage, utilizatorAutentificat));
        btnCautare.setOnAction(e -> new CautareW().display(primaryStage, utilizatorAutentificat));
        btnCos.setOnAction(e -> new CosW().display(primaryStage, utilizatorAutentificat));
        btnResetParola.setOnAction(e -> new ResetParolaW(utilizatorAutentificat).display(primaryStage));
        btnIstoric.setOnAction(e -> new IstoricW().display(primaryStage, utilizatorAutentificat));

        Button btnAdaugareProduse = new Button("Adaugare Produse");
        Button btnStergereProduse = new Button("Stergere Produse");
        Button btnModificareProduse = new Button("Modificare Produse");

        btnAdaugareProduse.setPrefWidth(200);
        btnStergereProduse.setPrefWidth(200);
        btnModificareProduse.setPrefWidth(200);

        btnAdaugareProduse.setOnAction(e -> new AdaugareProdusW().display(primaryStage, utilizatorAutentificat));
        btnStergereProduse.setOnAction(e -> new StergereProdusW().display(primaryStage, utilizatorAutentificat));
        btnModificareProduse.setOnAction(e -> new ModificareProdusW().display(primaryStage, utilizatorAutentificat));

        layout.getChildren().addAll(btnVizualizareProduse, btnCautare, btnCos, btnResetParola, btnIstoric, btnAdaugareProduse, btnStergereProduse, btnModificareProduse);

        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
