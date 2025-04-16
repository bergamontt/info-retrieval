package dictionary.cluster;

import java.util.concurrent.ConcurrentHashMap;

public class VectorNormalizer {
    private final ConcurrentHashMap<Integer, Integer> documentFrequency;

    public VectorNormalizer(ConcurrentHashMap<Integer, Integer> documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public void computeVector(SparseVector vector) {
        for (int index : vector.getIndices()) {
            float tf = vector.get(index);
            int n = documentFrequency.size();
            int df = documentFrequency.getOrDefault(index, 1);
            float idf = (float) Math.log((double) n / df);
            float tf_idf = tf * idf;
            vector.put(index, tf_idf);
        }
        vector.normalize();
    }

}