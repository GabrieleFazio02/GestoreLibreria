package model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import util.ISBNvalido;

import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione per la classe {@link Libreria}.
 * <p>Usa un file JSON temporaneo per non sporcare il vero archivio.</p>
 */
class LibreriaTest {

    private Libreria libreria;          // SUT
    private Path     tempJson;          // file temporaneo per SingletonJSON

    /*  ───────────────────────────  DATI DI TEST  ─────────────────────────── */

    private Libro libro1;
    private Libro libro2;
    private Libro libro3;

    @BeforeEach
    void setUp(@TempDir Path tempDir) throws Exception {

        /* --- inizializza file JSON fittizio per SingletonJSON --- */
        tempJson = tempDir.resolve("libreria_test.json");
        Files.writeString(tempJson, "[]");// JSON vuoto
        persistence.SingletonJSON.overridePath(tempJson);

        libreria = new Libreria(); // usa il JSON temporaneo

        /* --- libri di prova --- */
        libro1 = new Libro("Libro Uno", "AAAA","978-0000000001","Narrativo", 2020);
        libro2 = new Libro("Libro Due", "BBBB","978-0000000002", "Saggio",2021);
        libro3 = new Libro("Libro Tre", "CCCC", "978-0000000003",  "Fantasy", 2019);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(tempJson);   // pulizia file di test
    }

    /* ───────────────────────────  TEST DI BASE  ─────────────────────────── */

    @Test
    @DisplayName("Costruttore: libreria inizialmente vuota")
    void testCostruttore() {
        assertTrue(libreria.getLibri().isEmpty());
    }

    @Test
    @DisplayName("Aggiunta libro: successo")
    void testAggiuntaLibro() {
        assertTrue(libreria.aggiungiLibro(libro1));
        assertEquals(1, libreria.getLibri().size());
        assertTrue(libreria.getLibri().contains(libro1));
    }

    @Test
    @DisplayName("Aggiunta libro: successo")
    void testAggiuntaLibro3() {
        assertTrue(libreria.aggiungiLibro(libro3));
        assertEquals(1, libreria.getLibri().size());
        assertTrue(libreria.getLibri().contains(libro3));
    }

    @Test
    @DisplayName("Aggiunta duplicato ISBN: fallisce")
    void testAggiuntaDuplicato() {
        libreria.aggiungiLibro(libro1);
        assertFalse(libreria.aggiungiLibro(new Libro("Altro Titolo", "DDDD","978-0000000001", "Narrativa",2022)));
        assertEquals(1, libreria.getLibri().size());
    }

    @Test
    @DisplayName("Rimozione libro: successo")
    void testRimozioneLibro() {
        libreria.aggiungiLibro(libro2);
        assertTrue(libreria.rimuoviLibro(libro2));
        assertTrue(libreria.getLibri().isEmpty());
    }

    @Test
    @DisplayName("Modifica libro: ISBN non duplicato ⇒ ok")
    void testModificaLibro() {
        libreria.aggiungiLibro(libro1);
        Libro nuovo = new Libro("Nuovo Titolo", "EEEE","978-0000000099", "Horror", 2022);
        assertTrue(libreria.modificaLibro(libro1, nuovo));
        assertTrue(libreria.getLibri().contains(nuovo));
    }

    @Test
    @DisplayName("Modifica libro: ISBN duplicato ⇒ fail")
    void testModificaLibroDuplicato() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);
        // provo a mettere a libro2 l'ISBN di libro1
        Libro clone2 = new Libro("Clone","AAAA","978-0000000001","Narrativa", 2022);
        assertFalse(libreria.modificaLibro(libro2, clone2));
        assertEquals(2, libreria.getLibri().size());
    }

    /* ───────────────────────  VALIDAZIONE ISBN  ───────────────────────── */

    @Test
    @DisplayName("Validator: accetta ISBN‑13 con prefisso e trattino")
    void testIsbnValidatorValido() {
        assertTrue(ISBNvalido.isISBN("978-1234567890"));
        assertFalse(ISBNvalido.isISBN("1234567890123"));     // manca prefisso 97x-
        assertFalse(ISBNvalido.isISBN("978-123"));           // troppo corto
    }

    /* ───────────────────────  THREAD‑SAFETY BASE  ─────────────────────── */

    @Test
    @DisplayName("Accesso concorrente: nessuna perdita di dati")
    void testAccessoConcorrente() throws InterruptedException {
        int threadN  = 8;
        int perTh    = 20;
        Thread[] t   = new Thread[threadN];

        for (int i = 0; i < threadN; i++) {
            final int id = i;
            t[i] = new Thread(() -> {
                for (int j = 0; j < perTh; j++) {
                    String isbn = String.format("978-9999999%04d", id*perTh + j);
                    libreria.aggiungiLibro(new Libro("T"+id+"-"+j,"Aut", isbn, "Var", 2000+j));
                }
            });
            t[i].start();
        }
        for (Thread tt : t) tt.join();

        assertEquals(threadN * perTh, libreria.getLibri().size());
    }
}
