package is.strategy;

import model.Libro;

import java.util.List;
import java.util.stream.Collectors;

public class RicPerTitolo implements RicStrategy {
    //ricerca per titolo

    @Override
    public List<Libro> cerca(List<Libro> libri, String titolo) {
        return libri.stream().filter(lib -> lib.getTitolo().toLowerCase().contains(titolo.toLowerCase())).collect(Collectors.toList());
    }

}
