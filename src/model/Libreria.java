package model;

import is.strategy.OrdPerTitolo;
import is.strategy.RicPerTitolo;
import persistence.SingletonJSON;
import is.strategy.RicStrategy;
import is.strategy.OrdStrategy;
import observer.guiUpdate;

import java.util.ArrayList;
import java.util.List;


public class Libreria {
    private List<Libro> libri;
    private final SingletonJSON singletonJSON;
    private OrdStrategy ordStrategy;
    private RicStrategy ricStrategy;
    private List<guiUpdate> observers;

    public Libreria() {
        this.singletonJSON = SingletonJSON.getInstance();
        this.libri = new ArrayList<>(singletonJSON.leggiDaLibreria());
        this.ordStrategy = new OrdPerTitolo();
        this.ricStrategy = new RicPerTitolo();
        this.observers = new ArrayList<>();
    }

    public void setOrdStrategy(OrdStrategy ordSt) {
        this.ordStrategy = ordSt;
        ordinaLib();
    }

    public void ordinaLib() {
        ordStrategy.ordina(libri);
        notifyObservers();
    }

    public void setRicStrategy(RicStrategy ricSt) {
        this.ricStrategy = ricSt;
    }

    public List<Libro> cercaLib(String criterio) {
        return ricStrategy.cerca(libri, criterio);
    }

    public void addObserver(guiUpdate observer) {
        this.observers.add(observer);
    }

    public void removeObserver(guiUpdate observer) {
        this.observers.remove(observer);
    }

    public void notifyObservers() {
        for (guiUpdate observer : observers) {
            observer.update(libri);
        }
    }

    public void aggiungiLibro(Libro libro) {
        if(!libri.contains(libro)) {
            this.libri.add(libro);
            salvaLib();
            ordinaLib();
            notifyObservers();
        }
    }

    public void rimuoviLibro(Libro libro) {
        this.libri.remove(libro);
        salvaLib();
        notifyObservers();
    }

    public void modificaLibro(Libro libroV, Libro libroN) {
        int i = libri.indexOf(libroV);
        if (i != -1) {
            libri.set(i, libroN);
            salvaLib();
            ordinaLib();
            notifyObservers();
        }
    }


    public void salvaLib(){
        singletonJSON.salvaInLibreria(libri);
    }

    public void caricaLib(){
        this.libri = new ArrayList<>(singletonJSON.leggiDaLibreria());
    }

    public void stampaLibri() {
        System.out.println("Ecco tutti i libri presenti nella libreria:");
        if (libri.isEmpty()) {
            System.out.println("La libreria è vuota");
        } else {
            for (Libro libro : libri) {
                System.out.println(libro.toString());
            }
        }
        System.out.println("================================");
    }

    public List<Libro> getLibri() {
        return new ArrayList<>(libri);
    }






}
