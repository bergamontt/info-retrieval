package dictionary.termIndexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrieTermIndexer implements TermIndexer {

    private final Trie prefixTree = new Trie();
    private final Trie suffixTree = new Trie();

    @Override
    public void addTerm(String term) {
        String lowerTerm = term.toLowerCase();
        prefixTree.addTerm(lowerTerm);
        suffixTree.addTerm(reverse(lowerTerm));
    }

    @Override
    public void addTerms(List<String> terms) {
        for (String term : terms)
            addTerm(term);
    }

    @Override
    public List<String> getTermsFromQuery(String query) {
        String cleanQuery = query.toLowerCase().replace("*", "");
        if (query.startsWith("*"))
            return reverse(suffixTree.termsStartWith(reverse(cleanQuery)));
        if (query.endsWith("*"))
            return prefixTree.termsStartWith(query.replace("*", ""));
        cleanQuery = query;
        if (isComplex(query)) {
            String[] parts = query.split("\\*");
            cleanQuery = parts[0] + "*" + parts[parts.length - 1];
        }
        String[] tokens = cleanQuery.split("\\*");
        List<String> prefixes = prefixTree.termsStartWith(tokens[0]);
        List<String> suffixes = reverse(suffixTree.termsStartWith(tokens[1]));
        List<String> intersected = intersect(prefixes, suffixes);
        return filterTerms(intersected, query);
    }

    private List<String> filterTerms(List<String> terms, String query) {
        if (!isComplex(query)) return terms;
        return TermIndexerFilter.filter(terms.stream().toList(), query);
    }

    private List<String> intersect(List<String> prefixes, List<String> suffixes) {
        Set<String> result = new HashSet<>();
        for (String prefix : prefixes)
            if (suffixes.contains(prefix)) result.add(prefix);
        return result.stream().toList();
    }

    private List<String> reverse(List<String> terms) {
        List<String> result = new ArrayList<>();
        for (String term : terms)
            result.add(reverse(term));
        return result;
    }

    private String reverse(String term) {
        return new StringBuilder(term).reverse().toString();
    }

    private boolean isComplex(String query) {
        int count = 0;
        for (char c : query.toCharArray()) {
            if (c == '*') count++;
            if (count >= 2) return true;
        }
        return false;
    }

}