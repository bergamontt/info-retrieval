package dictionary.cluster;

import dictionary.cluster.threads.VectorThreadDelegator;
import parser.Normalizer;
import parser.Tokenizer;
import utils.FileLoader;

import java.io.*;
import java.util.List;

public class ClusterDictionary {

    private TermIndexer termIndexer = new TermIndexer();
    private BlockIndexer blockIndexer = new BlockIndexer();
    private DocIndexer docIndexer = new DocIndexer();
    private Cluster cluster;

    public static ClusterDictionary loadFromFile(String filepath) throws IOException {
        ClusterDictionary dictionary = new ClusterDictionary();
        DataInputStream reader = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)));
        dictionary.termIndexer = TermIndexer.load(reader);
        dictionary.blockIndexer = BlockIndexer.load(reader);
        dictionary.docIndexer = DocIndexer.load(reader);
        dictionary.cluster = Cluster.load(reader, dictionary.blockIndexer, dictionary.docIndexer);
        reader.close();
        return dictionary;
    }

    public void buildFromDirectory(String directory) {
        File directoryFile = FileLoader.loadFolder(directory);
        VectorThreadDelegator delegator = new VectorThreadDelegator(termIndexer, blockIndexer, docIndexer);
        delegator.runIndexingThreads(directoryFile);
        cluster = new Cluster(blockIndexer, docIndexer);
        cluster.buildCluster();
    }

    public void writeToFile(String filepath) throws IOException {
        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filepath)));
        termIndexer.writeToFile(writer);
        blockIndexer.writeToFile(writer);
        docIndexer.writeToFile(writer);
        cluster.writeToFile(writer);
        writer.close();
    }

    public List<String> getTenSimilarDocsFromQuery(String query) {
        List<String> queryTerms = getQueryTerms(query);
        SparseVector queryVector = termIndexer.buildVectorFromTerms(queryTerms);
        return cluster.findTenSimilarDocs(queryVector);
    }

    private List<String> getQueryTerms(String query) {
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenize(query);
        Normalizer normalizer = new Normalizer(tokens);
        return normalizer.getNormalizedTerms();
    }

}