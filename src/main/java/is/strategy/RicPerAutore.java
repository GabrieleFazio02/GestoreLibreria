package is.strategy;

import model.Libro;

import java.util.List;

public class RicPerAutore implements RicStrategy{
    //Ricerca per Autore

    @Override
    public List<Libro> cerca(List<Libro> libri, String autore) {
        return libri.stream().filter(lib -> lib.getAutore().toLowerCase().contains(autore.toLowerCase())).toList();
    }
}
