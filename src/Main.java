import dbms.Relation;

public class Main {
    public static void main(String[] args) {
        String[] fs = {"nome", "cognome", "sesso", "data_nascita"};

        Relation rel = Relation.load("studente");
        if(rel == null) {
            rel = new Relation("studente", fs);
            rel.insert(new String[]{"guido", "michieletto", "M", "20/02/2007"});
            rel.insert(new String[]{"antonio", "friselle", "M", "11/11/2007"});
            rel.insert(new String[]{"antonio", "friselle", "M", "11/11/2007"});
            rel.insert(new String[]{"maria", "rossi", "F", "05/05/2007"});
            rel.save();
        }

        Relation doc = Relation.load("docente");
        if(doc == null) {
            doc = new Relation("docente", new String[]{"nome", "cognome", "sesso", "data_nascita"});
            doc.insert(new String[]{"verdi", "luca", "M", "01/01/1980"});
            doc.insert(new String[]{"neri", "anna", "F", "02/02/1985"});
            doc.insert(new String[]{"maria", "rossi", "F", "05/05/2007"});
            doc.save();
        }

        Relation classi = Relation.load("classe");
        if(classi == null) return;

        System.out.println(rel);

        Relation join = rel.join(classi, "classe = classe");
        System.out.println(join);
    }
}