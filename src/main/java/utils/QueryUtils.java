package utils;

import parser.Normalizer;
import parser.Tokenizer;

import java.util.List;

public class QueryUtils {

    public static boolean isQueryPhrase(String query) {
        return !query.contains("&") && !query.contains("!") &&
                !query.contains("|") && !query.contains("/") &&
                !query.contains("*") ;
    }

    public static List<String> getQueryTerms(String query) {
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenize(query);
        Normalizer normalizer = new Normalizer(tokens);
        return normalizer.getNormalizedTerms();
    }

}