package dictionary.structure.posting;

public class Posting {
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