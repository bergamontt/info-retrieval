package dictionary.cluster.threads;

import dictionary.cluster.DocIndexer;
import dictionary.cluster.SparseVector;
import dictionary.cluster.TermIndexer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuxDocumentIndexer {
    private final Map<String, SparseVector> documents = new HashMap<>();
    private final TermIndexer termIndexer;

    public AuxDocumentIndexer(TermIndexer termIndexer) {
        this.termIndexer = termIndexer;
    }

    public void addDocument(String document, List<String> terms) {
        SparseVector documentVector = new SparseVector();
        for (String term : terms) {
            termIndexer.addTerm(term);
            documentVector.put(termIndexer.getTermID(term), 1.0f);
        }
        documents.put(document, documentVector);
    }

    public void writeToFile(DataOutputStream writer, DocIndexer docIndexer, int blockID) throws IOException {
        int currPosition = 0;
        for (String document : documents.keySet()) {
            docIndexer.addDocument(document, currPosition, blockID);
            SparseVector documentVector = documents.get(document);
            int vectorLength = documentVector.writeToFile(writer);
            currPosition += vectorLength * Integer.BYTES + (vectorLength + 1) + vectorLength * Float.BYTES;
        }
    }

    public void clear() {
        documents.clear();
    }

}