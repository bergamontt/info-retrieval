package dictionary.cluster;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class SparseVectorReader {
    private final DocIndexer docIndexer;
    private final static String postingPath = "src/main/java/indexed_collection/posting/posting.txt";

    public SparseVectorReader(DocIndexer docIndexer) {
        this.docIndexer = docIndexer;
    }

    public SparseVector readSparseVector(String document) {
        int position = docIndexer.getVectorPosition(document);
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(postingPath)))) {
            dis.skipBytes(position);
            int vectorSize = dis.readInt();
            SparseVector vector = new SparseVector();
            for (int i = 0; i < vectorSize; i++) {
                int index = dis.readInt();
                float value = dis.readFloat();
                vector.put(index, value);
            }
            return vector;
        } catch (IOException ignored)
        {return null;}
    }

}