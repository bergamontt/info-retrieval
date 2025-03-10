package dictionary.termIndexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PermutermIndexer implements TermIndexer {

    private final Trie permuterm = new Trie();

    @Override
    public void addTerm(String term) {
        List<String> permutations = getCyclicPermutations(term.toLowerCase());
        for (String permutation : permutations)
            permuterm.addTerm(permutation);
    }

    @Override
    public void addTerms(List<String> terms) {
        for (String term : terms)
            addTerm(term);
    }

    @Override
    public List<String> getTermsFromQuery(String query) {
        String translatedQuery = translateQuery(query);
        List<String> rawTerms = permuterm.termsStartWith(translatedQuery);
        List<String> translatedTerms = translateRawTerms(rawTerms);
        return filterTerms(translatedTerms, query);
    }

    private String translateQuery(String query) {
        if (isComplex(query)) {
            String[] parts = query.split("\\*");
            query = parts[0] + "*" + parts[parts.length - 1];
        }
        return shiftAsterisk(query).replace("*", "");
    }

    private boolean isComplex(String query) {
        int count = 0;
        for (char c : query.toCharArray()) {
            if (c == '*') count++;
            if (count >= 2) return true;
        }
        return false;
    }

    private List<String> filterTerms(List<String> terms, String query) {
        if (!isComplex(query)) return terms;
        return TermIndexerFilter.filter(terms.stream().toList(), query);
    }

    private List<String> translateRawTerms(List<String> terms) {
        HashSet<String> termSet = new HashSet<>();
        for (String term : terms) {
            int dollarIndex = term.indexOf('$');
            String rotatedTerm = rotate(term, term.length() - dollarIndex - 1, true);
            termSet.add(rotatedTerm.replace("$", ""));
        }
        return new ArrayList<>(termSet);
    }

    private List<String> getCyclicPermutations(String input) {
        List<String> permutations = new ArrayList<>();
        String base = input + "$";
        for (int i = 0; i < base.length(); ++i) {
            String permutation = base.substring(i) + base.substring(0, i);
            permutations.add(permutation);
        }
        return permutations;
    }

    private String shiftAsterisk(String query) {
        int asteriskIndex = query.indexOf('*');
        if (asteriskIndex == -1 || asteriskIndex == query.length() - 1)
            return query;
        return rotate(query, asteriskIndex + 1, false);
    }

    private String rotate(String s, int c, boolean forward) {
        int len = s.length();
        int n = c % len;
        if (n == 0)
            return s;
        String ss = s + s;
        n = forward ? n : len - n;
        return ss.substring(len - n, 2 * len - n);
    }

}