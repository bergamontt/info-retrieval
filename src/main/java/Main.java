import dictionary.Dictionary;

import dictionary.DiskDictionary;
import dictionary.DiskZoneDictionary;
import utils.StopWatch;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //DiskZoneDictionary dictionary = DiskZoneDictionary.load("src/main/java/indexed_collection/dict/dict.txt");

        DiskZoneDictionary dictionary = new DiskZoneDictionary();
        dictionary.buildFromDirectory("D:/info/books/books");

        System.out.println("saving");

       for (String word: dictionary.getDocsWithTerm("mississippi"))
           System.out.println(word);

//        for (String term : dictionary.allTerms())
//            System.out.println(term);

        dictionary.save("src/main/java/indexed_collection/dict/dict.txt");

        System.out.println(stopWatch.stop() + "ms");

    }

}