package dictionary.structure;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface DictionaryDataStructure extends Serializable {

    void addDocumentTerms(List<String> terms, int docID);
    Iterable<Integer> getDocIDsWithTerm(String term);
    Iterable<Integer> getDocIDsFromQuery(String query) throws NoSuchMethodException;
    void writeToFile(BufferedWriter bufferedWriter) throws IOException;

}