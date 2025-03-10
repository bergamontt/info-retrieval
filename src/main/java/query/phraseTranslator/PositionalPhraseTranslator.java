package query.phraseTranslator;

import utils.StemmerUtils;

public class PositionalPhraseTranslator extends PhraseTranslator {
    @Override
    public String translate(String phrase) {
        String[] tokens = phrase.split(" ");
        StringBuilder result = new StringBuilder(StemmerUtils.stem(tokens[0]));
        for (int i = 1; i < tokens.length; i++)
            result.append(" ").append(StemmerUtils.stem(tokens[i])).append(" /").append(1);
        return result.toString();
    }
}
