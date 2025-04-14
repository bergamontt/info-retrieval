package dictionary.cluster.threads;

import dictionary.cluster.BlockIndexer;
import dictionary.cluster.DocIndexer;
import dictionary.cluster.SparseVector;
import dictionary.cluster.VectorPosition;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClusterComputingThread implements Runnable {
    private final List<String> documents;
    private final DocIndexer docIndexer;
    private final BlockIndexer blockIndexer;
    private final Map<String, List<String>> clusters;
    private final List<String> leaders;

    public ClusterComputingThread(
            List<String> documents,
            DocIndexer docIndexer, BlockIndexer blockIndexer,
            Map<String, List<String>> clusters, List<String> leaders) {
        this.documents = documents;
        this.docIndexer = docIndexer;
        this.blockIndexer = blockIndexer;
        this.clusters = clusters;
        this.leaders = leaders;
    }

    @Override
    public void run() {
        List<String> leaders = clusters.keySet().stream().toList();
        calculateKNNForEachDocument(leaders);
    }

    private void calculateKNNForEachDocument(List<String> leaders) {
        for (String document : documents) {
            SparseVector docVector = getSparseVector(document);
            if (docVector == null) continue;
            String closestLeader = findClosestLeader(docVector);
            List<String> cluster = clusters.getOrDefault(closestLeader, new ArrayList<>());
            cluster.add(document);
            clusters.put(closestLeader, cluster);
        }
    }

    private String findClosestLeader(SparseVector vector) {
        String closestLeader = leaders.get(0);
        double similarity = Double.MIN_VALUE;
        for (String leader : leaders) {
            SparseVector leaderVector = getSparseVector(leader);
            if (leaderVector == null) continue;
            double currSimilarity =  vector.cosineSimilarity(leaderVector);
            if (currSimilarity > similarity) {
                similarity = currSimilarity;
                closestLeader = leader;
            }
        }
        return closestLeader;
    }

    private SparseVector getSparseVector(String document) {
        VectorPosition currVectorPosition = docIndexer.getVectorPosition(document);
        int blockID = currVectorPosition.getBlockID();
        int vectorPosition = currVectorPosition.getVectorPosition();
        String documentPath = blockIndexer.getBlockPath(blockID);
        return readSparseVector(documentPath, vectorPosition);
    }

    private SparseVector readSparseVector(String documentPath, int position) {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(documentPath)))) {
            dis.skipBytes(position);
            int vectorSize = dis.readInt();
            SparseVector vector = new SparseVector();
            for (int i = 0; i < vectorSize; i++) {
                int index = dis.readInt();
                float value = dis.readFloat();
                vector.put(index, value);
            }
            return vector;
        } catch (IOException ignored)
        {return null;}
    }

}