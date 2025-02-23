package utils;

import opennlp.tools.stemmer.PorterStemmer;

public class StemmerUtils {

    public static String stem(String term) {
        PorterStemmer porterStemmer = new PorterStemmer();
        return porterStemmer.stem(term.toLowerCase());
    }

}