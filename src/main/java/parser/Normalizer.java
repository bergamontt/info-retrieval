package parser;

import utils.StemmerUtils;

import java.util.ArrayList;
import java.util.List;

public class Normalizer {

    private final List<String> normalizedWords;

    public Normalizer(List<String> words) {
        normalizedWords = normalize(words);
    }

    public List<String> getNormalizedTerms() {
        return normalizedWords;
    }

    private List<String> normalize(List<String> words) {
        List<String> normalized = new ArrayList<>();
        for (String word : words)
            normalized.add(StemmerUtils.stem(word));
        return normalized;
    }

}