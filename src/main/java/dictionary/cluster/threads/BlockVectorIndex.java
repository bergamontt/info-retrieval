package dictionary.cluster.threads;

import dictionary.cluster.SparseVector;
import dictionary.cluster.TermIndexer;
import parser.Normalizer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockVectorIndex {

    private final Map<String, SparseVector> documents = new HashMap<>();
    private final ConcurrentHashMap<Integer, Integer> documentFrequency;
    private final TermIndexer termIndexer;

    public BlockVectorIndex(
            TermIndexer termIndexer,
            ConcurrentHashMap<Integer, Integer> documentFrequency
    ) {
        this.termIndexer = termIndexer;
        this.documentFrequency = documentFrequency;
    }

    public void addDocument(String document, List<String> terms) {
        SparseVector vector = new SparseVector();
        Set<String> documentTerms = new HashSet<>();
        Normalizer normalizer = new Normalizer(terms);
        for (String term : normalizer.getNormalizedTerms()) {
            termIndexer.addTerm(term);
            int termIndex = termIndexer.getTermID(term);
            vector.put(termIndex, vector.get(termIndex) + 1.0f);
            if (!documentTerms.contains(term))
                documentFrequency.compute(termIndex, (key, value) -> value == null ? 1 : value + 1);
            documentTerms.add(term);
        }
        documents.put(document, vector);
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(documents.size());
        for (String document : documents.keySet()) {
            writer.writeInt(document.length());
            writer.writeChars(document);
            SparseVector documentVector = documents.get(document);
            documentVector.writeToFile(writer);
        }
    }

    public void clear() {
        documents.clear();
    }

}