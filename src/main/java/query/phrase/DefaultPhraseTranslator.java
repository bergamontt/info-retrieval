package query.phrase;

import utils.StemmerUtils;

public class DefaultPhraseTranslator extends PhraseTranslator {
    @Override
    public String translate(String phrase) {
        StringBuilder result = new StringBuilder();
        String[] tokens = phrase.split(" ");
        for (String token : tokens) {
            if (isOperand(token))
                result.append(" ").append(token).append(" ");
            else result.append(StemmerUtils.stem(token)).append(" ");
        }
        return result.toString();
    }

    private boolean isOperand(String token) {
        return token.equals("&") || token.equals("|") || token.equals("!") || token.contains("/");
    }
}
