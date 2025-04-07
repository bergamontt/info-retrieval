package dictionary.threads;

import dictionary.docID.DiskIndexer;
import dictionary.structure.FrequencyIndex;
import parser.TxtParser;

import java.io.*;
import java.util.List;

public class BasicIndexingThread implements Runnable {

    private final static int MAX_BLOCK_SIZE = 10000000;

    private final List<File> indexingFiles;
    private final DiskIndexer indexer;
    private final int threadID;
    private int freeID;

    public BasicIndexingThread(DiskIndexer indexer, List<File> indexingFiles, int threadID, int freeID) {
        this.indexingFiles = indexingFiles;
        this.indexer = indexer;
        this.threadID = threadID;
        this.freeID = freeID;
    }

    @Override
    public void run() {
        System.out.println("Indexing thread " + threadID);
        try { invertDirectoryInBlocks();
        } catch (IOException e) {
            throw new RuntimeException(e); }
    }

    private void invertDirectoryInBlocks() throws IOException {
        int blockCount = 0;
        int currentBlockSize = 0;
        FrequencyIndex frequencyIndex = new FrequencyIndex();
        for (File file : indexingFiles) {
            TxtParser txtParser = new TxtParser(file);
            List<String> terms = txtParser.getTerms();
            if (blockIsFull(currentBlockSize, terms.size())) {
                writeIndexToFile(frequencyIndex, blockCount++);
                frequencyIndex = new FrequencyIndex();
                currentBlockSize = 0;
            }
            indexer.index(file, freeID);
            frequencyIndex.addDocumentTerms(terms, freeID++);
            currentBlockSize += terms.size();
        }
        writeIndexToFile(frequencyIndex, blockCount);
    }

    private boolean blockIsFull(int currentBlockSize, int termsCount) {
        return currentBlockSize + termsCount > MAX_BLOCK_SIZE;
    }

    private void writeIndexToFile(FrequencyIndex frequencyIndex, int blockNumber) throws IOException {
        DataOutputStream writer = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream("src/main/java/indexed_collection/blocks/" + threadID + '-' + blockNumber + ".txt")));
        frequencyIndex.writeToFile(writer);
        System.out.println("read blocks: " + blockNumber);
        writer.close();
    }
}
