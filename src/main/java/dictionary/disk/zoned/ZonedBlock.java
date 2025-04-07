package dictionary.disk.zoned;

import constants.Zone;
import posting.ZonedPosting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ZonedBlock {

    private DataInputStream reader;
    private String term;
    private List<ZonedPosting> posting;

    public ZonedBlock(File file) {
        try { reader = new DataInputStream(new BufferedInputStream(new FileInputStream(file))); }
        catch (FileNotFoundException ignored) {}
        nextLine();
    }

    public String getTerm() {
        return term;
    }

    public List<ZonedPosting> getPosting() {
        return posting;
    }

    public DataInputStream getReader() {
        return reader;
    }

    public boolean nextLine() {
        try { return tryNextLine();
        } catch (IOException ignored)
        { return false;}
    }

    private boolean tryNextLine() throws IOException {
        try {
            int termLength = reader.readInt();
            char[] buffer = new char[termLength];
            for (int i = 0; i < termLength; i++)
                buffer[i] = reader.readChar();
            term = new String(buffer);
            int postingLength = reader.readInt();
            posting = new ArrayList<>(postingLength);
            for (int i = 0; i < postingLength; i++) {
                int docID = reader.readInt();
                boolean[] zones = new boolean[Zone.values().length];
                for (int j = 0; j < zones.length; j++)
                    zones[j] = reader.readBoolean();
                posting.add(new ZonedPosting(docID, zones));
            }
            return true;
        } catch (EOFException e) {
            reader.close();
            return false;
        }
    }

}
