package query;

import dictionary.docID.SimpleIndexer;
import parser.TxtParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DocumentFilter {

    private final SimpleIndexer simpleIndexer;

    public DocumentFilter(SimpleIndexer simpleIndexer) {
        this.simpleIndexer = simpleIndexer;
    }

    public List<Integer> filter(List<Integer> documents, String query) {
        List<Integer> filtered = new ArrayList<>();
        String[] queryTokens = clearQuery(query).split(" ");
        for (int docID : documents) {
            File file = simpleIndexer.getDocumentByID(docID);
            TxtParser parser = new TxtParser(file);
            List<String> terms = parser.getTerms();
            if (contains(queryTokens, terms))
                filtered.add(docID);
        }
        return filtered;
    }

    private String clearQuery(String query) {
        return query.replace("\n", " ")
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .toLowerCase();
    }

    public boolean contains(String[] queryTokens, List<String> terms) {
        int n = queryTokens.length, m = terms.size();
        for (int i = 0; i <= m - n; ++i) {
            int j = 0;
            while (j < n && queryTokens[j].equals(terms.get(i + j))) j++;
            if (j == n) return true;
        }
        return false;
    }

}