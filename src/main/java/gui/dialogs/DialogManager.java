package gui.dialogs;

import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Libro;
import model.Libreria;

import java.util.List;
import java.util.Optional;

/**
 * Gestore centralizzato per tutti i dialog dell'applicazione
 * Implementa una versione semplificata del pattern Factory per la creazione degli alert per i Dialog
 */
public class DialogManager {

    private final Stage parentStage;
    private final Libreria libreria;

    public DialogManager(Stage parentStage, Libreria libreria) {
        this.parentStage = parentStage;
        this.libreria = libreria;
    }

    /**
     * Mostra il dialog per aggiungere un nuovo libro
     */
    public void mostraDialogAggiungiLibro() {
        LibroDialog dialog = new LibroDialog(parentStage);
        Optional<Libro> result = dialog.showAndWait();

        result.ifPresent(libro -> {
            if (libro != null) {
                libreria.aggiungiLibro(libro);
                mostraMessaggioSuccesso("Libro aggiunto con successo!");
            }
        });
    }

    /**
     * Mostra il dialog per modificare un libro esistente
     */
    public void mostraDialogModificaLibro(Libro libroSelezionato) {
        if (libroSelezionato == null) {
            mostraAvviso("Nessun libro selezionato", "Seleziona un libro da modificare");
            return;
        }

        LibroDialog dialog = new LibroDialog(parentStage, libroSelezionato);
        Optional<Libro> result = dialog.showAndWait();

        result.ifPresent(libroModificato -> {
            if (libroModificato != null) {
                libreria.modificaLibro(libroSelezionato, libroModificato);
                mostraMessaggioSuccesso("Libro modificato con successo!");
            }
        });
    }

    /**
     * Mostra il dialog di conferma per rimuovere un libro
     */
    public void mostraDialogRimuoviLibro(Libro libroSelezionato) {
        if (libroSelezionato == null) {
            mostraAvviso("Nessun libro selezionato", "Seleziona un libro da rimuovere");
            return;
        }

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma rimozione");
        conferma.setHeaderText(null);
        conferma.setContentText("Sei sicuro di voler rimuovere il libro \"" +
                libroSelezionato.getTitolo() + "\"?");
        conferma.initOwner(parentStage);

        conferma.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                libreria.rimuoviLibro(libroSelezionato);
                mostraMessaggioSuccesso("Libro rimosso con successo!");
            }
        });
    }

    /**
     * Mostra il dialog per la ricerca dei libri
     */
    public List<Libro> mostraDialogCerca() {
        CercaDialog dialog = new CercaDialog(parentStage, libreria);
        Optional<List<Libro>> result = dialog.showAndWait();

        return result.orElse(libreria.getLibri());
    }

    /**
     * Mostra un messaggio di successo
     */
    private void mostraMessaggioSuccesso(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.initOwner(parentStage);
        alert.showAndWait();
    }

    /**
     * Mostra un avviso
     */
    private void mostraAvviso(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.initOwner(parentStage);
        alert.showAndWait();
    }

    /**
     * Mostra un errore
     */
    public void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.initOwner(parentStage);
        alert.showAndWait();
    }
}