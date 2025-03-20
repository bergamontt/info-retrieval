import dictionary.Dictionary;

import dictionary.DiskDictionary;
import utils.StopWatch;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        DiskDictionary dictionary = DiskDictionary.load("src/main/java/indexed_collection/dict/dict.txt");

        //DiskDictionary dictionary = new DiskDictionary();
        //dictionary.buildFromDirectory("D:/info/books/books");

        System.out.println("saving");

        //dictionary.save("src/main/java/indexed_collection/dict/dict.txt");

       for (String word: dictionary.getDocsWithTerm("a"))
           System.out.println(word);

        System.out.println(stopWatch.stop() + "ms");

        System.out.println(dictionary.getUniqueWords());
        System.out.println(dictionary.getNonUniqueWords());

    }

}