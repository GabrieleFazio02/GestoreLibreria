package is.strategy;

import model.Libro;
import java.util.List;

public class OrdPerTitolo implements OrdStrategy {
    //Ordinamento libri per titolo

    @Override
    public void ordina(List<Libro> libri) {
        libri.sort((l1, l2) -> l1.getTitolo().compareToIgnoreCase(l2.getTitolo()));

    }
}
