package dictionary.structure;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DiskDictionaryDataStructure {

    void addTerm(String term, long position);
    void addTerms(Map<String,Integer> terms);
    Set<String> allTerms();
    List<Integer> getDocIDsWithTerm(String term);
    List<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException;
    void writeToFile(DataOutputStream writer) throws IOException;
    int uniqueWords();


}
