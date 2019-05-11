package messenger.utils;

public class StringUtils {

    private StringUtils() {

    }

    public static String removeBeginAndEndQuotes(String str) {
        return str.replaceAll("^\"|\"$", "");
    }
}
