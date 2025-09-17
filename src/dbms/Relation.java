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
     * Function to insert a new row in the relation (if not already present)
     * @param row the row to insert (must have the same number of fields as the relation)
     */
    public void insert(String[] row) {
        if(row.length != field_names.length) return;
        if(duplicated(row)) return;

        String[] copy = new String[row.length];
        System.arraycopy(row, 0, copy, 0, row.length);
        data.add(copy);
    }

    /**
     * Function to check if a row is already present in the relation
     * @param newData the row to check
     * @return true if the row is already present, false otherwise
     */
    private boolean duplicated(String[] newData) {
        for(String[] row : data) {
            boolean equal = true;
            for(int i = 0; i < row.length; i++) {
                if(!row[i].equals(newData[i])) {
                    equal = false;
                    break;
                }
            }
            if(equal) return true;
        }
        return false;
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

    /**
     * Function to perform a projection on the relation
     * @param fields the fields to keep
     * @return a new relation with only the specified fields
     */
    public Relation projection(String[] fields) {
        Relation res = new Relation("projection_" + name, fields);

        // getting the indexes
        int[] indexes = new int[fields.length];
        for(int i = 0; i < fields.length; i++) {
            for(int j = 0; j < field_names.length; j++) {
                if(fields[i].equals(field_names[j])) indexes[i] = j;
            }
        }

        String[] newRow = new String[fields.length];
        for(String[] row : data) {
            for(int i = 0; i < fields.length; i++) {
                newRow[i] = row[indexes[i]];
            }
            res.insert(newRow);
        }

        return res;
    }

    /**
     * Function to rename the fields of the relation
     * @param fields the new field names (must have the same number of fields as the relation)
     * @return a new relation with the renamed fields, or null if the number of fields is different
     */
    public Relation rename(String[] fields) {
        if(fields.length != field_names.length) return null;
        Relation res = new Relation("rename_" + name, fields);

        for(int i = 0; i < field_names.length; i++) {
            res.field_names[i] = fields[i];
        }

        res.data.addAll(data);

        return res;
    }

    /**
     * Function to perform a cartesian product between this relation and another one
     * @param r the other relation
     * @return a new relation with the cartesian product
     */
    public Relation xproduct(Relation r) {
        int fieldsNum = this.field_names.length + r.field_names.length;
        Relation res = createXBase(r);

        for(String[] data1 : this.data) {
            for(String[] data2 : r.data) {
                Relation.insertXData(res, data1, data2);
            }
        }

        return res;
    }

    /**
     * Function to perform a join between this relation and another one
     * @param r the other relation
     * @param condition expressed as a string of type field1 [=,<>] field2
     * @return a new relation with the join, or null if any error
     */
    public Relation join(Relation r, String condition) {
        // read the condition field1 [=,<>] field2
        int op = -1; // 0 = "=", 1 = "<>"
        if(condition.contains("=")) op = 0;
        else if(condition.contains("<>")) op = 1;
        if(op == -1) return null;

        // getting the fields
        String[] expr = condition.replace(" ", "").split(op == 0 ? "=" : "<>");
        String field1 = expr[0];
        String field2 = expr[1];
        if(field1.equals(field2)) return null;

        // getting the indexes
        int index1 = -1, index2 = -1;
        for(int i = 0; i < this.field_names.length; i++) {
            if(this.field_names[i].equals(field1)) {
                index1 = i;
                break;
            }
        }
        for(int i = 0; i < r.field_names.length; i++) {
            if(r.field_names[i].equals(field2)) {
                index2 = i;
                break;
            }
        }

        Relation res = createXBase(r);

        for(String[] data1 : this.data) {
            for(String[] data2 : r.data) {
                if((op == 0 && data1[index1].equals(data2[index2])) ||
                   (op == 1 && !data1[index1].equals(data2[index2]))) {
                    Relation.insertXData(res, data1, data2);
                }
            }
        }

        return res;
    }

    /**
     * Function to create the base of a cartesian product or join relation
     * @param r the other relation
     * @return the new relation with the combined fields
     */
    private Relation createXBase(Relation r) {
        int fieldsNum = this.field_names.length + r.field_names.length;
        String[] newFields = new String[fieldsNum];

        System.arraycopy(this.field_names, 0, newFields, 0, this.field_names.length);
        System.arraycopy(r.field_names, 0, newFields, this.field_names.length, r.field_names.length);

        return new Relation("xprod_" + name + "_" + r.name, newFields);
    }

    private static void insertXData(Relation res, String[] data1, String[] data2) {
        String[] newRow = new String[res.field_names.length];
        System.arraycopy(data1, 0, newRow, 0, data1.length);
        System.arraycopy(data2, 0, newRow, data1.length, data2.length);
        res.insert(newRow);
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