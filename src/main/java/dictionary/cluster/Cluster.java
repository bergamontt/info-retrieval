package dictionary.cluster;

import dictionary.cluster.threads.ClusterThreadDelegator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Cluster {
    private final BlockIndexer blockIndexer;
    private final DocIndexer docIndexer;
    private final Map<String, List<String>> clusters;

    public Cluster(
            BlockIndexer blockIndexer,
            DocIndexer docIndexer
    ) {
        this.blockIndexer = blockIndexer;
        this.docIndexer = docIndexer;
        this.clusters = new HashMap<>();
    }

    public void buildCluster() {
        List<String> leaders = findLeaders();
        //calculateKNNForEachDocument(leaders);
        ClusterThreadDelegator delegator = new ClusterThreadDelegator(docIndexer, blockIndexer, clusters, leaders);
        ArrayList<String> allDocuments = new ArrayList<>(docIndexer.getAllDocuments());
        delegator.runDelegatingThreads(allDocuments);
    }

    public List<String> findTenSimilarDocs(SparseVector vector) {
        List<String> leaders = clusters.keySet().stream().toList();
        String closestLeader = findClosestLeader(vector, leaders);
        return findTenSimilarDocsWithLeader(vector, closestLeader);
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(clusters.size());
        for (String leader : clusters.keySet()) {
            writer.writeInt(leader.length());
            writer.writeChars(leader);
            List<String> followers = clusters.get(leader);
            writer.writeInt(followers.size());
            for (String follower : followers) {
                writer.writeInt(follower.length());
                writer.writeChars(follower);
            }
        }
    }

    public static Cluster load(DataInputStream reader, BlockIndexer blockIndexer, DocIndexer docIndexer) throws IOException {
        Cluster cluster = new Cluster(blockIndexer, docIndexer);
        int clustersCount = reader.readInt();
        for (int i = 0; i < clustersCount; i++) {
            int leaderLength = reader.readInt();
            char[] leaderBuffer = new char[leaderLength];
            for (int j = 0; j < leaderLength; j++)
                leaderBuffer[j] = reader.readChar();
            String leader = new String(leaderBuffer);
            int followerCount = reader.readInt();
            List<String> followers = new ArrayList<>(followerCount);
            for (int j = 0; j < followerCount; j++) {
                int followerLength = reader.readInt();
                char[] followerBuffer = new char[followerLength];
                for (int k = 0; k < followerLength; k++)
                    followerBuffer[k] = reader.readChar();
                String follower = new String(followerBuffer);
                followers.add(follower);
            }
            cluster.clusters.put(leader, followers);
        }
        return cluster;
    }

    private List<String> findLeaders() {
        int docCount = docIndexer.getDocumentCount();
        int leaderCount = (int) Math.sqrt(docCount);
        return docIndexer.getNRandomDocuments(leaderCount);
    }

    private void calculateKNNForEachDocument(List<String> leaders) {
        int docCount = docIndexer.getDocumentCount();
        int calculated = 0;
        for (String document : docIndexer.getAllDocuments()) {
            SparseVector docVector = getSparseVector(document);
            if (docVector == null) continue;
            String closestLeader = findClosestLeader(docVector, leaders);
            List<String> cluster = clusters.getOrDefault(closestLeader, new ArrayList<>());
            cluster.add(document);
            clusters.put(closestLeader, cluster);
            System.out.println(++calculated + ":  " + docCount);
        }
    }

    private String findClosestLeader(SparseVector vector, List<String> leaders) {
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

    private List<String> findTenSimilarDocsWithLeader(SparseVector vector, String closestLeader) {
        TreeMap<String, Double> followersCosine = new TreeMap<>();
        for (String follower : clusters.get(closestLeader)) {
            SparseVector followerVector = getSparseVector(follower);
            double score = vector.cosineSimilarity(followerVector);
            followersCosine.put(follower, score);
        }
        return followersCosine.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}