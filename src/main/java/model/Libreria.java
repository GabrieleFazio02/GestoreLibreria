package model;

import is.strategy.OrdPerTitolo;
import is.strategy.RicPerTitolo;
import observer.Observer;
import persistence.SingletonJSON;
import is.strategy.RicStrategy;
import is.strategy.OrdStrategy;
import observer.Subject;

import java.util.ArrayList;
import java.util.List;


public class Libreria implements Subject {
    private List<Libro> libri;
    private final SingletonJSON singletonJSON;
    private OrdStrategy ordStrategy;
    private RicStrategy ricStrategy;
    private final List<Observer> observers;


    public Libreria() {
        this.singletonJSON = SingletonJSON.getInstance();
        this.libri = new ArrayList<>(singletonJSON.leggiDaLibreria());
        this.ordStrategy = new OrdPerTitolo();
        this.ricStrategy = new RicPerTitolo();
        this.observers = new ArrayList<>();

    }

    public void attach(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            observer.update(libri);
        }
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }


    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(libri);
        }
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

    //cambiato il metodo aggiungi, per gestire l'ISBN dublicato, con conseguente cambiamento anche in DialogManager
    public boolean aggiungiLibro(Libro libro) {
        if(libri.contains(libro)) {
            return false;
        }
        this.libri.add(libro);
        salvaLib();
        ordinaLib();
        return true;
    }

    public void rimuoviLibro(Libro libro) {
        this.libri.remove(libro);
        salvaLib();
        notifyObservers();
    }

    //cambiato il metodo modificaLibro, per gestire l'ISBN dublicato, con conseguente cambiamento anche in DialogManager
    public boolean modificaLibro(Libro libroV, Libro libroN) {
        int i = libri.indexOf(libroV);

        boolean isbnGiaPresente = libri.stream().anyMatch(l -> !l.equals(libroV) && l.getIsbn().equalsIgnoreCase(libroN.getIsbn()));
        if (i != -1) {
            if (!isbnGiaPresente) {
                libri.set(i, libroN);
                salvaLib();
                ordinaLib();
                return true;
                //notifyObservers(); non serve più perchè il metodo ordinaLib() chiama già notifyObservers()
            }
        }
        return false;
    }

    public boolean isbnGiaPresente(String isbn){
        return libri.stream().anyMatch(l -> l.getIsbn().equalsIgnoreCase(isbn));

    }


    public void salvaLib(){
        singletonJSON.salvaInLibreria(libri);
    }

    public void caricaLib(){
        this.libri = new ArrayList<>(singletonJSON.leggiDaLibreria());
        notifyObservers();
    }


    public List<Libro> getLibri() {
        return new ArrayList<>(libri);
    }






}
