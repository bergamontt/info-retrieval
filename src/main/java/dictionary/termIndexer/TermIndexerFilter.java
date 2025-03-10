package dictionary.termIndexer;

import java.util.ArrayList;
import java.util.List;

public class TermIndexerFilter {

    public static List<String> filter(List<String> terms, String query) {
        String cleanedQuery = query.replace("$", "");
        String[] tokens = cleanedQuery.split("\\*");
        if (tokens.length < 2) return terms;
        List<String> result = new ArrayList<>();
        for (String term : terms) {
            boolean valid = true;
            for (String token : tokens)
                if (!term.contains(token)) {
                    valid = false;
                    break;
                }
            if (valid) result.add(term);
        } return result;
    }

}