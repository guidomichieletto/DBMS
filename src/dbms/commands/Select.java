package dbms.commands;

import dbms.Condition;
import dbms.Relation;

import java.util.ArrayList;
import java.util.Arrays;

public class Select extends Command {
    private static final String[] KEYWORDS = {"from", "join", "where"};

    private static class Join {
        public Relation relation;
        public String condition;

        public Join(Relation relation, String condition) {
            this.relation = relation;
            this.condition = condition;
        }
    }

    private Relation relation;
    private ArrayList<Join> joins = new ArrayList<>();
    private ArrayList<String> fields = new ArrayList<>();
    private ArrayList<String> conditions = new ArrayList<>();

    public Select(String command) throws Exception {
        String[] tokens = command.split(" ");

        // get FROM index
        int fromIndex = -1;
        for(int i = 1; i < tokens.length; i++) {
            if(tokens[i].equalsIgnoreCase("from")) fromIndex = i;
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

        // find JOIN keywords and extract joins
        for(int i = 1; i < tokens.length; i++) {
            if(tokens[i].equalsIgnoreCase("join")) {
                if(!tokens[i + 2].equalsIgnoreCase("on")) {
                    throw new Exception("select: invalid JOIN syntax");
                }

                int endJoinIndex = getNextKeywordIndex(tokens, i + 3);
                String joinCondition = "";

                for(int j = i + 3; j < endJoinIndex; j++) {
                    joinCondition += tokens[j] + " ";
                }

                joins.add(new Join(Relation.load(tokens[i + 1]), joinCondition.trim()));
            }
        }

        // find if WHERE keyword exists
        int whereIndex = -1;
        for(int i = 1; i < tokens.length; i++) {
            if(tokens[i].equalsIgnoreCase("where")) whereIndex = i;
        }
        if(whereIndex == -1) return;

        // insert conditions
        String conditionStr = "";
        for(int i = whereIndex + 1; i < tokens.length; i++) {
            conditionStr += tokens[i] + " ";
        }
        conditions.addAll(Arrays.asList(conditionStr.split(" and ")));
    }

    private int getNextKeywordIndex(String[] tokens, int startIndex) {
        for(int i = startIndex; i < tokens.length; i++) {
            for(String keyword : KEYWORDS) {
                if(tokens[i].equalsIgnoreCase(keyword)) return i;
            }
        }

        return tokens.length;
    }

    @Override
    public String execute() throws Exception {
        if(relation == null) return "no relation loaded";

        for(Join join : joins) {
            relation = relation.join(join.relation, join.condition);
        }

        if(!fields.getFirst().equals("*")) {
            relation = relation.projection(fields.toArray(new String[] {}));
        }

        for(String condition : conditions) {
            relation = relation.selection(condition);
        }

        return relation.toString();
    }
}
