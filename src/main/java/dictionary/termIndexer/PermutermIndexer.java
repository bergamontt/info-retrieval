package dictionary.termIndexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PermutermIndexer implements TermIndexer {

    private final Trie permuterm = new Trie();

    @Override
    public void addTerm(String term) {
        List<String> permutations = getCyclicPermutations(term);
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
        String translatedQuery = shiftAsterisk(query).replace("*", "");
        List<String> rawTerms = permuterm.termsStartWith(translatedQuery);
        return filterRawTerms(rawTerms);
    }

    private List<String> filterRawTerms(List<String> terms) {
        HashSet<String> termSet = new HashSet<>();
        for (String term : terms) {
            int dollarIndex = term.indexOf('$');
            String rotatedTerm = rotate(term, terms.size() - dollarIndex, true);
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
        if (asteriskIndex == -1)
            return query;
        return rotate(query, query.length() - asteriskIndex, true);
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