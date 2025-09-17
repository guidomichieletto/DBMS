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

        System.out.println(rel);

        Relation search = rel.selection("nome = antonio");
        System.out.println(search);

        Relation projection = rel.projection(new String[]{"nome", "cognome"});
        System.out.println(projection);

        Relation redenomination = rel.rename(new String[]{"nome1", "cognome1", "sesso1", "dn"});
        System.out.println(redenomination);

        Relation cartesian = rel.xproduct(projection);
        System.out.println(cartesian);

        Relation join = rel.join(projection.rename(new String[]{"nome1", "congome1"}), "nome = nome1");
        System.out.println(join);

        search.save();
    }
}