package dictionary.cluster.threads;

import dictionary.cluster.TermIndexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VectorBuilderThreadDelegator {

    private final static int BUILDING_THREADS = 16;
    private final ConcurrentHashMap<Integer, Integer> documentFrequency;
    private final TermIndexer termIndexer;

    public VectorBuilderThreadDelegator(
            ConcurrentHashMap<Integer, Integer> documentFrequency,
            TermIndexer termIndexer
    ) {
        this.documentFrequency = documentFrequency;
        this.termIndexer = termIndexer;
    }

    public void runIndexingThreads(File directoryFile) {
        File[] directoryFilesArr = directoryFile.listFiles();
        assert directoryFilesArr != null;
        List<File> directoryFiles = new ArrayList<>(List.of(directoryFilesArr));
        int filesPerThread = directoryFiles.size() / BUILDING_THREADS;
        Thread[] threads = new Thread[BUILDING_THREADS];
        for (int i = 0; i < BUILDING_THREADS; i++) {
            List<File> threadFiles = new ArrayList<>();
            if (i == BUILDING_THREADS - 1) {
                threadFiles.addAll(directoryFiles);
            } else {
                for (int j = 0; j < filesPerThread; j++) {
                    File currFile = directoryFiles.remove(0);
                    threadFiles.add(currFile);
                }
            }
            VectorBuilderThread currThread =  new VectorBuilderThread(documentFrequency, termIndexer, threadFiles, i);
            threads[i] = new Thread(currThread);
            threads[i].start();
        }
        joinThreads(threads);
    }

    private void joinThreads(Thread[] threads) {
        for (Thread thread : threads) {
            try { thread.join();}
            catch (InterruptedException e)
            {throw new RuntimeException(e); }
        }
    }

}