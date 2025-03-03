package dictionary.structure.posting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PositionPosting extends Posting implements Comparable<PositionPosting>, Serializable {
    private List<Position> positions ;

    public PositionPosting(int docID, List<Position> positions) {
        super(docID);
        this.positions = positions;
    }

    public static PositionPosting parsePosition(String line) {
        String[] postingInfo = line.replaceFirst(":", ",").split(",");
        int docID = Integer.parseInt(postingInfo[0]);
        List<Position> positions = new ArrayList<>();
        for (int i = 1; i < postingInfo.length; ++i)
            positions.add(new Position(Integer.parseInt(postingInfo[i])));
        return new PositionPosting(docID, positions);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDocID()).append(":");
        for(Position position : positions)
            sb.append(position).append(",");
        return sb.toString();
    }
}