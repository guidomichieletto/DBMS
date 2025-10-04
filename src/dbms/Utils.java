package dbms;

public class Utils {
    public static String getValueFromString(String original) {
        int openApostropheIndex = original.indexOf("'");
        int closeApostropheIndex = original.lastIndexOf("'");
        if(openApostropheIndex == -1 || closeApostropheIndex == -1 || openApostropheIndex == closeApostropheIndex) {
            return null;
        }

        return original.substring(openApostropheIndex + 1, closeApostropheIndex);
    }
}
