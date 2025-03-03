package pattern.factory;

import structure.Biword;
import structure.DictionaryDataStructure;
import structure.PositionalIndex;
import query.phrase.BiwordPhraseTranslator;
import query.phrase.DefaultPhraseTranslator;
import query.phrase.PhraseTranslator;
import query.phrase.PositionalPhraseTranslator;

public class PhraseTranslatorFactory {

    public PhraseTranslator createPhraseTranslator(DictionaryDataStructure dataStructure) {
        if (dataStructure instanceof Biword)
            return new BiwordPhraseTranslator();
        else if (dataStructure instanceof PositionalIndex)
            return new PositionalPhraseTranslator();
        return new DefaultPhraseTranslator();
    }

}