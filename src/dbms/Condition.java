package dbms;

public class Condition {
    public enum Operator {
        EQUAL, NOT_EQUAL
    }

    private String field;
    private Operator operator;
    private String value;
    private boolean valueIsField = false;

    private Condition() {}

    public String getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    public boolean valueIsField() {
        return valueIsField;
    }

    public static Condition evaluate(String condition) throws Exception {
        Condition conditionObj = new Condition();

        if(condition.contains("=")) conditionObj.operator = Operator.EQUAL;
        else if(condition.contains("<>")) conditionObj.operator = Operator.NOT_EQUAL;
        else return null;

        String operatorStr = (conditionObj.operator == Operator.EQUAL ? "=" : "<>");

        String[] expr = condition.split(operatorStr);
        conditionObj.field = expr[0];

        if(expr[1].contains("'")) {
            conditionObj.value = Utils.getValueFromString(expr[1]);
            if(conditionObj.value == null) throw new Exception("condition: Invalid string value " + expr[1]);
            conditionObj.valueIsField = true;
        } else {
            conditionObj.value = expr[1];
        }

        return conditionObj;
    }
}
