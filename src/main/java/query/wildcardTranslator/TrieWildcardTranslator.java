package query.wildcardTranslator;

import dictionary.termIndexer.TermIndexer;

import java.util.List;

public class TrieWildcardTranslator implements WildcardTranslator {

    private final TermIndexer termIndexer;
    public TrieWildcardTranslator(TermIndexer termIndexer) {
        this.termIndexer = termIndexer;
    }

    @Override
    public String translate(String query) {
        String[] words = query.split(" ");
        for (String word : words) {
            if (!word.contains("*")) continue;
            String normalized = word.toLowerCase();
            List<String> result = termIndexer.getTermsFromQuery(normalized);
            StringBuilder sb = new StringBuilder(result.isEmpty() ? "" : result.get(0));
            for (int i = 1; i < result.size(); ++i)
                sb.append("*").append(result.get(i));
            query = query.replace(word, sb.toString());
        }
        return query;
    }

}