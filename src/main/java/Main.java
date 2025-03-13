import dictionary.Dictionary;

import dictionary.structure.PositionalIndex;
import utils.StopWatch;

public class Main {

    public static void main(String[] args) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Dictionary dictionary = new Dictionary(new PositionalIndex());
        dictionary.addFilesFromFolder("src/main/java/collection");

        //Dictionary dictionary = Dictionary.loadFromFile("src/main/java/dictionary/saved/dictionary.dict");
        //Dictionary dictionary = Dictionary.deserialize("src/main/java/dictionary/saved/dictionary.ser");

        //he had been to Sibyl Vane

        for (String document : dictionary.documentsFromQuery("*ian /1 g*"))
            System.out.println(document);
//
//        System.out.println();

        //dictionary.serialize("src/main/java/dictionary/saved/dictionary.ser");
        //dictionary.writeToFile("src/main/java/dictionary/saved/dictionary.dict");

        System.out.println(stopWatch.stop() + "ms");

    }

}