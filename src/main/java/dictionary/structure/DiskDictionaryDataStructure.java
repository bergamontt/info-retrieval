package dictionary.structure;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public interface DiskDictionaryDataStructure {

    void addTerm(String term, long position);
    List<Integer> getDocIDsWithTerm(String term);
    List<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException;
    void writeToFile(BufferedWriter bufferedWriter) throws IOException;

}
