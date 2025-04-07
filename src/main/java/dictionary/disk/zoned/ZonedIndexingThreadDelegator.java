package dictionary.disk.zoned;

import dictionary.docID.DiskIndexer;
import dictionary.threads.ZonedIndexingThread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ZonedIndexingThreadDelegator {

    private final static int INDEXING_THREADS = 16;
    private final DiskIndexer indexer;

    public ZonedIndexingThreadDelegator(DiskIndexer indexer) {
        this.indexer = indexer;
    }

    public void runIndexingThreads(File directoryFile) {
        File[] directoryFilesArr = directoryFile.listFiles();
        assert directoryFilesArr != null;
        List<File> directoryFiles = new ArrayList<>(List.of(directoryFilesArr));
        int filesPerThread = directoryFiles.size() / INDEXING_THREADS;
        Thread[] threads = new Thread[INDEXING_THREADS];
        for (int i = 0; i < INDEXING_THREADS; i++) {
            List<File> threadFiles = new ArrayList<>();
            if (i == INDEXING_THREADS - 1) {
                threadFiles.addAll(directoryFiles);
            } else {
                for (int j = 0; j < filesPerThread; j++) {
                    File currFile = directoryFiles.remove(0);
                    threadFiles.add(currFile);
                }
            }
            int currFreeID = i * (filesPerThread + 1);
            ZonedIndexingThread currThread =  new ZonedIndexingThread(indexer, threadFiles, i, currFreeID);
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
