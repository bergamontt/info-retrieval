package dictionary.structure.posting;

import java.util.ArrayList;
import java.util.List;

public class PositionPosting extends Posting implements Comparable<PositionPosting> {
    private List<Position> positions ;

    public PositionPosting(int docID, List<Position> positions) {
        super(docID);
        this.positions = positions;
    }

    public PositionPosting(int docID) {
        this(docID, new ArrayList<>());
    }

    public void addPosition(int position) {
        positions.add(new Position(position));
    }

    public void addPosition(Position position) {
        positions.add(position);
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
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