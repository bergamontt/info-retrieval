package dictionary.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TermIndexer {

    private final ConcurrentHashMap<String, Integer> termIDs = new ConcurrentHashMap<>();

    public static TermIndexer load(DataInputStream reader) throws IOException {
        TermIndexer indexer = new TermIndexer();
        int termIDsCount = reader.readInt();
        for (int i = 0; i < termIDsCount; i++) {
            int termLength = reader.readInt();
            char[] buffer = new char[termLength];
            for (int j = 0; j < termLength; j++)
                buffer[j] = reader.readChar();
            String term = new String(buffer);
            int termID = reader.readInt();
            indexer.termIDs.put(term, termID);
        }
        return indexer;
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(termIDs.size());
        for (String term : termIDs.keySet()) {
            writer.writeInt(term.length());
            writer.writeChars(term);
            writer.writeInt(termIDs.get(term));
        }
    }

    public void addTerm(String term) {
        termIDs.computeIfAbsent(term, k -> termIDs.size() + 1);
    }

    public SparseVector buildVectorFromTerms(List<String> terms) {
        SparseVector vector = new SparseVector();
        for (String term : terms) {
            if (!termIDs.containsKey(term))
                continue;
            int termID = termIDs.get(term);
            vector.put(termID, 1.0f);
        }
        return vector;
    }

    public int getTermID(String term) {
        return termIDs.get(term);
    }

    public boolean contains(String term) {
        return termIDs.containsKey(term);
    }

}
