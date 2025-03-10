package dictionary.termIndexer;

import java.util.List;

public class TrieTermIndexer implements TermIndexer {

    private final Trie prefixTree = new Trie();
    private final Trie suffixTree = new Trie();

    @Override
    public void addTerm(String term) {
        prefixTree.addTerm(term);
        suffixTree.addTerm(new StringBuilder(term).reverse().toString());
    }

    @Override
    public void addTerms(List<String> terms) {
        for (String term : terms)
            addTerm(term);
    }

    @Override
    public List<String> getTermsFromQuery(String query) {
        if (query.startsWith("*"))
            return suffixTree.termsStartWith(query.replace("*", ""));
        return prefixTree.termsStartWith(query.replace("*", ""));
    }

}