package query.phraseTranslator;

import utils.QueryUtils;
import utils.StemmerUtils;

public class DefaultPhraseTranslator extends PhraseTranslator {
    @Override
    public String translate(String phrase) {
        StringBuilder result = new StringBuilder();
        String[] tokens = phrase.split(" ");
        for (String token : tokens) {
            if (isOperand(token))
                result.append(" ").append(token).append(" ");
            else if (token.contains("*"))
                result.append(token);
            else result.append(StemmerUtils.stem(token));
        }
        return result.toString();
    }

    private boolean isOperand(String token) {
        return token.equals("&") || token.equals("|") || token.equals("!") || token.contains("/");
    }
}
