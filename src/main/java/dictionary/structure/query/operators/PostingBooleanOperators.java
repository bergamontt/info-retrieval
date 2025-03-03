package dictionary.structure.query.operators;

import dictionary.structure.posting.Position;
import dictionary.structure.posting.PositionPosting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class PostingBooleanOperators implements BooleanOperators<List<PositionPosting>>{

    private final int fileCount;
    public PostingBooleanOperators(int fileCount) {
        this.fileCount = fileCount;
    }

    @Override
    public List<PositionPosting> negate(List<PositionPosting> p1) {
        List<PositionPosting> result = new ArrayList<>();
        int lastDocID = -1;
        for (PositionPosting posting : p1) {
            int docID = posting.getDocID();
            for (int i = lastDocID + 1; i < docID; ++i)
                result.add(new PositionPosting(i));
            lastDocID = docID;
        }
        for (int i = lastDocID + 1; i < fileCount; ++i)
            result.add(new PositionPosting(i));
        return result;
    }

    @Override
    public List<PositionPosting> intersect(List<PositionPosting> p1, List<PositionPosting> p2) {
        List<PositionPosting> result = new ArrayList<>();
        int i1 = 0, i2 = 0;
        while (i1 < p1.size() && i2 < p2.size()) {
            int docID1 = p1.get(i1).getDocID();
            int docID2 = p2.get(i2).getDocID();
            if (docID1 == docID2) {
                result.add(new PositionPosting(docID1));
                ++i1; ++i2;
            } else if (docID1 < docID2) ++i1;
            else ++i2;
        }
        return result;
    }

    @Override
    public List<PositionPosting> positionalIntersect(List<PositionPosting> p1, List<PositionPosting> p2, int k)
            throws NoSuchMethodException
    {
        List<PositionPosting> result = new ArrayList<>();
        int i1 = 0, i2 = 0;
        while (i1 < p1.size() && i2 < p2.size()) {
            int docID1 = p1.get(i1).getDocID();
            int docID2 = p2.get(i2).getDocID();
            if (docID1 == docID2) {
                List<Position> pp1 = p1.get(i1).getPositions();
                List<Position> pp2 = p2.get(i2).getPositions();
                List<Position> currPositions = new ArrayList<>();
                int ip1 = 0, ip2 = 0;
                while (ip1 < pp1.size() && ip2 < pp2.size()) {
                    Position pos1 = pp1.get(ip1);
                    Position pos2 = pp2.get(ip2);
                    if (Math.abs(pos1.subtract(pos2)) < k) {
                        currPositions.add(new Position(pos1.getStart(), pos2.getEnd()));
                        ++ip2;
                    } else if (pos1.compareTo(pos2) < 0) {
                        ++ip1;
                    } else ++ip2;
                }
                result.add(new PositionPosting(docID1, currPositions));
                ++i1; ++i2;
            } else if (docID1 < docID2) ++i1;
            else ++i2;
        }
        return result;
    }

    @Override
    public List<PositionPosting> concatenate(List<PositionPosting> p1, List<PositionPosting> p2) {
        List<PositionPosting> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < p1.size() || j < p2.size()) {
            PositionPosting currentPosting;
            if (i < p1.size() && (j >= p2.size() || p1.get(i).compareTo(p2.get(j)) < 0))
                currentPosting = p1.get(i++);
            else currentPosting = p2.get(j++);
            if (!listContains(result, currentPosting))
                result.add(currentPosting);
        }
        return result;
    }

    @Override
    public List<PositionPosting> getSmallest(Stack<List<PositionPosting>> stack) {
        return stack.pop();
    }

    private boolean listContains(List<PositionPosting> documents, PositionPosting posting) {
        int index = Collections.binarySearch(documents, posting);
        return index >= 0;
    }

}