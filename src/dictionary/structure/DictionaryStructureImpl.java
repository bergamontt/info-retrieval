package dictionary.structure;

import parser.TxtParser;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

public class DictionaryStructureImpl implements DictionaryStructure {

    private final Map<String, List<Posting>> terms = new TreeMap<>();
    private final Map<String, BigInteger> termsCount = new HashMap<>();
    private BigInteger allTermsCount = BigInteger.ZERO;

    @Override
    public void addFile(File file) {
        TxtParser parser = new TxtParser(file);
//        addParsedTermsFromFile(parser.getTerms(), file);
    }

    @Override
    public void addTerm(String term, List<Posting> termPostings, BigInteger termCount) {
        terms.put(term, termPostings);
        termsCount.put(term, termCount);
        allTermsCount = allTermsCount.add(termCount);
    }

    @Override
    public Iterable<String> getTerms() {
        return terms.keySet();
    }

    @Override
    public Iterable<Posting> getTermPostings(String term) {
        return terms.get(term);
    }

    @Override
    public BigInteger getAllTermsCount() {
        return allTermsCount;
    }

    @Override
    public BigInteger getTermCount(String term) {
        return termsCount.get(term);
    }

    @Override
    public long getUniqueTermsCount() {
        return terms.size();
    }

    private void addParsedTermsFromFile(Map<String, Integer> terms, File file) {
        for (Map.Entry<String, Integer> posting : terms.entrySet()) {
            String term = posting.getKey();
            int count = posting.getValue();
            addPostingInTermMap(term, new Posting(file, count));
            updateTermsCount(term, BigInteger.valueOf(count));
        }
    }

    private void addPostingInTermMap(String term, Posting posting) {
        List<Posting> termPostings = terms.getOrDefault(term, new ArrayList<>());
        termPostings.add(posting);
        terms.put(term, termPostings);
    }

    private void updateTermsCount(String term, BigInteger count) {
        allTermsCount = allTermsCount.add(count);
        BigInteger oldCount = termsCount.getOrDefault(term, BigInteger.ZERO);
        termsCount.put(term, oldCount.add(count));
    }

}