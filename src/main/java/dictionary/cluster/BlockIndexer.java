package dictionary.cluster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BlockIndexer {
    private final Map<Integer, String> blocks = new HashMap<>();

    public static BlockIndexer load(DataInputStream reader) throws IOException {
        BlockIndexer indexer = new BlockIndexer();
        int blocksCount = reader.readInt();
        for (int i = 0; i < blocksCount; i++) {
            int blockID = reader.readInt();
            int pathLength = reader.readInt();
            char[] buffer = new char[pathLength];
            for (int j = 0; j < pathLength; j++)
                buffer[j] = reader.readChar();
            String blockPath = new String(buffer);
            indexer.blocks.put(blockID, blockPath);
        }
        return indexer;
    }

    public void writeToFile(DataOutputStream writer) throws IOException {
        writer.writeInt(blocks.size());
        for (int blockID : blocks.keySet()) {
            writer.writeInt(blockID);
            String blockPath = blocks.get(blockID);
            writer.writeInt(blockPath.length());
            writer.writeChars(blockPath);
        }
    }

    public void index(int blockID, String blockPath) {
        blocks.put(blockID, blockPath);
    }

    public String getBlockPath(int blockID) {
        return blocks.get(blockID);
    }

}