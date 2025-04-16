package dictionary.cluster.threads;

import dictionary.cluster.TermIndexer;
import dictionary.structure.FrequencyIndex;
import parser.TxtParser;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VectorBuilderThread implements Runnable {
    private final static int MAX_BLOCK_SIZE = 10000000;

    private final ConcurrentHashMap<Integer, Integer> documentFrequency;
    private final List<File> indexingFiles;
    private final TermIndexer termIndexer;
    private final int threadID;

    public VectorBuilderThread(
            ConcurrentHashMap<Integer, Integer> documentFrequency,
            TermIndexer termIndexer,
            List<File> indexingFiles,
            int threadID
    ) {
        this.documentFrequency = documentFrequency;
        this.indexingFiles = indexingFiles;
        this.termIndexer = termIndexer;
        this.threadID = threadID;
    }

    @Override
    public void run() {
        System.out.println("Indexing thread " + threadID);
        try { invertDirectoryInBlocks();}
        catch (IOException e) {throw new RuntimeException(e); }
    }

    private void invertDirectoryInBlocks() throws IOException {
        int blockCount = 0;
        int currentBlockSize = 0;
        BlockVectorIndex blockIndex = new BlockVectorIndex(termIndexer, documentFrequency);
        for (File file : indexingFiles) {
            TxtParser txtParser = new TxtParser(file);
            List<String> terms = txtParser.getTerms();
            if (blockIsFull(currentBlockSize, terms.size())) {
                writeIndexToFile(blockIndex, blockCount++);
                blockIndex.clear();
                currentBlockSize = 0;
            }
            String filepath = file.getAbsolutePath();
            blockIndex.addDocument(filepath, terms);
            currentBlockSize += terms.size();
        }
        writeIndexToFile(blockIndex, blockCount);
    }

    private boolean blockIsFull(int currentBlockSize, int termsCount) {
        return currentBlockSize + termsCount > MAX_BLOCK_SIZE;
    }

    private void writeIndexToFile(BlockVectorIndex blockIndex, int blockNumber) throws IOException {
        String blockPath = "src/main/java/indexed_collection/blocks/" + threadID + '-' + blockNumber + ".txt";
        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(blockPath)));
        blockIndex.writeToFile(writer);
        writer.close();
        System.out.println("Read block: " + blockNumber);
    }

}