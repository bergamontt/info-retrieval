package query.phrase;

public abstract class PhraseTranslator {

    public abstract String translate(String phrase);

    public boolean isPhraseQuery(String query) {
        return (!query.contains("|") && !query.contains("!") && !query.contains("&"));
    }

}