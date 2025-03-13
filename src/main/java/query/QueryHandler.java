package query;

import pattern.factory.*;
import query.phraseTranslator.DefaultPhraseTranslator;
import query.phraseTranslator.PhraseTranslator;
import dictionary.structure.DictionaryDataStructure;
import utils.QueryUtils;

import java.util.List;

public class QueryHandler {

    private final DictionaryDataStructure structure;

    public QueryHandler(DictionaryDataStructure structure) {
        this.structure = structure;
    }

    public List<Integer> getDocIDs(String query) throws NoSuchMethodException {
        String translatedQuery = translatePhrase(query);
        return structure.getDocIDsFromQuery(translatedQuery);
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

}