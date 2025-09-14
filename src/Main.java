import dbms.Relation;

public class Main {
    public static void main(String[] args) {
        String[] fs = {"nome", "cognome", "sesso", "data_nascita"};

        Relation rel = new Relation("studente", fs);
        rel.insert(new String[]{"guido", "michieletto", "M", "20/02/2007"});
        rel.insert(new String[]{"antonio", "friselle", "M", "11/11/2007"});
        rel.save();

        System.out.println(rel);
    }
}