package dictionary.structure.query.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListIntegerBooleanOperators implements BooleanOperators<List<Integer>> {

    private final int fileCount;

    public ListIntegerBooleanOperators(int fileCount) {
        this.fileCount = fileCount;
    }

    @Override
    public List<Integer> intersect(List<Integer> p1, List<Integer> p2) {
        List<Integer> result = new ArrayList<>();
        int i1 = 0, i2 = 0;
        while (i1 < p1.size() && i2 < p2.size()) {
            int docID1 = p1.get(i1);
            int docID2 = p2.get(i2);
            if (docID1 == docID2) {
                result.add(docID1);
                ++i1; ++i2;
            } else if (docID1 < docID2) ++i1;
            else ++i2;
        }
        return result;
    }

    @Override
    public List<Integer> negate(List<Integer> p1) {
        List<Integer> result = new ArrayList<>();
        int lastDocID = -1;
        for (int docID : p1) {
            for (int i = lastDocID + 1; i < docID; ++i)
                result.add(i);
            lastDocID = docID;
        }
        for (int i = lastDocID + 1; i < fileCount; ++i)
            result.add(i);
        return result;
    }

    @Override
    public List<Integer> concatenate(List<Integer> p1, List<Integer> p2) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < p1.size() || j < p2.size()) {
            int currentValue;
            if (i < p1.size() && (j >= p2.size() || p1.get(i) < p2.get(j)))
                currentValue = p1.get(i++);
            else currentValue = p2.get(j++);
            if (!listContains(result, currentValue))
                result.add(currentValue);
        }
        return result;
    }

    @Override
    public List<Integer> positionalIntersect(List<Integer> operand1, List<Integer> operand2, int k) throws NoSuchMethodException {
        throw new NoSuchMethodException("List<Integer> does not support positional intersect");
    }

    private boolean listContains(List<Integer> documents, int docID) {
        int index = Collections.binarySearch(documents, docID);
        return index >= 0;
    }

}