package dictionary.termIndexer;

import java.util.List;

public interface TermIndexer {

    void addTerm(String term);
    void addTerms(List<String> terms);
    List<String> getTermsFromQuery(String query);

}