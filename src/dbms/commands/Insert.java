package dbms.commands;

import dbms.Relation;

import java.util.ArrayList;
import java.util.Arrays;

public class Insert extends Command {
    private Relation relation;
    private ArrayList<String> values = new ArrayList<>();

    public Insert(String command) throws Exception {
        String[] tokens = command.toLowerCase().split(" ");

        // get the relation
        relation = Relation.load(tokens[2]);
        if(relation == null) throw new Exception("INSERT: relation not found");

        // check if VALUES keyword exists
        if(!tokens[3].equals("values")) throw new Exception("VALUES keyword not found in insert statement");

        // extract values
        int openParethesisIndex = command.indexOf("(");
        int closeParenthesisIndex = command.indexOf(")");
        String valStr = command.substring(openParethesisIndex + 1, closeParenthesisIndex).replace(" ", "").replace("'", "");
        values.addAll(Arrays.asList(valStr.split(",")));
    }

    @Override
    public String execute() {
        String[] values = this.values.toArray(new String[]{});

        try {
            relation.insert(values);
            relation.save();
        } catch (Exception e) {
            return e.getMessage();
        }

        return "INSERT OK";
    }
}
