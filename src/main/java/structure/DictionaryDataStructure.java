package structure;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface DictionaryDataStructure extends Serializable {

    void addDocumentTerms(List<String> terms, int docID);
    List<Integer> getDocIDsWithTerm(String term);
    List<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException;
    void writeToFile(BufferedWriter bufferedWriter) throws IOException;

}