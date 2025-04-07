package operators;

import posting.ZonedPosting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ZonedPostingBooleanOperators implements BooleanOperators<List<ZonedPosting>>{

    private final int fileCount;

    public ZonedPostingBooleanOperators(int fileCount) {
        this.fileCount = fileCount;
    }

    @Override
    public List<ZonedPosting> intersect(List<ZonedPosting> p1, List<ZonedPosting> p2) {
        List<ZonedPosting> result = new ArrayList<>();
        int i1 = 0, i2 = 0;
        while (i1 < p1.size() && i2 < p2.size()) {
            int docID1 = p1.get(i1).getDocID();
            int docID2 = p2.get(i2).getDocID();
            if (docID1 == docID2) {
                result.add(p1.get(i1));
                ++i1; ++i2;
            } else if (docID1 < docID2) ++i1;
            else ++i2;
        }
        return result;
    }

    @Override
    public List<ZonedPosting> negate(List<ZonedPosting> p1) {
//        List<ZonedPosting> result = new ArrayList<>();
//        int lastDocID = -1;
//        for (ZonedPosting zonedPosting : p1) {
//            int docID = zonedPosting.getDocID();
//            for (int i = lastDocID + 1; i < docID; ++i)
//                result.add(new ZonedPosting(i));
//            lastDocID = docID;
//        }
//        for (int i = lastDocID + 1; i < fileCount; ++i)
//            result.add(new ZonedPosting(i));
//        return result;
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ZonedPosting> concatenate(List<ZonedPosting> p1, List<ZonedPosting> p2) {
        List<ZonedPosting> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < p1.size() || j < p2.size()) {
            int currentValue;
            if (i < p1.size() && (j >= p2.size() || p1.get(i).getDocID() < p2.get(j).getDocID()))
                currentValue = p1.get(i++).getDocID();
            else currentValue = p2.get(j++).getDocID();
            if (!listContains(result, currentValue))
                result.add(new ZonedPosting(currentValue));
        }
        return result;
    }

    @Override
    public List<ZonedPosting> getSmallest(Stack<List<ZonedPosting>> stack) {
        List<ZonedPosting> smallest = stack.peek();
        for (List<ZonedPosting> operand : stack)
            if (smallest.size() > operand.size())
                smallest = operand;
        stack.remove(smallest);
        return smallest;
    }

    @Override
    public List<ZonedPosting> positionalIntersect(List<ZonedPosting> operand1, List<ZonedPosting> operand2, int k) throws NoSuchMethodException {
        throw new NoSuchMethodException("List<ZonedPosting> does not support positional intersect");
    }

    private boolean listContains(List<ZonedPosting> documents, int docID) {
        int index = Collections.binarySearch(documents, new ZonedPosting(docID));
        return index >= 0;
    }

}