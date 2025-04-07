package dictionary.structure;

import posting.ZonedPosting;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ZonedDictionaryDataStructure extends Serializable {

    void addTerm(String term, long position);
    void addTerms(Map<String,Integer> terms);
    Set<String> allTerms();
    List<ZonedPosting> getDocIDsWithTerm(String term);
    List<ZonedPosting> getDocIDsFromQuery(String query) throws NoSuchMethodException;
    void writeToFile(DataOutputStream writer) throws IOException;
    int uniqueWords();

}