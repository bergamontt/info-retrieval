package query.phraseTranslator;

import utils.StemmerUtils;

public class BiwordPhraseTranslator extends PhraseTranslator {

    @Override
    public String translate(String phrase) {
        String[] words = phrase.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < words.length; ++i) {
            result.append(StemmerUtils.stem(words[i - 1])).append("$").append(StemmerUtils.stem(words[i]));
            if (i != words.length - 1) result.append(" & ");
        }
        return result.toString();
    }
}