import dictionary.Dictionary;
import dictionary.structure.Biword;
import dictionary.structure.IncidenceMatrix;
import utils.StopWatch;

public class Main {

    public static void main(String[] args) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Dictionary dictionary = new Dictionary(new Biword());
        dictionary.addFilesFromFolder("src/main/java/collection");

        //Dictionary dictionary = Dictionary.loadFromFile("src/main/java/dictionary/saved/dictionary.dict");
        //Dictionary dictionary = Dictionary.deserialize("src/main/java/dictionary/saved/dictionary.ser");

        if (dictionary == null) return;

        for (String document : dictionary.documentsWithTerm("negative"))
            System.out.println(document);

        System.out.println();

        for (String document : dictionary.documentsWithTerm("grain"))
            System.out.println(document);

        System.out.println();

        for (String document : dictionary.documentsWithTerm("treasure"))
            System.out.println(document);

        System.out.println();

        for (String document : dictionary.documentsFromQuery("the man who"))
            System.out.println(document);

        System.out.println();

        //dictionary.serialize("src/main/java/dictionary/saved/dictionary.ser");
        //dictionary.writeToFile("src/main/java/dictionary/saved/dictionary.dict");

        System.out.println(stopWatch.stop() + "ms");

    }

}