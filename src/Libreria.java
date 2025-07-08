import java.util.List;
import java.io.*;


public class Libreria {
    private List<Libro> libri;
    private final SingletonJSON singletonJSON;


    public Libreria() {
        this.singletonJSON = SingletonJSON.getInstance();
        this.libri = singletonJSON.leggiDaLibreria();
    }

    public void aggiungiLibro(Libro libro) {
        if(!libri.contains(libro)) {
            this.libri.add(libro);
            salvaLib();
        }
    }

    public void rimuoviLibro(Libro libro) {
        this.libri.remove(libro);
        salvaLib();
    }

    public void modificaLibro(Libro libroV, Libro libroN) {
        int i = libri.indexOf(libroV);
        if (i != -1) {
            libri.set(i, libroN);
            salvaLib();
        }
    }


    public void salvaLib(){
        singletonJSON.salvaInLibreria(libri);
    }

    public void caricaLib(){
        this.libri = singletonJSON.leggiDaLibreria();
    }




}
