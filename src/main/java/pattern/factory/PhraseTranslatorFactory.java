package pattern.factory;

import dictionary.structure.*;
import query.phraseTranslator.*;

public class PhraseTranslatorFactory {

    public PhraseTranslator createPhraseTranslator(DictionaryDataStructure dataStructure) {
        if (dataStructure instanceof Biword)
            return new BiwordPhraseTranslator();
        else if (dataStructure instanceof PositionalIndex)
            return new PositionalPhraseTranslator();
        return new DefaultPhraseTranslator();
    }

}