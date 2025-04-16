package dictionary.cluster;

import utils.FileLoader;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class BlockVectorMerger {
    private final static String blocksPath = "src/main/java/indexed_collection/blocks";
    private final static String postingPath = "src/main/java/indexed_collection/posting/posting.txt";
    private final ConcurrentHashMap<Integer, Integer> documentFrequency;

    public BlockVectorMerger(ConcurrentHashMap<Integer, Integer> documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public DocIndexer mergeBlocks() throws IOException {
        int currPosition = 0;
        DocIndexer docIndexer = new DocIndexer();
        File blocksDirectoryFile = FileLoader.loadFolder(blocksPath);
        DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(postingPath)));
        for (File block : Objects.requireNonNull(blocksDirectoryFile.listFiles())) {
            DataInputStream reader = new DataInputStream(new BufferedInputStream(new FileInputStream(block)));
            int documentsCount = reader.readInt();
            for (int i = 0; i < documentsCount; i++) {
                int documentLength = reader.readInt();
                char[] docBuffer = new char[documentLength];
                for (int j = 0; j < documentLength; j++)
                    docBuffer[j] = reader.readChar();
                String document = new String(docBuffer);
                int vectorLength = reader.readInt();
                SparseVector vector = new SparseVector();
                for (int j = 0; j < vectorLength; j++) {
                    int vectorIndex = reader.readInt();
                    float vectorValue = reader.readFloat();
                    vector.put(vectorIndex, vectorValue);
                }
                VectorNormalizer normalizer = new VectorNormalizer(documentFrequency);
                normalizer.computeVector(vector);
                vector.writeToFile(writer);
                docIndexer.addDocument(document, currPosition);
                currPosition += Integer.BYTES * (vectorLength + 1) + Float.BYTES * vectorLength;
            }
        }
        writer.close();
        return docIndexer;
    }

}