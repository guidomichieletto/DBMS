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
     * @param condition expressed as a string of type field [=,<>] 'value' or field [=,<>] field
     * @return a new relation with the rows that satisfy the condition
     */
    public Relation selection(String condition) {
        Relation res = new Relation("selection_" + name, field_names);

        // getting operator
        int op = -1; // 0 = "=", 1 = "<>"
        if(condition.contains("=")) op = 0;
        else if(condition.contains("<>")) op = 1;
        if(op == -1) return null;

        // getting the fields or value
        String[] expr = condition.replace(" ", "").split(op == 0 ? "=" : "<>");
        String field1 = expr[0];
        String field2 = expr[1];

        // determine value or field
        int type = -1; // 0 = value, 1 = field
        if(field2.charAt(0) == '\'') type = 0; else type = 1;

        // getting index for fields
        int field1Index = getFieldIndex(field1);
        int field2Index = -1;
        if(type == 1) field2Index = getFieldIndex(field2);


        // search field [=,<>] value
        if(type == 0) {
            for(String[] row : data) {
                if(row[field1Index].equals(field2.replace("'", ""))) res.insert(row);
            }
        }

        // search field [=,<>] field
        if(type == 1) {
            for(String[] row : data) {
                if(row[field1Index].equals(row[field2Index])) res.insert(row);
            }
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
     * Function to perform a union between this relation and another one
     * The two relations must have the same number of fields and the same field names
     * @param r the other relation
     * @return a new relation with the union, or null if the number of fields or their names are different
     */
    public Relation union(Relation r) {
        if(this.field_names.length != r.field_names.length) return null;

        for(int i = 0; i < this.field_names.length; i++) {
            if(!this.field_names[i].equals(r.field_names[i])) return null;
        }

        Relation res = new Relation("union_" + name + "_" + r.name, this.field_names);

        for(String[] row : this.data) {
            res.insert(row);
        }
        for(String[] row : r.data) {
            res.insert(row);
        }

        return res;
    }

    public Relation difference(Relation r) {
        Relation res = new Relation("difference_" + name + "_" + r.name, field_names);

        if(this.field_names.length != r.field_names.length) return null;
        for(int i = 0; i < this.field_names.length; i++) {
            if(!this.field_names[i].equals(r.field_names[i])) return null;
        }

        for(String[] row : this.data) {
            if(!r.duplicated(row)) res.insert(row);
        }

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
     * @param condition expressed as a string of type field1 [=,<>] field2 AND field3 [=,<>] field4 ...
     * @return a new relation with the join, or null if any error
     */
    public Relation join(Relation r, String condition) {
        // getting multiple conditions
        String[] conditionsStr = condition.replace(" ", "").split("AND");
        Condition[] conditions = new Condition[conditionsStr.length];
        for(int i = 0; i < conditionsStr.length; i++) {
            conditions[i] = Condition.evaluate(conditionsStr[i]);
            if(conditions[i] == null) return null;
        }

        // getting conditions indexes
        int[][] condIndexes = new int[conditions.length][2];
        for(int i = 0; i < conditions.length; i++) {
            condIndexes[i][0] = getFieldIndex(conditions[i].getField());
            condIndexes[i][1] = r.getFieldIndex(conditions[i].getValue());
            if(condIndexes[i][0] == -1 || condIndexes[i][1] == -1) return null;
        }

        Relation res = createXBase(r);

        for(String[] data1 : this.data) {
            for(String[] data2 : r.data) {
                boolean valid = true;

                for (int i = 0; i < conditions.length; i++) {
                    if(conditions[i].getOperator() == Condition.Operator.EQUAL) {
                        if(!data1[condIndexes[i][0]].equals(data2[condIndexes[i][1]])) {
                            valid = false;
                            break;
                        }
                    } else if(conditions[i].getOperator() == Condition.Operator.NOT_EQUAL) {
                        if(data1[condIndexes[i][0]].equals(data2[condIndexes[i][1]])) {
                            valid = false;
                            break;
                        }
                    }
                }

                if(valid) insertXData(res, data1, data2);
            }
        }

        return res;
    }

    /**
     * Function to perform a natural join between this relation and another one
     */
    public Relation naturalJoin(Relation r) {
        // getting common fields
        ArrayList<String> commonFields = new ArrayList<>();
        for(String f1 : this.field_names) {
            for(String f2 : r.field_names) {
                if(f1.equals(f2)) {
                    commonFields.add(f1);
                    break;
                }
            }
        }

        // creating conditions
        StringBuilder condition = new StringBuilder();
        for(int i = 0; i < commonFields.size(); i++) {
            condition.append(commonFields.get(i)).append("=").append(commonFields.get(i));
            if(i != commonFields.size() - 1) condition.append(" AND ");
        }

        return join(r, condition.toString());
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

    /**
     * Function to get the index of a field by its name
     * @param name the name of the field
     * @return the index of the field, or -1 if not found
     */
    private int getFieldIndex(String name) {
        int fieldIndex = -1;
        for (int i = 0; i < field_names.length; i++) {
            if(field_names[i].equals(name)) {
                fieldIndex = i;
                break;
            }
        }

        return fieldIndex;
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