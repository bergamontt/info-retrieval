package dictionary.structure;

import java.io.File;
import java.io.Serializable;

public class Posting implements Serializable {
    private File file;
    private int frequency;

    public Posting(File file, int frequency) {
        this.file = file;
        this.frequency = frequency;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String toString() {
        return file.getName() + " " + frequency;
    }

}