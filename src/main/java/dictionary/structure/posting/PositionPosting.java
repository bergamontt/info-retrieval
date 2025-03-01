package dictionary.structure.posting;

import java.util.ArrayList;
import java.util.List;

public class PositionPosting extends Posting implements Comparable<PositionPosting> {
    private List<Integer> positions ;

    public PositionPosting(int docID, List<Integer> positions) {
        super(docID);
        this.positions = positions;
    }

    public PositionPosting(int docID) {
        this(docID, new ArrayList<>());
    }

    public void addPosition(int position) {
        positions.add(position);
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PositionPosting that)
            return this.getDocID() == that.getDocID();
        return false;
    }

    @Override
    public int compareTo(PositionPosting that) {
        return Integer.compare(this.getDocID(), that.getDocID());
    }

}