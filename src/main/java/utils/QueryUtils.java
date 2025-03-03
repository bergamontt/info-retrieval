package utils;

public class QueryUtils {

    public static boolean isPhraseQuery(String query) {
        return (!query.contains("|") && !query.contains("!") && !query.contains("&"));
    }

}