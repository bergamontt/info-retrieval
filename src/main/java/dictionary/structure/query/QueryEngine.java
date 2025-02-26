package dictionary.structure.query;

import dictionary.structure.query.operators.BooleanOperators;
import utils.StemmerUtils;

import java.util.Stack;

public class QueryEngine<T> {

    private final BooleanRetrieval<T> retrieval;
    private final BooleanOperators<T> booleanOperators;

    public QueryEngine(BooleanRetrieval<T> retrieval) {
        this.retrieval = retrieval;
        this.booleanOperators = retrieval.getBooleanOperators();
    }

    public T getDocIDsFromQuery(String query) {
        String[] tokens = query.split(" ");
        Stack<String> operators = new Stack<>();
        Stack<T> operands = new Stack<>();
        processTokens(tokens, operators, operands);
        executeOperators(operators, operands);
        if (!operators.isEmpty() || operands.size() != 1)
            throw new RuntimeException("Invalid query: " + query);
        return operands.pop();
    }

    private void processTokens(String[] tokens, Stack<String> operators, Stack<T> operands) {
        boolean negate = false;
        for (String token : tokens) {
            if (token.equals("&")) {
                operators.push(token);
            } else if (token.equals("|")) {
                executeOperators(operators, operands);
                operators.push(token);
            } else if (token.equals("!")) {
                negate = true;
            } else if (negate) {
                String normalizedToken = StemmerUtils.stem(token);
                validateToken(normalizedToken, token);
                T negated = retrieval.getTermRawDocIDs(normalizedToken);
                operands.push(booleanOperators.negate(negated));
                negate = false;
            } else {
                String normalizedToken = StemmerUtils.stem(token);
                validateToken(normalizedToken, token);
                operands.push(retrieval.getTermRawDocIDs(normalizedToken));
            }
        }
    }

    private void executeOperators(Stack<String> operators, Stack<T> operands) {
        if (operands.isEmpty()) return;
        T smallest = retrieval.removeSmallestInSize(operands);
        while (!operators.isEmpty()) {
            String operator = operators.pop();
            T operand = operands.pop();
            if (operator.equals("&"))
                smallest = booleanOperators.intersect(smallest, operand);
            else smallest = booleanOperators.concatenate(smallest, operand);
        }
        operands.push(smallest);
    }

    private void validateToken(String normalizedToken, String token) {
        if (!retrieval.contains(normalizedToken))
            throw new RuntimeException("Dictionary has no term: " + token);
    }

}