package dictionary.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class DocIndexer {
    private final Map<String, Integer> documents = new HashMap<>();

    public void addDocument(String document, int position) {
        documents.put(document, position);
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
            int position = reader.readInt();
            indexer.addDocument(document, position);
        }
        return indexer;
    }

    public void writeToFile(DataOutputStream writer) throws IOException  {
        writer.writeInt(documents.size());
        for (String document : documents.keySet()) {
            writer.writeInt(document.length());
            writer.writeChars(document);
            writer.writeInt(documents.get(document));
        }
    }

    public Set<String> getAllDocuments() {
        return documents.keySet();
    }

    public Integer getVectorPosition(String document) {
        return documents.get(document);
    }

    public int getDocumentCount() {
        return documents.size();
    }

}