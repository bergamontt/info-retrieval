package dictionary.structure;

import java.io.File;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public interface DictionaryStructure extends Serializable {

    Iterable<String> getTerms();
    Iterable<Posting> getTermPostings(String term);
    BigInteger getAllTermsCount();
    BigInteger getTermCount(String term);
    long getUniqueTermsCount();
    void addFile(File file);
    void addTerm(String term, List<Posting> termPostings, BigInteger termCount);

}