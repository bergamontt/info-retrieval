package dictionary.cluster.threads;

import dictionary.cluster.BlockIndexer;
import dictionary.cluster.DocIndexer;
import dictionary.cluster.TermIndexer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VectorThreadDelegator {
    private final static int INDEXING_THREADS = 16;

    private final TermIndexer termIndexer;
    private final BlockIndexer blockIndexer;
    private final DocIndexer docIndexer;

    public VectorThreadDelegator(
            TermIndexer termIndexer,
            BlockIndexer blockIndexer,
            DocIndexer docIndexer
    ) {
        this.termIndexer = termIndexer;
        this.blockIndexer = blockIndexer;
        this.docIndexer = docIndexer;
    }

    public void runIndexingThreads(File directoryFile) {
        File[] directoryFilesArr = directoryFile.listFiles();
        assert directoryFilesArr != null;
        List<File> directoryFiles = new ArrayList<>(List.of(directoryFilesArr));
        int filesPerThread = directoryFiles.size() / INDEXING_THREADS;
        Thread[] threads = new Thread[INDEXING_THREADS];
        for (int i = 0; i < INDEXING_THREADS; i++) {
            List<File> threadFiles = new ArrayList<>(filesPerThread);
            if (i == INDEXING_THREADS - 1) {
                threadFiles.addAll(directoryFiles);
            } else {
                for (int j = 0; j < filesPerThread; j++) {
                    File currFile = directoryFiles.remove(0);
                    threadFiles.add(currFile);
                }
            }
            VectorIndexingThread currThread =  new VectorIndexingThread(termIndexer, blockIndexer, docIndexer, threadFiles, i);
            threads[i] = new Thread(currThread);
            threads[i].start();
        }
        joinThreads(threads);
    }

    private void joinThreads(Thread[] threads) {
        for (Thread thread : threads) {
            try { thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e); }
        }
    }

}