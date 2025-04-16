
import dictionary.cluster.ClusterDictionary;
import utils.StopWatch;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, NoSuchMethodException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String savedDictionary = "src/main/java/indexed_collection/dict/dict.txt";
        String bigDictionary = "D:\\info2\\books\\books";

        ClusterDictionary clusterDictionary = ClusterDictionary.load(savedDictionary);

        //ClusterDictionary clusterDictionary = new ClusterDictionary();
        //clusterDictionary.buildFromDirectory("src/main/java/collection");

        for (String docs : clusterDictionary.getTopTenSimilarDocuments("god heaven bible"))
            System.out.println(docs);

        //clusterDictionary.writeToFile(savedDictionary);

        System.out.println(stopWatch.stop() + "ms");

    }

}