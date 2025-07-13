package gui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Libro;
import util.ISBNvalido;

public class LibroDialog extends Dialog<Libro> {
    private final TextField titoloField = new TextField();
    private final TextField autoreField = new TextField();
    private final TextField isbnField = new TextField();
    private final TextField genereField = new TextField();
    private final TextField annoField = new TextField();
    private final ComboBox<Libro.StatoLettura> statoCombo = new ComboBox<>();
    private final Spinner<Integer> valutazioneSpinner;


    public LibroDialog(Stage owner) {
        this(owner, null);
    }

    public LibroDialog(Stage owner, Libro libro) {
        setTitle(libro == null ? "Aggiungi Libro" : "Modifica Libro");
        setHeaderText(null);
        initOwner(owner);

        // Creazione del form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Titolo:"), 0, 0);
        grid.add(titoloField, 1, 0);

        grid.add(new Label("Autore:"), 0, 1);
        grid.add(autoreField, 1, 1);

        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);

        grid.add(new Label("Genere:"), 0, 3);
        grid.add(genereField, 1, 3);

        grid.add(new Label("Anno:"), 0, 4);
        grid.add(annoField, 1, 4);

        statoCombo.getItems().addAll(Libro.StatoLettura.values());
        grid.add(new Label("Stato:"), 0, 5);
        grid.add(statoCombo, 1, 5);

        valutazioneSpinner = new Spinner<>(0, 5, 0, 1);
        grid.add(new Label("Valutazione:"), 0, 6);
        grid.add(valutazioneSpinner, 1, 6);

        // Inizialmente disabilita la valutazione
        valutazioneSpinner.setDisable(true);

        // Listener per abilitare/disabilitare la valutazione in base allo stato
        statoCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Libro.StatoLettura.LETTO) {
                valutazioneSpinner.setDisable(false);
            } else {
                valutazioneSpinner.setDisable(true);
                valutazioneSpinner.getValueFactory().setValue(0); // Reset a 0
            }
        });

        getDialogPane().setContent(grid);

        // Popolamento campi se in modalità modifica
        if (libro != null) {
            titoloField.setText(libro.getTitolo());
            autoreField.setText(libro.getAutore());
            isbnField.setText(libro.getIsbn());
            genereField.setText(libro.getGenere());
            annoField.setText(String.valueOf(libro.getAnnoPubblicazione()));
            statoCombo.setValue(libro.getStatoLettura());
            valutazioneSpinner.getValueFactory().setValue(libro.getValutazione());

            // Abilita/disabilita la valutazione in base allo stato del libro esistente
            if (libro.getStatoLettura() == Libro.StatoLettura.LETTO) {
                valutazioneSpinner.setDisable(false);
            } else {
                valutazioneSpinner.setDisable(true);
                valutazioneSpinner.getValueFactory().setValue(0);
            }
        }

        // Bottoni
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        // Validazione e prevenzione chiusura dialog
        Button okButton = (Button) getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validaDati()) {
                event.consume(); // Previene la chiusura del dialog
            }
        });

        // Conversione risultato
        setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeOk) {
                try {
                    int anno = Integer.parseInt(annoField.getText());
                    Libro nuovoLibro = new Libro(
                            titoloField.getText(),
                            autoreField.getText(),
                            isbnField.getText(),
                            genereField.getText(),
                            anno
                    );
                    nuovoLibro.setStatoLettura(statoCombo.getValue());
                    if (statoCombo.getValue() == Libro.StatoLettura.LETTO) {
                        nuovoLibro.setValutazione(valutazioneSpinner.getValue());
                    }
                    /*Rimosso il blocco else, in conflitto con il metodo setvalutazione() della classe Libro
                    che sollevava l'eccezione quando si scegleva uno stato diverso da LETTO
                    l'else prima settava comunque la valutazione a 0 (impossibile se non LETTO)*/
                    return nuovoLibro;
                } catch (NumberFormatException e) {
                    // Questo non dovrebbe mai accadere perché la validazione è già stata fatta
                    return null;
                }
            }
            return null;
        });
    }

    /**
     * Controlla i dati inseriti nei form
     * restituisce true se tutto è corretto
     * richiama il metodo mostraErrore altrimenti e ritorna false
     */
    private boolean validaDati() {
        // Verifica che i campi obbligatori non siano vuoti
        if (titoloField.getText().trim().isEmpty()) {
            mostraErrore("Il titolo è obbligatorio");
            titoloField.requestFocus();
            return false;
        }

        if (autoreField.getText().trim().isEmpty()) {
            mostraErrore("L'autore è obbligatorio");
            autoreField.requestFocus();
            return false;
        }

        if (genereField.getText().trim().isEmpty()) {
            mostraErrore("Il genere è obbligatorio");
            genereField.requestFocus();
            return false;
        }

        if (statoCombo.getValue() == null) {
            mostraErrore("Lo stato di lettura è obbligatorio");
            statoCombo.requestFocus();
            return false;
        }


        // Verifica che l'anno sia un numero valido
        try {
            int anno = Integer.parseInt(annoField.getText().trim());
            if (anno < 1000 || anno > 2030) {
                mostraErrore("L'anno deve essere compreso tra 1000 e 2030");
                annoField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostraErrore("L'anno deve essere un numero valido");
            annoField.requestFocus();
            return false;
        }

        String isbn = isbnField.getText().trim();
        if(!ISBNvalido.isISBN(isbn)){
            mostraErrore("ISBN non plausibile. Deve avere:\n"
                    + "• 10 cifre  oppure\n"
                    + "• 13 cifre che iniziano con 978/979 seguite da - , ad es. 978-1234567890");
            isbnField.requestFocus();
            return false;
        }

        return true;
    }

    //Messaggio di errore
    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di validazione");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.initOwner(getDialogPane().getScene().getWindow());
        alert.showAndWait();
    }
}