package is.strategy;

import model.Libro;
import java.util.List;

public interface RicStrategy {
    //Pattern Strategy per ricerca

    List<Libro> cerca(List<Libro> libri, String criterio);

}
