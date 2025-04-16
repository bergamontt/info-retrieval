package dictionary.cluster;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

public class SparseVector {
    private final TreeMap<Integer, Float> vector = new TreeMap<>();

    public void put(int index, float value) {
        if (value == 0.0)
            vector.remove(index);
        else vector.put(index, value);
    }

    public float get(int index) {
        if (vector.containsKey(index))
            return vector.get(index);
        return 0.0f;
    }

    public int nnz() {
        return vector.size();
    }

    public float dot(SparseVector that) {
        float sum = 0.0f;
        if (this.nnz() <= that.nnz()) {
            for (int i : this.vector.keySet())
                if (that.vector.containsKey(i))
                    sum += (this.get(i) * that.get(i));
        }
        else {
            for (int i : that.vector.keySet())
                if (this.vector.containsKey(i))
                    sum += (this.get(i) * that.get(i));
        }
        return sum;
    }

    public double norm() {
        return Math.sqrt(dot(this));
    }

    public float cosineSimilarity(SparseVector that) {
        double denominator = this.norm() * that.norm();
        if (denominator == 0.0) return 0.0f;
        return (float) (this.dot(that) / denominator);
    }

    public void normalize() {
        double norm = norm();
        if (norm == 0.0) return;
        vector.replaceAll((i, v) -> (float) (vector.get(i) / norm));
    }

    public void scalarMultiply(float scalar) {
        if (scalar == 0.0) {
            vector.clear();
        } else vector.replaceAll((i, v) -> vector.get(i) * scalar);
    }

    public Set<Integer> getIndices() {
        return vector.keySet();
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(nnz());
        for (int index : vector.keySet()) {
            writer.writeInt(index);
            writer.writeFloat(vector.get(index));
        }
    }

}