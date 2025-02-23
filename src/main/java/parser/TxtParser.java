package parser;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class TxtParser implements Parser {

    private List<String> words = new ArrayList<>();

    public TxtParser(File file) {
        try {
            parse(file);
        } catch (IOException e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.info(e.getMessage());
        }
    }

    @Override
    public List<String> getTerms() {
        return words;
    }

    private void parse(File file) throws IOException {
        List<String> lines = readLines(file);
        for (String line : lines)
            parseLine(line);
    }

    private List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null)
            lines.add(line);
        reader.close();
        return lines;
    }

    private void parseLine(String line) {
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenize(line);
        words.addAll(tokens);
    }

}