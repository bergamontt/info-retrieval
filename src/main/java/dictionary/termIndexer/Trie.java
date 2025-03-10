package dictionary.termIndexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie {

    private Node root = new Node();

    private static class Node {
        private final Map<Character, Node> next = new HashMap<>();
        private char c;
        private boolean end;

        public Node(char c) {
            this.c = c;
        }

        public Node() {}

    }

    public void addTerm(String term) {
        Node currNode = root;
        char[] chars = term.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (!currNode.next.containsKey(c))
                currNode.next.put(c, new Node(c));
            if (i == chars.length - 1)
                currNode.next.get(c).end = true;
            currNode = currNode.next.get(c);
        }
    }

    public List<String> termsStartWith(String prefix) {
        Node currNode = root;
        for (char c : prefix.toCharArray()) {
            currNode = currNode.next.get(c);
            if (currNode == null) return new ArrayList<>();
        }
        return getTermsFrom(prefix, currNode);
    }

    private List<String> getTermsFrom(String prefix, Node currNode) {
        List<String> terms = new ArrayList<>();
        if (currNode.end) terms.add(prefix);
        fillTerms(currNode, terms, prefix);
        return terms;
    }

    private void fillTerms(Node currNode, List<String> terms, String currentTerm) {
        for (Node child : currNode.next.values())
            fillTerms(child, terms, currentTerm + child.c);
        if (currNode.end)
            terms.add(currentTerm);
    }

}