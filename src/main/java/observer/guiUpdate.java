package observer;

import model.Libro;

import java.util.List;

public class guiUpdate implements Observer{




    @Override
    public void update(List<Libro> libri){
        javafx.application.Platform.runLater(()->{});


    }
}
