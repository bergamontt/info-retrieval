
import dictionary.cluster.ClusterDictionary;
import utils.StopWatch;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchMethodException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String savedDictionary = "src/main/java/indexed_collection/dict/dict.txt";

        //ClusterDictionary clusterDictionary = ClusterDictionary.loadFromFile(savedDictionary);

        ClusterDictionary clusterDictionary = new ClusterDictionary();
        clusterDictionary.buildFromDirectory("D:\\info2\\books\\books");

        for (String docs : clusterDictionary.getTenSimilarDocsFromQuery("mississippi"))
            System.out.println(docs);

        clusterDictionary.writeToFile(savedDictionary);

        System.out.println(stopWatch.stop() + "ms");

    }

}