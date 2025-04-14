package dictionary.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class DocIndexer {
    private final Map<String, VectorPosition> documents = new HashMap<>();

    public void addDocument(String document, int position, int blockID) {
        VectorPosition vectorPosition = new VectorPosition();
        vectorPosition.setVectorPosition(position);
        vectorPosition.setBlockID(blockID);
        documents.put(document, vectorPosition);
    }

    public List<String> getNRandomDocuments(int n) {
        List<String> randomDocuments = new ArrayList<>(documents.keySet());
        Collections.shuffle(randomDocuments);
        return randomDocuments.stream().limit(n).toList();
    }

    public static DocIndexer load(DataInputStream reader) throws IOException {
        DocIndexer indexer = new DocIndexer();
        int docsCount = reader.readInt();
        for (int i = 0; i < docsCount; i++) {
            int docLength = reader.readInt();
            char[] buffer = new char[docLength];
            for (int j = 0; j < docLength; j++)
                buffer[j] = reader.readChar();
            String document = new String(buffer);
            int blockID = reader.readInt();
            int position = reader.readInt();
            indexer.addDocument(document, position, blockID);
        }
        return indexer;
    }

    public void writeToFile(DataOutputStream writer) throws IOException  {
        writer.writeInt(documents.size());
        for (String document : documents.keySet()) {
            writer.writeInt(document.length());
            writer.writeChars(document);
            VectorPosition vectorPosition = documents.get(document);
            writer.writeInt(vectorPosition.getBlockID());
            writer.writeInt(vectorPosition.getVectorPosition());
        }
    }

    public Set<String> getAllDocuments() {
        return documents.keySet();
    }

    public VectorPosition getVectorPosition(String document) {
        return documents.get(document);
    }

    public int getDocumentCount() {
        return documents.size();
    }

}