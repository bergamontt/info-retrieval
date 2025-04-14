package dictionary.cluster.threads;

import dictionary.cluster.BlockIndexer;
import dictionary.cluster.DocIndexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClusterThreadDelegator {
    private static final int COMPUTING_THREADS = 16;

    private final DocIndexer docIndexer;
    private final BlockIndexer blockIndexer;
    private final Map<String, List<String>> clusters;
    private final List<String> leaders;

    public ClusterThreadDelegator(
            DocIndexer docIndexer,
            BlockIndexer blockIndexer,
            Map<String, List<String>> clusters,
            List<String> leaders
    ) {
        this.docIndexer = docIndexer;
        this.blockIndexer = blockIndexer;
        this.clusters = clusters;
        this.leaders = leaders;
    }

    public void runDelegatingThreads(ArrayList<String> documents) {
        System.out.println(documents.size());
        Thread[] threads = new Thread[COMPUTING_THREADS];
        int documentPerThread = documents.size() / COMPUTING_THREADS;
        for (int i = 0; i < COMPUTING_THREADS; i++) {
            List<String> computedDocuments = new ArrayList<>(documentPerThread);
            if (i == COMPUTING_THREADS - 1) {
                computedDocuments.addAll(documents);
            } else {
                for (int j = 0; j < documentPerThread; j++) {
                    String document = documents.remove(0);
                    computedDocuments.add(document);
                }
            }
            ClusterComputingThread thread = new ClusterComputingThread(computedDocuments, docIndexer, blockIndexer, clusters, leaders);
            threads[i] = new Thread(thread);
            threads[i].start();
        }
        joinThreads(threads);
    }

    private void joinThreads(Thread[] threads) {
        for (Thread thread : threads) {
            try { thread.join();}
            catch (InterruptedException e)
            { throw new RuntimeException(e); }
        }
    }

}