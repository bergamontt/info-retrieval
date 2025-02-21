package dictionary;

import java.util.*;

public class IncidenceMatrix {

    private final Map<String, BitSet> incidenceMatrix = new HashMap<>();

    public Iterable<String> getTerms() {
        return incidenceMatrix.keySet();
    }

    public void addDocumentTerms(List<String> terms, int docID) {
        for (String term : terms) {
            BitSet bitSet = incidenceMatrix.getOrDefault(term, new BitSet());
            bitSet.set(docID, true);
            incidenceMatrix.put(term, bitSet);
        }
    }

    public Iterable<Integer> getDocIDsWithTerm(String term) {
        BitSet bitSet = incidenceMatrix.get(term);
        return getDocIDsFromBitSet(bitSet);
    }

    public Iterable<Integer> getDocIDsFromQuery(String query) {
        String[] tokens = query.split(" ");
        Stack<String> operators = new Stack<>();
        Stack<BitSet> operands = new Stack<>();
        boolean negate = false;
        for (String token : tokens) {
            if (token.equals("!"))
                negate = true;
            else if (token.equals("&"))
                operators.push(token);
            else if (token.equals("|")) {
                executeOperators(operators, operands);
                operators.push(token);
            } else if (!negate)
                operands.push(incidenceMatrix.get(token));
            else {
                BitSet bitSet = incidenceMatrix.get(token);
                BitSet result = (BitSet) bitSet.clone();
                result.flip(0, bitSet.length());
                operands.push(result);
                negate = false;
            }
        }
        executeOperators(operators, operands);
        return getDocIDsFromBitSet(operands.pop());
    }

    private void executeOperators(Stack<String> operators, Stack<BitSet> operands) {
        while (!operators.empty()) {
            String operator = operators.pop();
            BitSet scdOperand = operands.pop();
            BitSet fstOperand = operands.pop();
            BitSet result = (BitSet)fstOperand.clone();
            if (operator.equals("&")) {
                result.and(scdOperand);
            } else if (operator.equals("|"))
                result.or(scdOperand);
            else result.or(scdOperand);
            operands.push(result);
        }
    }

    private Iterable<Integer> getDocIDsFromBitSet(BitSet bitSet) {
        if (bitSet == null) return null;
        List<Integer> docIDs = new ArrayList<>();
        for (int i = 0; i < bitSet.length(); i++)
            if (bitSet.get(i)) docIDs.add(i);
        return docIDs;
    }

}