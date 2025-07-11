package is.strategy;

import model.Libro;
import java.util.List;

public interface OrdStrategy {
    //Pattern Strategy per ordinamento

    public void ordina(List<Libro> libri);
}
