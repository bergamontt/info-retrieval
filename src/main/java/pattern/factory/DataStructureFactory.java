package pattern.factory;

import dictionary.structure.*;

import java.io.BufferedReader;
import java.io.IOException;

public class DataStructureFactory {

    public DictionaryDataStructure createDataStructure(String dsType, BufferedReader bufferedReader)
            throws IOException
    {
        return switch (dsType) {
            case "matrix" -> IncidenceMatrix.readFromFile(bufferedReader);
            case "index" -> InvertedIndex.readFromFile(bufferedReader);
            case "biindex" -> Biword.readFromFile(bufferedReader);
            case "posindex" -> PositionalIndex.readFromFile(bufferedReader);
            default -> throw new RuntimeException("Unknown dictionary type");
        };
    }

}