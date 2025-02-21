package dictionary;

import java.util.*;

public class InvertedIndex {

    Map<String, List<Integer>> invertedIndex = new HashMap<>();
    private int fileCount = 0;

    public Iterable<String> getTerms() {
        return invertedIndex.keySet();
    }

    public void addDocumentTerms(List<String> terms, int docID) {
        for (String term : terms) {
            List<Integer> documents = invertedIndex.getOrDefault(term, new ArrayList<>());
            if (documentsHasDocument(documents, docID)) continue;
            documents.add(docID);
            invertedIndex.put(term, documents);
        }
        ++fileCount;
    }

    public Iterable<Integer> getDocIDsWithTerm(String term) {
        return invertedIndex.get(term);
    }

    public Iterable<Integer> getDocIDsFromQuery(String query) {

        String[] tokens = query.split(" ");
        Stack<String> operators = new Stack<>();
        Stack<List<Integer>> operands = new Stack<>();
        boolean negate = false;

        for (String token : tokens) {
            if (token.equals("&"))
                operators.push(token);
            else if (token.equals("|")) {
                executeOperators(operators, operands);
                operators.push(token);
            } else if (token.equals("!"))
               negate = true;
            else if (negate) {
                List<Integer> negated = negate(invertedIndex.get(token));
                operands.push(negated);
                negate = false;
            }
            else {
               operands.push(invertedIndex.get(token));
            }
        }

        executeOperators(operators, operands);
        return operands.pop();
    }

    private List<Integer> intersect(List<Integer> p1, List<Integer> p2) {
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

    private List<Integer> negate(List<Integer> p1) {
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

    private List<Integer> concatenate(List<Integer> p1, List<Integer> p2) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < p1.size() || j < p2.size()) {
            int currentValue;
            if (i < p1.size() && (j >= p2.size() || p1.get(i) < p2.get(j)))
                currentValue = p1.get(i++);
            else currentValue = p2.get(j++);
            if (!documentsHasDocument(result, currentValue))
                result.add(currentValue);
        }
        return result;
    }

    private void executeOperators(Stack<String> operators, Stack<List<Integer>> operands) {
        List<Integer> smallest = removeSmallestInSize(operands);
        while (!operators.isEmpty()) {
            String operator = operators.pop();
            List<Integer> operand = operands.pop();
            if (operator.equals("&"))
                smallest = intersect(smallest, operand);
            else smallest = concatenate(smallest, operand);
        }
        operands.push(smallest);
    }

    private List<Integer> removeSmallestInSize(Stack<List<Integer>> operands) {
        List<Integer> smallest = operands.peek();
        for (List<Integer> operand : operands)
            if (smallest.size() > operand.size())
                smallest = operand;
        operands.remove(smallest);
        return smallest;
    }

    private boolean documentsHasDocument(List<Integer> documents, int docID) {
        int index = Collections.binarySearch(documents, docID);
        return index >= 0;
    }

}