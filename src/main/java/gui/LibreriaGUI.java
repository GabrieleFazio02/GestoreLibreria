package gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Libreria;
import model.Libro;
import observer.Observer;
import gui.dialogs.DialogManager;
import is.strategy.*;

import java.util.List;

/**
 * Classe principale della GUI
 */
public class LibreriaGUI extends Application implements Observer {

    private Libreria libreria;
    private ListView<Libro> listaLibri;
    private Stage primaryStage;
    private DialogManager dialogManager;
    private Label statusBar;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.libreria = new Libreria();
        this.dialogManager = new DialogManager(primaryStage, libreria);

        setupUI();
        setupData();
    }

    private void setupUI() {
        // Inizializzazione componenti
        listaLibri = new ListView<>();
        ObservableList<Libro> items = FXCollections.observableArrayList();
        listaLibri.setItems(items);

        // Layout principale
        BorderPane root = new BorderPane();

        // Menu, toolbar e lista
        root.setTop(createMenuBar());
        root.setCenter(createCenterPanel());
        root.setBottom(createStatusBar());

        // Configurazione scena
        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("Gestore Libreria Personale");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupData() {
        libreria.attach(this);
        updateListView();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // Menu File
        Menu fileMenu = new Menu("File");
        MenuItem salvaItem = new MenuItem("Salva");
        MenuItem caricaItem = new MenuItem("Carica");
        fileMenu.getItems().addAll(salvaItem, caricaItem);

        // Menu Ordina
        Menu ordinaMenu = new Menu("Ordina");
        MenuItem ordinaTitoloItem = new MenuItem("Per Titolo");
        MenuItem ordinaAnnoItem = new MenuItem("Per Anno");
        MenuItem ordinaAutoreItem = new MenuItem("Per Autore");
        ordinaMenu.getItems().addAll(ordinaTitoloItem, ordinaAnnoItem, ordinaAutoreItem);

        menuBar.getMenus().addAll(fileMenu, ordinaMenu);

        // Event handlers
        salvaItem.setOnAction(e -> {
            try {
                libreria.salvaLib();
                statusBar.setText("Libreria salvata con successo");
            } catch (Exception ex) {
                dialogManager.mostraErrore("Errore", "Impossibile salvare: " + ex.getMessage());
            }
        });

        caricaItem.setOnAction(e -> {
            try {
                libreria.caricaLib();
                statusBar.setText("Libreria caricata con successo");
            } catch (Exception ex) {
                dialogManager.mostraErrore("Errore", "Impossibile caricare: " + ex.getMessage());
            }
        });

        // IMPLEMENTAZIONE PATTERN STRATEGY
        ordinaTitoloItem.setOnAction(e -> {
            libreria.setOrdStrategy(new OrdPerTitolo());
            libreria.ordinaLib();
            statusBar.setText("Libri ordinati per titolo");
        });

        ordinaAnnoItem.setOnAction(e -> {
            libreria.setOrdStrategy(new OrdPerAnno());
            libreria.ordinaLib();
            statusBar.setText("Libri ordinati per anno");
        });

        ordinaAutoreItem.setOnAction(e -> {
            libreria.setOrdStrategy(new OrdPerAutore());
            libreria.ordinaLib();
            statusBar.setText("Libri ordinati per autore");
        });

        return menuBar;
    }

    private VBox createCenterPanel() {
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10));

        // Toolbar
        ToolBar toolBar = createToolBar();

        // ListView con stile migliorato
        setupListView();

        vBox.getChildren().addAll(toolBar, listaLibri);
        return vBox;
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();

        Button aggiungiBtn = new Button("Aggiungi");
        Button modificaBtn = new Button("Modifica");
        Button rimuoviBtn = new Button("Rimuovi");
        Button cercaBtn = new Button("Cerca");
        Button mostraTuttiBtn = new Button("Mostra Tutti");

        toolBar.getItems().addAll(
                aggiungiBtn, new Separator(),
                modificaBtn, rimuoviBtn, new Separator(),
                cercaBtn, mostraTuttiBtn
        );

        // Event handlers delegati al DialogManager
        aggiungiBtn.setOnAction(e -> dialogManager.mostraDialogAggiungiLibro());
        modificaBtn.setOnAction(e -> dialogManager.mostraDialogModificaLibro(getSelectedBook()));
        rimuoviBtn.setOnAction(e -> dialogManager.mostraDialogRimuoviLibro(getSelectedBook()));
        cercaBtn.setOnAction(e -> {
            List<Libro> risultati = dialogManager.mostraDialogCerca();
            if (risultati != null) {
                mostraRisultatiRicerca(risultati);
            }
        });
        mostraTuttiBtn.setOnAction(e -> {
            updateListView();
            statusBar.setText("Visualizzazione completa ripristinata");
        });

        return toolBar;
    }

    private Label createStatusBar() {
        statusBar = new Label("Pronto");
        statusBar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5;");
        return statusBar;
    }

    private void setupListView() {
        listaLibri.setCellFactory(lv -> new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);
                if (empty || libro == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(createBookCell(libro));
                }
            }
        });
    }

    private VBox createBookCell(Libro libro) {
        VBox cellContainer = new VBox(5);
        cellContainer.setPadding(new Insets(5));

        // Prima riga: Titolo e Valutazione
        HBox primaRiga = new HBox(10);
        Label titolo = new Label(libro.getTitolo());
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label valutazione = new Label();
        if (libro.getStatoLettura() == Libro.StatoLettura.LETTO && libro.getValutazione() > 0) {
            valutazione.setText("★".repeat(libro.getValutazione()));
            valutazione.setStyle("-fx-text-fill: gold; -fx-font-size: 12px;");
        }

        primaRiga.getChildren().addAll(titolo, valutazione);

        // Seconda riga: Autore e Anno
        HBox secondaRiga = new HBox(10);
        Label autore = new Label("di " + libro.getAutore());
        autore.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
        Label anno = new Label("(" + libro.getAnnoPubblicazione() + ")");
        anno.setStyle("-fx-text-fill: #666;");
        Label ISBN = new Label("ISBN: " + libro.getIsbn());
        ISBN.setStyle("-fx-text-fill: #666;");
        secondaRiga.getChildren().addAll(autore, anno, ISBN);

        // Terza riga: Genere e Stato
        HBox terzaRiga = new HBox(10);
        Label genere = new Label(libro.getGenere());
        genere.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 2 6 2 6; -fx-background-radius: 10;");

        Label stato = new Label(libro.getStatoLettura().toString());
        String statoStyle = switch (libro.getStatoLettura()) {
            case LETTO -> "-fx-background-color: #c8e6c9; -fx-text-fill: #2e7d32;";
            case IN_LETTURA -> "-fx-background-color: #fff3e0; -fx-text-fill: #ef6c00;";
            case DA_LEGGERE -> "-fx-background-color: #ffcdd2; -fx-text-fill: #c62828;";
        };
        stato.setStyle(statoStyle + " -fx-padding: 2 6 2 6; -fx-background-radius: 10; -fx-font-size: 10px;");

        terzaRiga.getChildren().addAll(genere, stato);

        cellContainer.getChildren().addAll(primaRiga, secondaRiga, terzaRiga);
        return cellContainer;
    }

    // Metodi di utilità
    private Libro getSelectedBook() {
        return listaLibri.getSelectionModel().getSelectedItem();
    }

    private void mostraRisultatiRicerca(List<Libro> risultati) {
        listaLibri.getItems().clear();
        listaLibri.getItems().addAll(risultati);
        updateStatusBar();
        statusBar.setText("Trovati " + risultati.size() + " libri");
    }

    private void updateListView() {
        if (listaLibri != null) {
            listaLibri.getItems().clear();
            listaLibri.getItems().addAll(libreria.getLibri());
            updateStatusBar();
        }
    }

    private void updateStatusBar() {
        int totaleLibri = libreria.getLibri().size();
        long libriLetti = libreria.getLibri().stream()
                .filter(l -> l.getStatoLettura() == Libro.StatoLettura.LETTO)
                .count();

        statusBar.setText(String.format("Totale libri: %d | Libri letti: %d", totaleLibri, libriLetti));
    }

    // PATTERN OBSERVER
    @Override
    public void update(List<Libro> libri) {
        javafx.application.Platform.runLater(() -> {
            listaLibri.getItems().setAll(libri);
            updateStatusBar();
        });
    }

    //Lancia l'applicazione
    public static void main(String[] args) {
        launch(args);
    }
}