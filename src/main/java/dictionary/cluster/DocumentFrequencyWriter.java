package dictionary.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentFrequencyWriter {

    public static void writeToFile(DataOutputStream writer, Map<Integer, Integer> documentFrequency) throws IOException {
        writer.writeInt(documentFrequency.size());
        for (Integer termID : documentFrequency.keySet()) {
            writer.writeInt(termID);
            writer.writeInt(documentFrequency.get(termID));
        }
    }

    public static ConcurrentHashMap<Integer, Integer> load(DataInputStream reader) throws IOException {
        ConcurrentHashMap<Integer, Integer> documentFrequency = new ConcurrentHashMap<>();
        int termCount = reader.readInt();
        for (int i = 0; i < termCount; i++) {
            int termID = reader.readInt();
            int frequency = reader.readInt();
            documentFrequency.put(termID, frequency);
        }
        return documentFrequency;
    }

}