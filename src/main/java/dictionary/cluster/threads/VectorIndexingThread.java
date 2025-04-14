package dictionary.cluster.threads;

import dictionary.cluster.BlockIndexer;
import dictionary.cluster.DocIndexer;
import dictionary.cluster.TermIndexer;
import parser.TxtParser;

import java.io.*;
import java.util.List;

public class VectorIndexingThread implements Runnable {

    private final static int MAX_BLOCK_SIZE = 10000000;

    private final List<File> indexingFiles;
    private final TermIndexer termIndexer;
    private final BlockIndexer blockIndexer;
    private final DocIndexer docIndexer;
    private final int threadID;

    public VectorIndexingThread(
            TermIndexer termIndexer,
            BlockIndexer blockIndexer,
            DocIndexer docIndexer,
            List<File> indexingFiles, int threadID
    ) {
        this.indexingFiles = indexingFiles;
        this.blockIndexer = blockIndexer;
        this.termIndexer = termIndexer;
        this.docIndexer = docIndexer;
        this.threadID = threadID;
    }

    @Override
    public void run() {
        System.out.println("Indexing thread " + threadID);
        try { invertDirectoryInBlocks(); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    private void invertDirectoryInBlocks() throws IOException {
        int blockCount = 0;
        int currentBlockSize = 0;
        AuxDocumentIndexer indexer = new AuxDocumentIndexer(termIndexer);
        for (File file : indexingFiles) {
            TxtParser txtParser = new TxtParser(file);
            List<String> terms = txtParser.getTerms();
            if (blockIsFull(currentBlockSize, terms.size())) {
                writeIndexToFile(indexer, blockCount++);
                indexer.clear();
                currentBlockSize = 0;
            }
            indexer.addDocument(file.getAbsolutePath(), terms);
            currentBlockSize += terms.size();
        }
        writeIndexToFile(indexer, blockCount);
    }

    private boolean blockIsFull(int currentBlockSize, int termsCount) {
        return currentBlockSize + termsCount > MAX_BLOCK_SIZE;
    }

    private void writeIndexToFile(AuxDocumentIndexer indexer, int blockNumber) throws IOException {
        int blockID = threadID * 100 + blockNumber;
        String path = "src/main/java/indexed_collection/blocks/" + blockID + ".txt";
        blockIndexer.index(blockID, path);
        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
        indexer.writeToFile(writer, docIndexer, blockID);
        System.out.println("read blocks: " + blockNumber);
        writer.close();
    }

}