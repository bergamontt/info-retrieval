package query;

import dictionary.termIndexer.TermIndexer;
import operators.BooleanOperators;
import operators.BooleanRetrieval;
import parser.Normalizer;

import java.util.List;
import java.util.Stack;

public class QueryEngine<T> {

    private final BooleanRetrieval<T> retrieval;
    private final BooleanOperators<T> booleanOperators;
    private final TermIndexer termIndexer;

    public QueryEngine(BooleanRetrieval<T> retrieval) {
        this.retrieval = retrieval;
        this.booleanOperators = retrieval.getBooleanOperators();
        termIndexer = null;
    }

    public QueryEngine(BooleanRetrieval<T> retrieval, TermIndexer termIndexer) {
        this.retrieval = retrieval;
        this.booleanOperators = retrieval.getBooleanOperators();
        this.termIndexer = termIndexer;
    }

    public T getDocIDsFromQuery(String query) throws NoSuchMethodException {
        String[] tokens = query.split(" ");
        Stack<String> operators = new Stack<>();
        Stack<T> operands = new Stack<>();
        processTokens(tokens, operators, operands);
        executeOperators(operators, operands);
        if (!operators.isEmpty() || operands.size() != 1)
            throw new RuntimeException("Invalid query");
        return operands.pop();
    }

    private void processTokens(String[] tokens, Stack<String> operators, Stack<T> operands) throws NoSuchMethodException {
        boolean negate = false;
        for (String token : tokens) {
            if (token.equals("&") || token.contains("/")) {
                operators.push(token);
            } else if (token.equals("|")) {
                executeOperators(operators, operands);
                operators.push(token);
            } else if (token.equals("!")) {
                negate = true;
            } else if (negate) {
                T negated = retrieval.getTermRawDocIDs(token);
                operands.push(booleanOperators.negate(negated));
                negate = false;
            } else if (token.contains("*")) {
                List<String> queryTerms = termIndexer.getTermsFromQuery(token.toLowerCase());
                Normalizer normalizer = new Normalizer(queryTerms);
                queryTerms = normalizer.getNormalizedTerms();
                T result = retrieval.getTermRawDocIDs(queryTerms.remove(0));
                for (String queryTerm : queryTerms)
                    result = booleanOperators.concatenate(result, retrieval.getTermRawDocIDs(queryTerm));
                operands.push(result);
            } else {
                operands.push(retrieval.getTermRawDocIDs(token));
            }
        }
    }

    private void executeOperators(Stack<String> operators, Stack<T> operands) throws NoSuchMethodException {
        if (operands.isEmpty() || operators.isEmpty()) return;
        T smallest = booleanOperators.getSmallest(operands);
        while (!operators.isEmpty()) {
            String operator = operators.pop();
            T operand = operands.pop();
            if (operator.equals("&"))
                smallest = booleanOperators.intersect(smallest, operand);
            else if (operator.contains("/")) {
                int k = Integer.parseInt(operator.replace("/", ""));
                smallest = booleanOperators.positionalIntersect(smallest, operand, k);
            } else smallest = booleanOperators.concatenate(smallest, operand);
        }
        operands.push(smallest);
    }

}