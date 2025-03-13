package dictionary.termIndexer;

import java.util.*;

public class KgramIndexer implements TermIndexer {

    private final Map<String, List<String>> kgrams = new HashMap<>();

    @Override
    public void addTerm(String term) {
        String base = "$$" + term.toLowerCase() + "$$";
        List<String> termKgrams = kgrams(base);
        for (String kgram : termKgrams) {
            List<String> terms = kgrams.getOrDefault(kgram, new ArrayList<>());
            terms.add(term);
            kgrams.put(kgram, terms);
        }
    }

    @Override
    public void addTerms(List<String> terms) {
        for (String term : terms)
            addTerm(term);
    }

    @Override
    public List<String> getTermsFromQuery(String query) {
        query = query.toLowerCase();
        String[] tokens = translateQuery(query).split("\\*");
        List<String> allKgrams = mergeTokensKgrams(tokens);
        Set<String> terms = new HashSet<>(kgrams.get(allKgrams.get(0)));
        for (int i = 1; i < allKgrams.size(); ++i) {
            Set<String> intersected = new HashSet<>();
            List<String> kgramTerms = kgrams.get(allKgrams.get(i));
            for (String kgramTerm : kgramTerms)
                if (terms.contains(kgramTerm))
                    intersected.add(kgramTerm);
            terms = intersected;
        }
        return TermIndexerFilter.filter(terms.stream().toList(), query);
    }

    public String translateQuery(String query) {
        if (!query.startsWith("*"))
            query = "$$" + query.toLowerCase();
        if (!query.endsWith("*"))
            query += "$$";
        return query;
    }

    private List<String> mergeTokensKgrams(String[] tokens) {
        List<String> mergedKgrams = new ArrayList<>();
        for (String token : tokens)
            if (token.length() > 2)
                mergedKgrams.addAll(kgrams(token));
        return mergedKgrams;
    }

    private List<String> kgrams(String term) {
        List<String> kgrams = new ArrayList<>();
        for (int i = 0; i < term.length() - 2; ++i)
            kgrams.add(term.substring(i, i + 3));
        return kgrams;
    }

}
