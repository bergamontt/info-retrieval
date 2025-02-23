package dictionary;

import utils.StemmerUtils;

import java.util.Stack;

public class QueryEngine<T> {

    private final BooleanRetrieval<T> retrieval;

    public QueryEngine(BooleanRetrieval<T> retrieval) {
        this.retrieval = retrieval;
    }

    public T getDocIDsFromQuery(String query) {
        String[] tokens = query.split(" ");
        Stack<String> operators = new Stack<>();
        Stack<T> operands = new Stack<>();
        processTokens(tokens, operators, operands);
        executeOperators(operators, operands);
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
                T negated = retrieval.getTermRawDocIDs(normalizedToken);
                operands.push(retrieval.negate(negated));
                negate = false;
            } else {
                String normalizedToken = StemmerUtils.stem(token);
                operands.push(retrieval.getTermRawDocIDs(normalizedToken));
            }
        }
    }

    private void executeOperators(Stack<String> operators, Stack<T> operands) {
        T smallest = retrieval.removeSmallestInSize(operands);
        while (!operators.isEmpty()) {
            String operator = operators.pop();
            T operand = operands.pop();
            if (operator.equals("&"))
                smallest = retrieval.intersect(smallest, operand);
            else smallest = retrieval.concatenate(smallest, operand);
        }
        operands.push(smallest);
    }

}