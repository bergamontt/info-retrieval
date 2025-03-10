package pattern.factory;

import dictionary.termIndexer.*;
import query.wildcardTranslator.*;

public class WildcardTranslatorFactory {

    public WildcardTranslator create(TermIndexer termIndexer) {
        if (termIndexer instanceof TrieTermIndexer)
            return new TrieWildcardTranslator(termIndexer);
        if (termIndexer instanceof PermutermIndexer)
            return new PermutermWildcardTranslator(termIndexer);
        if (termIndexer instanceof KgramIndexer)
            return new KgramWildcardTranslator(termIndexer);
        else return new DefaultWildCardTranslator();
    }

}