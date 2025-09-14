package dbms;

import java.io.*;
import java.util.ArrayList;

public class Relation {
    private String name;
    private String[] field_names;
    private ArrayList<String[]> data;

    public Relation(String name, String[] fs) {
        this.name = name;
        field_names = new String[fs.length];

        System.arraycopy(fs, 0, field_names, 0, fs.length);

        data = new ArrayList<>();
    }

    public void insert(String[] row) {
        String[] copy = new String[row.length];
        System.arraycopy(row, 0, copy, 0, row.length);
        data.add(copy);
    }

    public void save() {
        File file = new File("data/" + name + ".csv");

        try {
            FileWriter fw = new FileWriter(file);

            // intestazione
            for(int i = 0; i < field_names.length; i++) {
                fw.write(field_names[i]);
                if(i != field_names.length - 1) fw.write(",");
            }
            fw.write("\n");

            // dati
            for(String[] row : data) {
                for(int i = 0; i < row.length; i++) {
                    fw.write(row[i]);
                    if(i != row.length - 1) fw.write(",");
                }
                fw.write("\n");
            }

            fw.flush();
            fw.close();
        } catch (Exception ex) {

        }
    }

    public void load() {
        File file = new File("data/" + name + ".csv");
        if(!file.exists()) return;

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            // skip first line
            br.readLine();

            String line = br.readLine();
            while(line != null) {
                // TODO
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public String toString() {
        String ret = "";

        // intestazione
        int full_len = field_names.length * 21 - 1;
        ret += "+" + String.format("%-" + full_len + "s", "").replace(" ", "-") + "+\n";
        ret += "|" + String.format("%-" + full_len + "s", name) + "|\n";

        ret += "+";
        for(int i = 0; i < field_names.length; i++) {
            ret += String.format("%-20s", "").replace(" ", "-") + "+";
        }
        ret += "\n";

        // intestazione colonne
        ret += "|";
        for(int i = 0; i < field_names.length; i++) {
            ret += String.format("%-20s", field_names[i]) + "|";
        }
        ret += "\n";

        // dati
        for(String[] row : data) {
            ret += "|";
            for(String field_val : row) {
                ret += String.format("%-20s", field_val) + "|";
            }
            ret += "\n";
        }

        // piè
        ret += "+";
        for(int i = 0; i < field_names.length; i++) {
            ret += String.format("%-20s", "").replace(" ", "-") + "+";
        }
        ret += "\n";

        return ret;
    }
}