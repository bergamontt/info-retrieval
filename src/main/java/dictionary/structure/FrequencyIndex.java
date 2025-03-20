package dictionary.structure;

import parser.Normalizer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class FrequencyIndex {

    private final Map<String, List<Integer>> index = new TreeMap<>();

    public void addDocumentTerms(List<String> terms, int docID) {
        Normalizer normalizer = new Normalizer(terms);
        for (String term : normalizer.getNormalizedTerms()) {
            List<Integer> docIDs = index.getOrDefault(term, new ArrayList<>());
            if (containsTerm(docIDs, docID)) continue;
            docIDs.add(docID);
            index.put(term, docIDs);
        }
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        for (String term : index.keySet()) {
            writer.writeInt(term.length());
            writer.writeChars(term);
            writer.writeInt(index.get(term).size());
            for (int docID : index.get(term))
                writer.writeInt(docID);
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