package dictionary.disk;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Block {

    private DataInputStream reader;
    private String term;
    private List<Integer> posting;

    public Block(File file) {
        try { reader = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        } catch (FileNotFoundException ignored) {}
        nextLine();
    }

    public String getTerm() {
        return term;
    }

    public List<Integer> getPosting() {
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
            for (int i = 0; i < postingLength; i++)
                posting.add(reader.readInt());
            return true;
        } catch (EOFException e) {
            reader.close();
            return false;
        }
    }

}