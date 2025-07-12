package is.strategy;

import model.Libro;
import java.util.Comparator;
import java.util.List;

public class OrdPerAnno implements OrdStrategy {
    //Ordinamento libri per anno

    @Override
    public void ordina(List<Libro> libri) {
        libri.sort(Comparator.comparingInt(Libro::getAnnoPubblicazione));
    }
}
