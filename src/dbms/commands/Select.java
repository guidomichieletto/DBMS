package dbms.commands;

import dbms.Condition;
import dbms.Relation;

import java.util.ArrayList;
import java.util.Arrays;

public class Select extends Command {
    private Relation relation;
    private ArrayList<String> fields = new ArrayList<>();
    private ArrayList<String> conditions = new ArrayList<>();

    public Select(String command) throws Exception {
        String[] tokens = command.toLowerCase().split(" ");

        // get FROM index
        int fromIndex = -1;
        for(int i = 1; i < tokens.length; i++) {
            if(tokens[i].equals("from")) fromIndex = i;
        }
        if(fromIndex == -1) throw new Exception("FROM keyword not found in select statement");

        // load the relation
        if(tokens.length <= fromIndex + 1) throw new Exception("relation name not found in select statement");
        relation = Relation.load(tokens[fromIndex + 1]);
        if(relation == null) throw new Exception("SELECT: relation not found");

        // get fields to extract
        String fieldsStr = "";
        for(int i = 1; i < fromIndex; i++) {
            fieldsStr += tokens[i];
        }
        fields.addAll(Arrays.asList(fieldsStr.split(",")));
        if(fields.isEmpty()) throw new Exception("no fields to extract in select statement");

        // find if WHERE keyword exists
        int whereIndex = -1;
        for(int i = 1; i < tokens.length; i++) {
            if(tokens[i].equals("where")) whereIndex = i;
        }
        if(whereIndex == -1) return;

        // insert conditions
        String conditionStr = "";
        for(int i = whereIndex + 1; i < tokens.length; i++) {
            conditionStr += tokens[i] + " ";
        }
        conditions.addAll(Arrays.asList(conditionStr.split(" and ")));
    }

    @Override
    public String execute() {
        if(relation == null) return "no relation loaded";

        if(!fields.getFirst().equals("*")) {
            relation = relation.projection(fields.toArray(new String[] {}));
        }

        for(String condition : conditions) {
            relation = relation.selection(condition);
        }

        return relation.toString();
    }
}
