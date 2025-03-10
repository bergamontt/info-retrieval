package query.wildcardTranslator;

import dictionary.termIndexer.TermIndexer;

import java.util.List;

public class KgramWildcardTranslator implements WildcardTranslator {

    private final TermIndexer termIndexer;
    public KgramWildcardTranslator(TermIndexer termIndexer) {
        this.termIndexer = termIndexer;
    }

    @Override
    public String translate(String query) {
        String[] tokens = query.split(" ");
        for (String token : tokens) {
            String translatedToken = token;
            if (!translatedToken.contains("*"))
                continue;
            if (translatedToken.charAt(0) != '*')
                translatedToken = "$$" + translatedToken.toLowerCase();
            if (translatedToken.charAt(translatedToken.length() - 1) != '*')
                translatedToken += "$$";
            List<String> terms = termIndexer.getTermsFromQuery(translatedToken);
            StringBuilder sb = new StringBuilder(terms.get(0));
            for (int i = 1; i < terms.size(); ++i)
                sb.append("*").append(terms.get(i));
            query = query.replace(token, sb.toString());
        }
        return query;
    }
}