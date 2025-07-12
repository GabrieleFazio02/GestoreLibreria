package model;

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

    public Libro(String titolo, String autore, String isbn, String genere, int annoPubblicazione) {
        this.titolo = titolo;
        this.autore = autore;
        this.isbn = isbn;
        this.genere = genere;
        this.annoPubblicazione = annoPubblicazione;
        this.valutazione = 0;
        this.statoLettura = StatoLettura.DA_LEGGERE;
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
        //La valutazione può essere inserita solo una volta che lo stato del libro è "LETTO"
        if(this.statoLettura != StatoLettura.LETTO) {
            throw new IllegalStateException("La valutazione può essere inserita solo per libri già letti!");
        }
        if(valutazione < 1 || valutazione > 5) {
            throw new IllegalArgumentException("Valutazione non valida, deve essere compresa tra 1 e 5");
        }
        this.valutazione = valutazione;
    }

    public void setStatoLettura(StatoLettura nuovoStato) {
        //Se cambiamo lo stato da LETTO a un altro stato, resettiamo la valutazione
        if (nuovoStato != StatoLettura.LETTO && this.valutazione != 0) {
            this.valutazione = 0;
        }
        this.statoLettura = nuovoStato;
    }


    public StatoLettura getStatoLettura() {
        return statoLettura;
    }

    @Override
    public String toString() {
        //oltre alle informazioni principali stampa la valutazione solo se presente, di conseguenza solo se il libro è stato letto
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\"%s\" di %s - %s (%d) - ISBN: %s",
                titolo, autore, genere, annoPubblicazione, isbn));

        if (statoLettura == StatoLettura.LETTO && valutazione > 0) {
            sb.append(String.format(" - %d/5 stelle", valutazione));
        }

        sb.append(String.format(" - %s", statoLettura));

        return sb.toString();
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
