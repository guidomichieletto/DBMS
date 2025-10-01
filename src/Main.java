import dbms.Relation;

public class Main {
    public static void main(String[] args) {

        Relation abita = Relation.load("abita");
        Relation dipende_da = Relation.load("dipende_da");
        Relation ha_sede_in = Relation.load("ha_sede_in");
        Relation lavora = Relation.load("lavora");

        if(abita == null || dipende_da == null || ha_sede_in == null || lavora == null) {
            System.out.println("Errore nel caricamento delle relazioni.");
            return;
        }

        Relation persona_lavora_sede = abita.naturalJoin(lavora).naturalJoin(ha_sede_in.rename(new String[] {"Azienda", "citta_azienda"}));
        System.out.println(persona_lavora_sede);

        Relation persone_stessa_citta = persona_lavora_sede.selection("Citta = citta_azienda");

        Relation bad = persone_stessa_citta.projection(new String[] {"Persona"});

        Relation result = lavora.projection(new String[] {"Persona"}).difference(bad);

        System.out.println(result);
    }
}