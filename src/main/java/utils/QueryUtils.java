package utils;

public class QueryUtils {

    public static boolean isQueryPhrase(String query) {
        return !query.contains("&") && !query.contains("!") &&
                !query.contains("|") && !query.contains("/") &&
                !query.contains("*") ;
    }

}