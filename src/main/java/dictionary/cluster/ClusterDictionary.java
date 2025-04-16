package dictionary.cluster;

import dictionary.cluster.threads.VectorBuilderThreadDelegator;
import utils.FileLoader;
import utils.QueryUtils;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterDictionary {

    private ConcurrentHashMap<Integer, Integer> documentFrequency = new ConcurrentHashMap<>();
    private TermIndexer termIndexer = new TermIndexer();
    private DocIndexer docIndexer;
    private Cluster cluster;

    public void buildFromDirectory(String directory) throws IOException {
        File directoryFile = FileLoader.loadFolder(directory);
        VectorBuilderThreadDelegator builderDelegator = new VectorBuilderThreadDelegator(documentFrequency, termIndexer);
        builderDelegator.runIndexingThreads(directoryFile);
        BlockVectorMerger merger = new BlockVectorMerger(documentFrequency);
        docIndexer = merger.mergeBlocks();
        cluster = new Cluster(docIndexer);
        cluster.buildCluster();
    }

    public List<String> getTopTenSimilarDocuments(String query) {
        List<String> queryTerms = QueryUtils.getQueryTerms(query);
        SparseVector queryVector = termIndexer.buildVectorFromTerms(queryTerms);
        VectorNormalizer normalizer = new VectorNormalizer(documentFrequency);
        normalizer.computeVector(queryVector);
        return cluster.findTenSimilarDocs(queryVector);
    }

    public void writeToFile(String filename) throws IOException {
        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
        DocumentFrequencyWriter.writeToFile(writer, documentFrequency);
        termIndexer.writeToFile(writer);
        docIndexer.writeToFile(writer);
        cluster.writeToFile(writer);
        writer.close();
    }

    public static ClusterDictionary load(String filename) throws IOException {
        ClusterDictionary clusterDictionary = new ClusterDictionary();
        DataInputStream reader = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        clusterDictionary.documentFrequency = DocumentFrequencyWriter.load(reader);
        clusterDictionary.termIndexer = TermIndexer.load(reader);
        clusterDictionary.docIndexer = DocIndexer.load(reader);
        clusterDictionary.cluster = Cluster.load(reader, clusterDictionary.docIndexer);
        reader.close();
        return clusterDictionary;
    }

}