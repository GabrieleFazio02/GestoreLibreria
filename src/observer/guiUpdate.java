package observer;

import model.Libro;

import java.util.List;

public class guiUpdate implements Observer{

    @Override
    public void update(List<Libro> libri){
        for (Libro libro : libri) {
            System.out.println(libro.getLib());
        }
    }
}
