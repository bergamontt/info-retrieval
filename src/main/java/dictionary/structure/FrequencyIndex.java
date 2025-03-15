package dictionary.structure;

import parser.Normalizer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class FrequencyIndex {

    private Map<String, List<Integer>> index = new TreeMap<>();

    public void addDocumentTerms(List<String> terms, int docID) {
        Normalizer normalizer = new Normalizer(terms);
        for (String term : normalizer.getNormalizedTerms()) {
            List<Integer> docIDs = index.getOrDefault(term, new ArrayList<>());
            if (containsTerm(docIDs, docID)) continue;
            docIDs.add(docID);
            index.put(term, docIDs);
        }
    }

    public void writeToFile(BufferedWriter bufferedWriter) throws IOException {
        for (Map.Entry<String, List<Integer>> entry : index.entrySet()) {
            bufferedWriter.write(entry.getKey() + " ");
            for (int docID : entry.getValue())
                bufferedWriter.write(docID + " ");
            bufferedWriter.newLine();
        }
    }

    public void clear() {
        index.clear();
    }

    private boolean containsTerm(List<Integer> docIDs, int docID) {
        int position = Collections.binarySearch(docIDs, docID);
        return position >= 0;
    }

}