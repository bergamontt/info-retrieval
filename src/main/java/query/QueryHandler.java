package query;

import dictionary.Indexer;
import dictionary.termIndexer.TermIndexer;
import pattern.factory.*;
import query.phraseTranslator.DefaultPhraseTranslator;
import query.phraseTranslator.PhraseTranslator;
import dictionary.structure.DictionaryDataStructure;
import query.wildcardTranslator.WildcardTranslator;
import utils.QueryUtils;

import java.util.List;

public class QueryHandler {

    private final DictionaryDataStructure structure;
    private final TermIndexer termIndexer;
    private final Indexer indexer;

    public QueryHandler(DictionaryDataStructure structure, TermIndexer termIndexer, Indexer indexer) {
        this.structure = structure;
        this.termIndexer = termIndexer;
        this.indexer = indexer;
    }

    public List<Integer> getDocIDs(String query) throws NoSuchMethodException {
        String translatedPhrase = translateWildCardQuery(query);
        String translatedQuery = translatePhrase(translatedPhrase);
        List<Integer> docIDS = structure.getDocIDsFromQuery(translatedQuery);
        return filter(docIDS, query);
    }

    private String translatePhrase(String query) {
        if (!QueryUtils.isQueryPhrase(query)) {
            PhraseTranslator translator = new DefaultPhraseTranslator();
            return translator.translate(query);
        }
        PhraseTranslatorFactory factory = new PhraseTranslatorFactory();
        PhraseTranslator translator = factory.createPhraseTranslator(structure);
        return translator.translate(query);
    }

    private String translateWildCardQuery(String query) {
        WildcardTranslatorFactory factory = new WildcardTranslatorFactory();
        WildcardTranslator translator = factory.create(termIndexer);
        return translator.translate(query);
    }

    private List<Integer> filter(List<Integer> docIDS, String query) {
        if (QueryUtils.isQueryPhrase(query)) {
            DocumentFilter documentFilter = new DocumentFilter(indexer);
            return documentFilter.filter(docIDS, query);
        }
        return docIDS;
    }

}