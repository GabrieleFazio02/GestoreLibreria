package gui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Libro;
import model.Libreria;
import is.strategy.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialog specializzato per la ricerca dei libri
 * Implementa i pattern Strategy per la ricerca
 */
public class CercaDialog extends Dialog<List<Libro>> {

    private final Libreria libreria;
    private TextField cercaTitolo;
    private TextField cercaAutore;
    private TextField cercaISBN;
    private ComboBox<String> cercaGenere;
    private ComboBox<Libro.StatoLettura> cercaStato;
    private Spinner<Integer> cercaAnnoMin;
    private Spinner<Integer> cercaAnnoMax;
    private Spinner<Integer> cercaValutazioneMin;
    private ComboBox<String> tipoCercaCombo;

    public CercaDialog(Stage owner, Libreria libreria) {
        this.libreria = libreria;

        setTitle("Ricerca Avanzata");
        setHeaderText("Inserisci i criteri di ricerca");
        initOwner(owner);

        createContent();
        setupButtons();
        setupResultConverter();
    }

    private void createContent() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // ComboBox per tipo di ricerca - USANDO I PATTERN STRATEGY
        tipoCercaCombo = new ComboBox<>();
        tipoCercaCombo.getItems().addAll("Ricerca per Titolo", "Ricerca per Autore", "Ricerca per ISBN", "Ricerca Avanzata");
        tipoCercaCombo.setValue("Ricerca Avanzata");

        // Campi di ricerca
        cercaTitolo = new TextField();
        cercaTitolo.setPromptText("Inserisci parte del titolo...");

        cercaAutore = new TextField();
        cercaAutore.setPromptText("Inserisci parte dell'autore...");

        cercaISBN = new TextField();
        cercaISBN.setPromptText("Inserisci ISBN completo...");

        // ComboBox genere
        cercaGenere = new ComboBox<>();
        cercaGenere.getItems().add("Tutti i generi");
        cercaGenere.getItems().addAll(
                libreria.getLibri().stream()
                        .map(Libro::getGenere)
                        .distinct()
                        .sorted(String.CASE_INSENSITIVE_ORDER)
                        .collect(Collectors.toList())
        );
        cercaGenere.setValue("Tutti i generi");

        // ComboBox stato
        cercaStato = new ComboBox<>();
        cercaStato.getItems().add(null); // Per "Tutti gli stati"
        cercaStato.getItems().addAll(Libro.StatoLettura.values());
        cercaStato.setPromptText("Tutti gli stati");

        // Spinner per range di anni
        int annoMin = libreria.getLibri().stream()
                .mapToInt(Libro::getAnnoPubblicazione)
                .min().orElse(1900);
        int annoMax = libreria.getLibri().stream()
                .mapToInt(Libro::getAnnoPubblicazione)
                .max().orElse(2024);

        cercaAnnoMin = new Spinner<>(annoMin, annoMax, annoMin);
        cercaAnnoMax = new Spinner<>(annoMin, annoMax, annoMax);

        // Spinner per valutazione minima
        cercaValutazioneMin = new Spinner<>(0, 5, 0);

        // Layout
        int row = 0;
        grid.add(new Label("Tipo Ricerca:"), 0, row);
        grid.add(tipoCercaCombo, 1, row++);

        grid.add(new Label("Titolo:"), 0, row);
        grid.add(cercaTitolo, 1, row++);

        grid.add(new Label("Autore:"), 0, row);
        grid.add(cercaAutore, 1, row++);

        grid.add(new Label("ISBN:"), 0, row);
        grid.add(cercaISBN, 1, row++);

        grid.add(new Label("Genere:"), 0, row);
        grid.add(cercaGenere, 1, row++);

        grid.add(new Label("Stato:"), 0, row);
        grid.add(cercaStato, 1, row++);

        grid.add(new Label("Anno da:"), 0, row);
        grid.add(cercaAnnoMin, 1, row++);

        grid.add(new Label("Anno a:"), 0, row);
        grid.add(cercaAnnoMax, 1, row++);

        grid.add(new Label("Valutazione min:"), 0, row);
        grid.add(cercaValutazioneMin, 1, row++);

        // Listener per abilitare/disabilitare campi in base al tipo di ricerca
        tipoCercaCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFieldsVisibility(newValue);
        });

        updateFieldsVisibility(tipoCercaCombo.getValue());

        getDialogPane().setContent(grid);
    }

    private void updateFieldsVisibility(String tipoRicerca) {
        boolean isAvanzata = "Ricerca Avanzata".equals(tipoRicerca);
        boolean isTitolo = "Ricerca per Titolo".equals(tipoRicerca);
        boolean isAutore = "Ricerca per Autore".equals(tipoRicerca);
        boolean isISBN = "Ricerca per ISBN".equals(tipoRicerca);

        cercaTitolo.setDisable(!isAvanzata && !isTitolo);
        cercaAutore.setDisable(!isAvanzata && !isAutore);
        cercaISBN.setDisable(!isAvanzata && !isISBN);
        cercaGenere.setDisable(!isAvanzata);
        cercaStato.setDisable(!isAvanzata);
        cercaAnnoMin.setDisable(!isAvanzata);
        cercaAnnoMax.setDisable(!isAvanzata);
        cercaValutazioneMin.setDisable(!isAvanzata);

        if (!isAvanzata) {
            resetNonActiveFields(tipoRicerca);
        }
    }

    private void resetNonActiveFields(String tipoRicerca) {
        if (!"Ricerca per Titolo".equals(tipoRicerca)) {
            cercaTitolo.clear();
        }
        if (!"Ricerca per Autore".equals(tipoRicerca)) {
            cercaAutore.clear();
        }
        if (!"Ricerca per ISBN".equals(tipoRicerca)) {
            cercaISBN.clear();
        }
    }

    private void setupButtons() {
        ButtonType cercaButtonType = new ButtonType("Cerca", ButtonBar.ButtonData.OK_DONE);
        ButtonType resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.OTHER);
        ButtonType annullaButtonType = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(cercaButtonType, resetButtonType, annullaButtonType);

        // Pulsante Reset
        Button resetButton = (Button) getDialogPane().lookupButton(resetButtonType);
        resetButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            resetFields();
            event.consume(); // Previene la chiusura del dialog
        });
    }

    private void resetFields() {
        cercaTitolo.clear();
        cercaAutore.clear();
        cercaISBN.clear();
        cercaGenere.setValue("Tutti i generi");
        cercaStato.setValue(null);
        cercaAnnoMin.getValueFactory().setValue(
                libreria.getLibri().stream()
                        .mapToInt(Libro::getAnnoPubblicazione)
                        .min().orElse(1900)
        );
        cercaAnnoMax.getValueFactory().setValue(
                libreria.getLibri().stream()
                        .mapToInt(Libro::getAnnoPubblicazione)
                        .max().orElse(2024)
        );
        cercaValutazioneMin.getValueFactory().setValue(0);
        tipoCercaCombo.setValue("Ricerca Avanzata");
    }

    private void setupResultConverter() {
        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return eseguiRicerca();
            }
            return null;
        });
    }

    private List<Libro> eseguiRicerca() {
        String tipoRicerca = tipoCercaCombo.getValue();

        // IMPLEMENTAZIONE CORRETTA DEI PATTERN STRATEGY
        switch (tipoRicerca) {
            case "Ricerca per Titolo":
                if (!cercaTitolo.getText().trim().isEmpty()) {
                    libreria.setRicStrategy(new RicPerTitolo());
                    return libreria.cercaLib(cercaTitolo.getText().trim());
                }
                break;

            case "Ricerca per Autore":
                if (!cercaAutore.getText().trim().isEmpty()) {
                    libreria.setRicStrategy(new RicPerAutore());
                    return libreria.cercaLib(cercaAutore.getText().trim());
                }
                break;

            case "Ricerca per ISBN":
                if (!cercaISBN.getText().trim().isEmpty()) {
                    libreria.setRicStrategy(new RicPerISBN());
                    return libreria.cercaLib(cercaISBN.getText().trim());
                }
                break;

            case "Ricerca Avanzata":
                return eseguiRicercaAvanzata();
        }

        // Se non ci sono criteri, restituisce tutti i libri
        return libreria.getLibri();
    }

    private List<Libro> eseguiRicercaAvanzata() {
        // Per la ricerca avanzata, usiamo il filtering manuale
        // In alternativa, potresti creare una RicStrategy combinata
        return libreria.getLibri().stream()
                .filter(libro -> {
                    // Filtro per titolo
                    boolean matchTitolo = cercaTitolo.getText().isEmpty() ||
                            libro.getTitolo().toLowerCase().contains(cercaTitolo.getText().toLowerCase());

                    // Filtro per autore
                    boolean matchAutore = cercaAutore.getText().isEmpty() ||
                            libro.getAutore().toLowerCase().contains(cercaAutore.getText().toLowerCase());

                    // Filtro per ISBN
                    boolean matchISBN = cercaISBN.getText().isEmpty() ||
                            libro.getIsbn().equals(cercaISBN.getText());

                    // Filtro per genere
                    boolean matchGenere = cercaGenere.getValue().equals("Tutti i generi") ||
                            libro.getGenere().equals(cercaGenere.getValue());

                    // Filtro per stato
                    boolean matchStato = cercaStato.getValue() == null ||
                            libro.getStatoLettura() == cercaStato.getValue();

                    // Filtro per anno
                    boolean matchAnno = libro.getAnnoPubblicazione() >= cercaAnnoMin.getValue() &&
                            libro.getAnnoPubblicazione() <= cercaAnnoMax.getValue();

                    // Filtro per valutazione
                    boolean matchValutazione = libro.getValutazione() >= cercaValutazioneMin.getValue();

                    return matchTitolo && matchAutore && matchISBN && matchGenere && matchStato && matchAnno && matchValutazione;
                })
                .collect(Collectors.toList());
    }
}