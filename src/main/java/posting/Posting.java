package posting;

import java.io.Serializable;

public class Posting implements Serializable {
    private int docID;

    public Posting(int docID) {
        this.docID = docID;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }
}