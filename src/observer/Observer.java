package observer;

import model.Libro;
import java.util.List;

public interface Observer {
    //Pattern Observer

    public void update(List<Libro> libri);
}
