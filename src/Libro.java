import java.util.Objects;

public class Libro {
    private String titolo;
    private String autore;
    private String isbn;
    private String genere;
    private int annoPubblicazione;
    private int valutazione;
    private StatoLettura statoLettura;

    public enum StatoLettura {
        LETTO, DA_LEGGERE, IN_LETTURA
    }

    public Libro() {}

    public Libro(String titolo, String autore, String isbn, String genere, int annoPubblicazione,int valutazione, StatoLettura statoLettura) {
        this.titolo = titolo;
        this.autore = autore;
        this.isbn = isbn;
        this.genere = genere;
        this.annoPubblicazione = annoPubblicazione;
        setValutazione(valutazione);
        this.statoLettura = statoLettura;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(int annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }

    public int getValutazione() {
        return valutazione;
    }

    public void setValutazione(int valutazione) {
        if(valutazione < 1 || valutazione > 5) {
            throw new IllegalArgumentException("Valutazione non valida, deve essere compresa tra 1 e 5");
        }
        this.valutazione = valutazione;
    }

    public StatoLettura getStatoLettura() {
        return statoLettura;
    }

    @Override
    public String toString() {
        return "Libro [titolo=" + titolo + ", autore=" + autore + ", isbn=" + isbn +
                ", genere=" + genere + ", annoPubblicazione=" + annoPubblicazione +
                ", valutazione=" + valutazione + ", statoLettura=" + statoLettura + "]";

    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Libro libro = (Libro) obj;
        return Objects.equals(isbn, libro.isbn);
    }

    public int hashCode() {
        return Objects.hash(isbn);
    }

    public String getLib() {
        return "Titolo: " + titolo + "\nAutore: " + autore + "\nISBN: " + isbn + "\nGenere: " + genere + "\nAnno di pubblicazione: " + annoPubblicazione + "\nValutazione: " + valutazione + "\nStato lettura: " + statoLettura;
    }


}
