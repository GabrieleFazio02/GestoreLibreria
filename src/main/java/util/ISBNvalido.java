package util;

public final class ISBNvalido {

    private ISBNvalido(){}

    public static boolean isISBN(String isbn){
        if(isbn == null) return false;
        String isbnval = isbn.trim();

        //ISBN-10: devono essere inserite 10 cifre
        if(isbnval.matches("[0-9]{10}")){
            return true;
        }

        //ISBN-13: devono essere inserite le prime tre cifre (978 o 979) seguite da - e 10 cifre
        return isbnval.matches("97[89]-[0-9]{10}");
    }
}
