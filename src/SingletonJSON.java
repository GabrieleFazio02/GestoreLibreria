import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SingletonJSON {
    private static SingletonJSON instance;
    private Gson gson;
    private final String PATH = "libreria.json";

    //Usiamo il costruttore privato, per utilizzare il pattern Singleton,
    //usiamo questo pattern per assicurarci che ci sia sempre un'unica istanza del file JSON
    private SingletonJSON() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    //Creiamo e otteniamo una nuova istanza Singleton se non è esiste già una
    public static SingletonJSON getInstance() {
        if(instance == null) {
            instance = new SingletonJSON();
        }
        return instance;
    }


    //Metodo per salavare i libri in libreria

    public void salvaInLibreria(List<Libro> libri) {
        try {
            FileWriter fileWriter = new FileWriter(PATH);
            gson.toJson(libri, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Errore durante il salvataggio del libro" + e.getMessage());
        }
    }

    //Metodo per leggere il contenuto della libreria
    public List<Libro> leggiDaLibreria() {
        File file = new File(PATH);

        if(!file.exists()) {
            return new ArrayList<>();
        }

        //Legge prima il libro dal file JSON e lo inserisce in un array, che convertiamo dopo in un ArrayList
        try (FileReader fileReader = new FileReader(file)){
            Libro[] lib = gson.fromJson(fileReader, Libro[].class);
            List<Libro> libri = Arrays.asList(lib);
            return Objects.requireNonNullElseGet(libri, ArrayList::new);// Se libri dovesse essere null restituisce un'ArrayList vuoto

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Errore durante la lettura del file" + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la chiusura del file: " + e.getMessage());
        }

    }


}
