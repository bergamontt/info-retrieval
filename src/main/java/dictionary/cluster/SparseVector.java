package dictionary.cluster;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TreeMap;

public class SparseVector {
    private final TreeMap<Integer, Float> vector = new TreeMap<>();

    public void put(int index, float value) {
        if (value == 0.0)
            vector.remove(index);
        else vector.put(index, value);
    }

    public double get(int index) {
        if (vector.containsKey(index))
            return vector.get(index);
        return 0.0;
    }

    public int nnz() {
        return vector.size();
    }

    public double dot(SparseVector that) {
        double sum = 0.0;
        if (this.nnz() <= that.nnz()) {
            for (int i : this.vector.keySet())
                if (that.vector.containsKey(i))
                    sum += this.get(i) * that.get(i);
        }
        else {
            for (int i : that.vector.keySet())
                if (this.vector.containsKey(i))
                    sum += this.get(i) * that.get(i);
        }
        return sum;
    }

    public double norm() {
        return Math.sqrt(dot(this));
    }

    public double cosineSimilarity(SparseVector that) {
        double denominator = this.norm() * that.norm();
        if (denominator == 0.0) return 0.0;
        return this.dot(that) / denominator;
    }

    public int writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(nnz());
        for (int index : vector.keySet()) {
            writer.writeInt(index);
            writer.writeFloat(vector.get(index));
        }
        return nnz();
    }

}