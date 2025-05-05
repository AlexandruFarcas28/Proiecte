package ecrane;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import proiect.DatabaseHandler;
import proiect.Utilizator;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


/**
 * Clasa CosW este responsabila pentru afisarea unui ecran de achitare a unui/unor produs(e) in aplicatie printr-o adresa de livrare si prin selectarea unei metode de plata.
 */
public class CosW {

    public void display(Stage primaryStage, Utilizator utilizatorAutentificat) {
        Stage cartStage = new Stage();
        /** Crearea unui nou stage pentru cosul de cumparaturi */
        cartStage.setTitle("Coșul de Cumpărături");
        /** Setarea unei iconite pentru fereastra */
        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        cartStage.getIcons().add(icon);

        /** Crearea tabelului pentru afisarea produselor din cos */
        TableView<Map<String, Object>> table = new TableView<>();

        TableColumn<Map<String, Object>, Integer> idColumn = new TableColumn<>("ID Produs");
        idColumn.setMinWidth(50);
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>((Integer) cellData.getValue().get("produs_id")));

        TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("Nume Produs");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>((String) cellData.getValue().get("nume")));

        TableColumn<Map<String, Object>, String> brandColumn = new TableColumn<>("Marcă");
        brandColumn.setMinWidth(150);
        brandColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>((String) cellData.getValue().get("marca")));

        TableColumn<Map<String, Object>, Double> priceColumn = new TableColumn<>("Preț");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>((Double) cellData.getValue().get("pret")));

        TableColumn<Map<String, Object>, Integer> quantityColumn = new TableColumn<>("Cantitate");
        quantityColumn.setMinWidth(100);
        quantityColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>((Integer) cellData.getValue().get("cantitate")));

        table.getColumns().addAll(idColumn, nameColumn, brandColumn, priceColumn, quantityColumn);
        table.setItems(getCartItemsFromDatabase(utilizatorAutentificat.getId()));

        double maxPrice = getMaxPriceFromDatabase();
        
        Label minPriceLabel = new Label("Preț Minim: 1 Lei");
        Label maxPriceLabel = new Label("Preț Maxim: " + String.format("%.2f", maxPrice) + " Lei");

        /** Configurarea unui label pentru afisarea totalului */
        Label totalLabel = new Label("Total: 0.00 Lei");
        updateTotalLabel(utilizatorAutentificat.getId(), totalLabel);

        /** Optiuni pentru metoda de plata */
        RadioButton cashPayment = new RadioButton("Plată Ramburs");
        RadioButton cardPayment = new RadioButton("Plată cu Cardul");
        ToggleGroup paymentGroup = new ToggleGroup();
        cashPayment.setToggleGroup(paymentGroup);
        cardPayment.setToggleGroup(paymentGroup);
        cashPayment.setSelected(true);

        /** Formular pentru plata cu cardul */
        VBox cardForm = new VBox(10);
        cardForm.setVisible(false);

        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Numărul Cardului");
        cardNumberField.setMaxWidth(200); 

        TextField cardHolderField = new TextField();
        cardHolderField.setPromptText("Numele Posesorului");
        cardHolderField.setMaxWidth(200);  

        PasswordField cardCvvField = new PasswordField();
        cardCvvField.setPromptText("Codul de Securitate");
        cardCvvField.setMaxWidth(200);  

        DatePicker expiryDateField = new DatePicker();
        expiryDateField.setPromptText("Data Expirare (MM/YYYY)");

        CheckBox saveCardCheckbox = new CheckBox("Salvați cardul pentru comenzi viitoare");

        ComboBox<String> savedCards = new ComboBox<>();
        savedCards.setPromptText("Selectați un card salvat");

        populateSavedCards(utilizatorAutentificat.getId(), savedCards, cardNumberField, cardHolderField, cardCvvField, expiryDateField);

        cardForm.getChildren().addAll(savedCards, cardNumberField, cardHolderField, cardCvvField, expiryDateField, saveCardCheckbox);

        cardPayment.setOnAction(e -> cardForm.setVisible(true));
        cashPayment.setOnAction(e -> cardForm.setVisible(false));

        /** Buton Inapoi */
        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> cartStage.close());

        VBox addressForm = new VBox(10);

        /** Câmpuri de text pentru adresa utilizatorului */
        TextField countryField = new TextField();
        countryField.setPromptText("Țara");
        countryField.setMaxWidth(200);
        TextField countyField = new TextField();
        countyField.setPromptText("Județ");
        countyField.setMaxWidth(200);
        TextField cityField = new TextField();
        cityField.setPromptText("Oraș/Comuna");
        cityField.setMaxWidth(200);
        TextField streetField = new TextField();
        streetField.setPromptText("Strada");
        streetField.setMaxWidth(200);
        TextField numberField = new TextField();
        numberField.setPromptText("Număr");
        numberField.setMaxWidth(200);
        TextField postalCodeField = new TextField();
        postalCodeField.setPromptText("Cod Poștal");
        postalCodeField.setMaxWidth(200);
        TextField blockField = new TextField();
        blockField.setPromptText("Bloc");
        blockField.setMaxWidth(200);
        TextField stairField = new TextField();
        stairField.setPromptText("Scara");
        stairField.setMaxWidth(200);
        TextField apartmentField = new TextField();
        apartmentField.setPromptText("Apartament");
        apartmentField.setMaxWidth(200);

        /** ComboBox pentru selectarea adreselor salvate */
        ComboBox<String> savedAddresses = new ComboBox<>();
        savedAddresses.setPromptText("Selectează o adresă salvată");

        populateSavedAddresses(utilizatorAutentificat.getId(), savedAddresses);

        savedAddresses.setOnAction(e -> {
            String selectedAddress = savedAddresses.getValue();
            if (selectedAddress != null) {
                fillAddressFields(selectedAddress, countryField, countyField, cityField, streetField, numberField, postalCodeField, blockField, stairField, apartmentField);
            }
        });

        addressForm.getChildren().addAll(savedAddresses, countryField, countyField, cityField, streetField, numberField, postalCodeField, blockField, stairField, apartmentField);
        
        /** Buton pentru plasarea comenzii */
        Button purchaseButton = new Button("Plasează Comanda");
        purchaseButton.setOnAction(e -> {
            if (getCartItemsFromDatabase(utilizatorAutentificat.getId()).isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Coșul este gol. Nu puteți plasa comanda.");
                return; 
            }

            if (countryField.getText().isEmpty() || countyField.getText().isEmpty() || cityField.getText().isEmpty() ||
                streetField.getText().isEmpty() || numberField.getText().isEmpty() || postalCodeField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Vă rugăm să completați toate câmpurile pentru adresă.");
                return;
            }

            /** Salvăm adresa de livrare în baza de date */
            saveDeliveryAddress(utilizatorAutentificat.getId(), countryField.getText(), countyField.getText(), cityField.getText(),
                    streetField.getText(), numberField.getText(), postalCodeField.getText(), blockField.getText(),
                    stairField.getText(), apartmentField.getText());

            if (cashPayment.isSelected()) {
                boolean success = purchaseItems(utilizatorAutentificat.getId(), "Ramburs", null);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Produsele au fost achitate ramburs!");
                    table.setItems(getCartItemsFromDatabase(utilizatorAutentificat.getId()));
                    updateTotalLabel(utilizatorAutentificat.getId(), totalLabel);
                }
            } else if (cardPayment.isSelected()) {
                String cardNumber = cardNumberField.getText();
                String cardHolder = cardHolderField.getText();
                String cardCvv = cardCvvField.getText();
                LocalDate expiryDate = expiryDateField.getValue();

                if (!validateCardDetails(cardNumber, cardHolder, cardCvv, expiryDate)) {
                    return;
                }

                Map<String, String> cardDetails = new HashMap<>();
                cardDetails.put("cardNumber", cardNumber);
                cardDetails.put("cardHolder", cardHolder);
                cardDetails.put("cardCvv", cardCvv);
                cardDetails.put("expiryDate", expiryDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));

                boolean success = purchaseItems(utilizatorAutentificat.getId(), "Card", cardDetails);
                if (success) {
                    if (saveCardCheckbox.isSelected()) {
                        saveCard(utilizatorAutentificat.getId(), cardNumber, cardHolder, cardCvv, expiryDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Produsele au fost achitate cu succes!");
                    table.setItems(getCartItemsFromDatabase(utilizatorAutentificat.getId()));
                    updateTotalLabel(utilizatorAutentificat.getId(), totalLabel);
                }
            }
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(new VBox(10, table, totalLabel, cashPayment, cardPayment, cardForm, addressForm, purchaseButton, backButton));
        scrollPane.setFitToWidth(true); 

        Scene scene = new Scene(scrollPane, 800, 600);
        cartStage.setScene(scene);
        cartStage.show();
    }

    private double getMaxPriceFromDatabase() {
        double maxPrice = 0;
        try {
            String query = "SELECT MAX(pret) AS max_price FROM produse";
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maxPrice = rs.getDouble("max_price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxPrice;
    }

    /**
     * Valideaza detaliile unui card.
     * @param cardNumber Numarul cardului.
     * @param cardHolder Numele posesorului cardului.
     * @param cardCvv Codul CVV al cardului.
     * @param expiryDate Data de expirare a cardului.
     * @return True daca detaliile sunt valide, altfel false.
     */
    
    private boolean validateCardDetails(String cardNumber, String cardHolder, String cardCvv, LocalDate expiryDate) {
        if (cardNumber.isEmpty() || cardHolder.isEmpty() || cardCvv.isEmpty() || expiryDate == null) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "Toate câmpurile pentru card trebuie completate.");
            return false;
        }

        if (!cardNumber.matches("\\d{16}")) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "Numărul cardului trebuie să conțină exact 16 cifre.");
            return false;
        }

        if (!cardHolder.matches("[a-zA-Z ]+")) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "Numele posesorului trebuie să conțină doar caractere.");
            return false;
        }

        if (!cardCvv.matches("\\d{3}")) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "Codul CVV trebuie să conțină exact 3 cifre.");
            return false;
        }

        if (expiryDate.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "Cardul este expirat.");
            return false;
        }

        return true;
    }
    
    
    /**
     * Obtine produsele din cosul utilizatorului din baza de date.
     * @param userId ID-ul utilizatorului.
     * @return Lista de produse din cos.
     */

    private ObservableList<Map<String, Object>> getCartItemsFromDatabase(int userId) {
        ObservableList<Map<String, Object>> cartItems = FXCollections.observableArrayList();
        try {
            String query = """
                SELECT produs_id, nume, marca, taraOrigine, pret, SUM(cantitate) AS cantitate 
                FROM cos_cumparaturi 
                WHERE user_id = ? 
                GROUP BY produs_id
            """;
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("produs_id", rs.getInt("produs_id"));
                item.put("nume", rs.getString("nume"));
                item.put("marca", rs.getString("marca"));
                item.put("taraOrigine", rs.getString("taraOrigine"));
                item.put("pret", rs.getDouble("pret"));
                item.put("cantitate", rs.getInt("cantitate"));
                cartItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    /**
     * Plaseaza produsele din cosul utilizatorului.
     * @param userId ID-ul utilizatorului.
     * @param paymentMethod Metoda de plata (Ramburs sau Card).
     * @param cardDetails Detalii despre card, daca metoda de plata este Card.
     * @return True daca comanda a fost plasata cu succes, altfel false.
     */
    
    private boolean purchaseItems(int userId, String paymentMethod, Map<String, String> cardDetails) {
        try {
            var conn = DatabaseHandler.getInstance().getConnection();
            conn.setAutoCommit(false);

            String moveToHistoryQuery = """
                INSERT INTO istoric_comenzi (user_id, produs_id, nume, marca, taraOrigine, pret, cantitate, data_comanda, metoda_plata)
                SELECT ?, produs_id, nume, marca, taraOrigine, pret, SUM(cantitate), datetime('now'), ? 
                FROM cos_cumparaturi
                WHERE user_id = ? 
                GROUP BY produs_id, nume, marca, taraOrigine, pret
            """;
            var stmt = conn.prepareStatement(moveToHistoryQuery);
            stmt.setInt(1, userId);
            stmt.setString(2, paymentMethod);
            stmt.setInt(3, userId);
            stmt.executeUpdate();

            String clearCartQuery = "DELETE FROM cos_cumparaturi WHERE user_id = ?";
            var clearCartStmt = conn.prepareStatement(clearCartQuery);
            clearCartStmt.setInt(1, userId);
            clearCartStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    /**
     * Salveaza detaliile unui card nou in baza de date.
     * @param userId ID-ul utilizatorului.
     * @param cardNumber Numarul cardului.
     * @param cardHolder Numele posesorului cardului.
     * @param cardCvv Codul CVV al cardului.
     * @param expiryDate Data de expirare a cardului.
     */
    private void saveCard(int userId, String cardNumber, String cardHolder, String cardCvv, String expiryDate) {
        try {
            String query = """
                INSERT INTO carduri_utilizatori (user_id, numar_card, nume_posesor, cod_securitate, data_expirare)
                VALUES (?, ?, ?, ?, ?)
            """;
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, cardNumber);
            stmt.setString(3, cardHolder);
            stmt.setString(4, cardCvv);
            stmt.setString(5, expiryDate); 
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Populeaza ComboBox-ul cu cardurile salvate ale utilizatorului.
     * @param userId ID-ul utilizatorului.
     * @param savedCards ComboBox-ul care va contine optiunile cardurilor salvate.
     * @param cardNumberField Campul pentru numarul cardului.
     * @param cardHolderField Campul pentru numele posesorului cardului.
     * @param cardCvvField Campul pentru codul CVV.
     * @param expiryDateField Campul pentru data de expirare.
     */

    private void populateSavedCards(int userId, ComboBox<String> savedCards, TextField cardNumberField, TextField cardHolderField, PasswordField cardCvvField, DatePicker expiryDateField) {
        try {
            String query = "SELECT numar_card, nume_posesor, cod_securitate, data_expirare FROM carduri_utilizatori WHERE user_id = ?";
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            /** Map pentru a lega datele cardului cu opțiunile din ComboBox */
            Map<String, Map<String, String>> cardDetailsMap = new HashMap<>();

            while (rs.next()) {
                /** Conversie corectă a datei de expirare */
                String expiryDateString = rs.getString("data_expirare");
          
                String fullExpiryDate = "01/" + expiryDateString; 
                
                LocalDate expiryDate = LocalDate.parse(fullExpiryDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                
                if (expiryDate.isBefore(LocalDate.now())) {
                    continue; 
                }

                String cardInfo = rs.getString("numar_card") + " - " + rs.getString("nume_posesor");
                savedCards.getItems().add(cardInfo);

               
                Map<String, String> cardDetails = new HashMap<>();
                cardDetails.put("numar_card", rs.getString("numar_card"));
                cardDetails.put("nume_posesor", rs.getString("nume_posesor"));
                cardDetails.put("cod_securitate", rs.getString("cod_securitate"));
                cardDetails.put("data_expirare", rs.getString("data_expirare"));
                cardDetailsMap.put(cardInfo, cardDetails);
            }

            savedCards.setOnAction(e -> {
                String selectedCard = savedCards.getValue();
                if (selectedCard != null && cardDetailsMap.containsKey(selectedCard)) {
                    Map<String, String> selectedDetails = cardDetailsMap.get(selectedCard);
                    cardNumberField.setText(selectedDetails.get("numar_card"));
                    cardHolderField.setText(selectedDetails.get("nume_posesor"));
                    cardCvvField.setText(selectedDetails.get("cod_securitate"));
                    
                    String expiryDateStr = selectedDetails.get("data_expirare");
                    String fullExpiryDateStr = "01/" + expiryDateStr;  
                    LocalDate expiryDate = LocalDate.parse(fullExpiryDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    expiryDateField.setValue(expiryDate);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Populeaza ComboBox-ul cu adresele salvate ale utilizatorului.
     * @param userId ID-ul utilizatorului.
     * @param savedAddresses ComboBox-ul care va contine optiunile adreselor salvate.
     */
    
    private void populateSavedAddresses(int userId, ComboBox<String> savedAddresses) {
        try {
            String query = "SELECT CONCAT(tara, ' ', judet, ' ', oras_comuna, ' ', strada, ' ', numar) AS full_address FROM adrese_livrare WHERE user_id = ?";
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                savedAddresses.getItems().add(rs.getString("full_address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Populeaza campurile de text cu detaliile adresei selectate.
     * @param selectedAddress Adresa selectata.
     * @param countryField Campul pentru tara.
     * @param countyField Campul pentru judet.
     * @param cityField Campul pentru oras/comuna.
     * @param streetField Campul pentru strada.
     * @param numberField Campul pentru numar.
     * @param postalCodeField Campul pentru cod postal.
     * @param blockField Campul pentru bloc.
     * @param stairField Campul pentru scara.
     * @param apartmentField Campul pentru apartament.
     */

    private void fillAddressFields(String selectedAddress, TextField countryField, TextField countyField, TextField cityField,
                                   TextField streetField, TextField numberField, TextField postalCodeField, TextField blockField,
                                   TextField stairField, TextField apartmentField) {
        try {
            String query = "SELECT * FROM adrese_livrare WHERE CONCAT(tara, ' ', judet, ' ', oras_comuna, ' ', strada, ' ', numar) = ?";
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setString(1, selectedAddress);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                countryField.setText(rs.getString("tara"));
                countyField.setText(rs.getString("judet"));
                cityField.setText(rs.getString("oras_comuna"));
                streetField.setText(rs.getString("strada"));
                numberField.setText(rs.getString("numar"));
                postalCodeField.setText(rs.getString("cod_postal"));
                blockField.setText(rs.getString("bloc"));
                stairField.setText(rs.getString("scara"));
                apartmentField.setText(rs.getString("apartament"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Salveaza o adresa de livrare in baza de date.
     * @param userId ID-ul utilizatorului.
     * @param country Tara utilizatorului.
     * @param county Judetul utilizatorului.
     * @param city Orasul/comuna utilizatorului.
     * @param street Strada utilizatorului.
     * @param number Numarul strazii.
     * @param postalCode Codul postal.
     * @param block Blocul.
     * @param stair Scara.
     * @param apartment Apartamentul.
     */
    private void saveDeliveryAddress(int userId, String country, String county, String city, String street, String number,
                                     String postalCode, String block, String stair, String apartment) {
        try {
            String query = """
                INSERT INTO adrese_livrare (user_id, tara, judet, oras_comuna, strada, numar, cod_postal, bloc, scara, apartament)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, country);
            stmt.setString(3, county);
            stmt.setString(4, city);
            stmt.setString(5, street);
            stmt.setString(6, number);
            stmt.setString(7, postalCode);
            stmt.setString(8, block);
            stmt.setString(9, stair);
            stmt.setString(10, apartment);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualizeaza label-ul care afiseaza totalul cosului.
     * @param userId ID-ul utilizatorului.
     * @param totalLabel Label-ul care afiseaza totalul.
     */
    
    private void updateTotalLabel(int userId, Label totalLabel) {
        try {
            String query = "SELECT SUM(pret * cantitate) AS total FROM cos_cumparaturi WHERE user_id = ?";
            var stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            stmt.setInt(1, userId);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                totalLabel.setText("Total: " + String.format("%.2f", total) + " Lei");
            }
        } catch (Exception e) {
            e.printStackTrace();
            totalLabel.setText("Total: Eroare");
        }
    }

    /**
     * Afiseaza o alerta.
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
