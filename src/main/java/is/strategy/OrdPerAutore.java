package is.strategy;

import model.Libro;
import java.util.List;

public class OrdPerAutore implements OrdStrategy {
    //Ordinamento libri per Autore

    @Override
    public void ordina(List<Libro> libri) {
        libri.sort((l1, l2) -> l1.getAutore().compareToIgnoreCase(l2.getAutore()));
    }
}
