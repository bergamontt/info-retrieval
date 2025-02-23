package dictionary.structure;

import dictionary.structure.query.BooleanRetrieval;
import dictionary.structure.query.QueryEngine;
import utils.BitSetUtils;

import java.io.*;
import java.util.*;

public class IncidenceMatrix implements Serializable, DictionaryDataStructure, BooleanRetrieval<BitSet> {

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
        QueryEngine<BitSet> queryEngine = new QueryEngine<>(this);
        BitSet result = queryEngine.getDocIDsFromQuery(query);
        return getDocIDsFromBitSet(result);
    }

    public void writeToFile(BufferedWriter fileWriter) throws IOException {
        fileWriter.write("matrix\n");
        fileWriter.write(incidenceMatrix.size() + "\n");
        for (String term : incidenceMatrix.keySet()) {
            BitSet bitSet = incidenceMatrix.get(term);
            fileWriter.write(term + ":" + bitSet.toString() + "\n");
        }
    }

    public static IncidenceMatrix readFromFile(BufferedReader fileReader) throws IOException {
        IncidenceMatrix matrix = new IncidenceMatrix();
        int termCount = Integer.parseInt(fileReader.readLine());
        for (int i = 0; i < termCount; ++i) {
            String[] termInfo = fileReader.readLine().split(":");
            String term = termInfo[0];
            BitSet termBitSet = BitSetUtils.toBitSet(termInfo[1]);
            matrix.incidenceMatrix.put(term, termBitSet);
        }
        return matrix;
    }

    @Override
    public BitSet negate(BitSet operand) {
        BitSet result = (BitSet) operand.clone();
        result.flip(0, operand.length());
        return result;
    }

    @Override
    public BitSet intersect(BitSet operand1, BitSet operand2) {
        BitSet result = (BitSet) operand1.clone();
        result.and(operand2);
        return result;
    }

    @Override
    public BitSet concatenate(BitSet operand1, BitSet operand2) {
        BitSet result = (BitSet) operand1.clone();
        result.or(operand2);
        return result;
    }

    @Override
    public BitSet getTermRawDocIDs(String token) {
        return incidenceMatrix.get(token);
    }

    @Override
    public BitSet removeSmallestInSize(Stack<BitSet> operands) {
        return operands.pop();
    }

    @Override
    public boolean contains(String term) {
        return incidenceMatrix.containsKey(term);
    }

    private Iterable<Integer> getDocIDsFromBitSet(BitSet bitSet) {
        List<Integer> docIDs = new ArrayList<>();
        if (bitSet == null) return docIDs;
        for (int i = 0; i < bitSet.length(); i++)
            if (bitSet.get(i)) docIDs.add(i);
        return docIDs;
    }

}