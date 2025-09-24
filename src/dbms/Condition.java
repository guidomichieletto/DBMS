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

    public boolean isValueIsField() {
        return valueIsField;
    }

    public static Condition evaluate(String condition) {
        Condition conditionObj = new Condition();

        if(condition.contains("=")) conditionObj.operator = Operator.EQUAL;
        else if(condition.contains("<>")) conditionObj.operator = Operator.NOT_EQUAL;
        else return null;

        String operatorStr = (conditionObj.operator == Operator.EQUAL ? "=" : "<>");

        String[] expr = condition.replace(" ", "").split(operatorStr);
        conditionObj.field = expr[0];
        conditionObj.value = expr[1];

        if(expr[1].charAt(0) != '\'') conditionObj.valueIsField = true;

        return conditionObj;
    }
}
