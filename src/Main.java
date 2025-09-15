import dbms.Relation;

public class Main {
    public static void main(String[] args) {
        String[] fs = {"nome", "cognome", "sesso", "data_nascita"};

        Relation rel = Relation.load("studente");
        if(rel == null) {
            rel = new Relation("studente", fs);
            rel.insert(new String[]{"guido", "michieletto", "M", "20/02/2007"});
            rel.insert(new String[]{"antonio", "friselle", "M", "11/11/2007"});
            rel.save();
        }

        System.out.println(rel);

        Relation search = rel.selection("nome = antonio");
        System.out.println(search);

        Relation projection = rel.projection(new String[]{"nome", "cognome"});
        System.out.println(projection);

        search.save();
    }
}