package is.strategy;

import model.Libro;

import java.util.List;
import java.util.stream.Collectors;

public class RicPerISBN implements RicStrategy {
    //ricerca per ISBN

    @Override
    public List<Libro> cerca(List<Libro> libri, String isbn){
        return libri.stream().filter(lib -> lib.getIsbn().equals(isbn)).collect(Collectors.toList());
    }
}
