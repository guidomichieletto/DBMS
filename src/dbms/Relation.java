package dbms;

import java.io.*;
import java.util.ArrayList;

public class Relation {
    private final String name;
    private final String[] field_names;
    private final ArrayList<String[]> data;

    /**
     * Constructor for a new relation
     * @param name the name of the relation
     * @param fs the names of the fields
     */
    public Relation(String name, String[] fs) {
        this.name = name;
        field_names = new String[fs.length];

        System.arraycopy(fs, 0, field_names, 0, fs.length);

        data = new ArrayList<>();
    }

    /**
     * Function to insert a new row in the relation
     * @param row the row to insert (must have the same number of fields as the relation)
     */
    public void insert(String[] row) {
        String[] copy = new String[row.length];
        System.arraycopy(row, 0, copy, 0, row.length);
        data.add(copy);
    }

    /**
     * Function to save the relation to a CSV file
     */
    public void save() {
        File file = new File(name + ".csv");

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
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Function to load a relation from a CSV file
     * @param name the name of the relation (without .csv)
     * @return the loaded relation, or null if any error
     */
    public static Relation load(String name) {
        File file = new File(name + ".csv");
        if(!file.exists()) return null;

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            // load fields from first line
            String[] header = br.readLine().split(",");
            Relation rel = new Relation(name, header);

            String line;
            String[] dataRow;
            while((line = br.readLine()) != null) {
                dataRow = line.split(",");
                rel.data.add(dataRow);
            }

            return rel;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Function to perform a selection on the relation
     * @param condition expressed as a string of type field = value
     * @return a new relation with the rows that satisfy the condition
     */
    public Relation selection(String condition) {
        Relation res = new Relation("selection_" + name, field_names);

        String[] expr = condition.replace(" ", "").split("=");
        String fieldName = expr[0];
        String value = expr[1];

        int fieldIndex = -1;
        for (int i = 0; i < field_names.length; i++) {
            if(field_names[i].equals(fieldName)) {
                fieldIndex = i;
                break;
            }
        }
        if(fieldIndex == -1) return null;

        for(String[] row : data) {
            if(row[fieldIndex].equals(value)) res.insert(row);
        }

        return res;
    }

    public Relation projection(String[] fields) {
        Relation res = new Relation("projection_" + name, fields);

        // getting the indexes
        int[] indexes = new int[fields.length];
        for(int i = 0; i < fields.length; i++) {
            for(int j = 0; j < field_names.length; j++) {
                if(fields[i].equals(field_names[j])) indexes[i] = j;
            }
        }

        for(String[] row : data) {
            String[] newRow = new String[fields.length];
            for(int i = 0; i < fields.length; i++) {
                newRow[i] = row[indexes[i]];
            }
            res.insert(newRow);
        }

        return res;
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