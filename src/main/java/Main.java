import dictionary.Dictionary;

import dictionary.DiskDictionary;

import utils.StemmerUtils;
import utils.StopWatch;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

       //Dictionary posting = new Dictionary(new PositionalIndex());


        //DiskDictionary dictionary = DiskDictionary.load("src/main/java/indexed_collection/dict/dict.txt");
        DiskDictionary dictionary = new DiskDictionary();
        dictionary.buildFromDirectory("D:/info/books/books");

        for (String str : dictionary.getDocsWithTerm("a"))
         System.out.println(str);

        //he had been to Sibyl Vane

//        for (String document : posting.documentsFromQuery("d* /1 g*"))
//            System.out.println(document);

        dictionary.save("src/main/java/indexed_collection/dict/dict.txt");
        System.out.println(stopWatch.stop() + "ms");

    }

}