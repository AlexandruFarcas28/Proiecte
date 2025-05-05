package ecrane;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clasa responsabila pentru ecranul de Logare/Inregistrare
 */

public class InitialW {

    /**
     * Afiseaza fereastra initiala a aplicatiei cu optiuni pentru conectare sau inregistrare.
     * @param primaryStage Scena principala a aplicatiei.
     */
    public void display(Stage primaryStage) {
        /** Crearea unui nou stage pentru fereastra initiala */
        Stage initialStage = new Stage();
        initialStage.setTitle("Bine ati venit!");

        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        initialStage.getIcons().add(icon);
        
        /** Crearea butoanelor pentru Conectare si Inregistrare */
        Button btnConectare = new Button("Conectare");
        Button btnInregistrare = new Button("Inregistrare");

        /** Setarea dimensiunii butoanelor */
        btnConectare.setPrefWidth(200);  
        btnInregistrare.setPrefWidth(200);

        /** Eveniment pentru butonul de Conectare */
        btnConectare.setOnAction(e -> {
            initialStage.close();
            new LoginW().display(primaryStage);
        });

        /** Eveniment pentru butonul de Inregistrare */
        btnInregistrare.setOnAction(e -> {
            initialStage.close();
            new InregistrareW().display(primaryStage);
        });

        /** Configurarea layout-ului */
        VBox layout = new VBox(20); 
        layout.getChildren().addAll(btnConectare, btnInregistrare);
        layout.setAlignment(Pos.CENTER); 

        /** Crearea si setarea scenei */
        Scene scene = new Scene(layout, 300, 200);
        initialStage.setScene(scene);
        initialStage.show();
    }
}
