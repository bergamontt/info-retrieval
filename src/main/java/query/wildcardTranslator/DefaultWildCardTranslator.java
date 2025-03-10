package query.wildcardTranslator;

import dictionary.termIndexer.TermIndexer;

public class DefaultWildCardTranslator implements WildcardTranslator {

    @Override
    public String translate(String query) {
        return query;
    }

}