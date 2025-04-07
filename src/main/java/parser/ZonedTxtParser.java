package parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ZonedTxtParser {

    private final List<String> title = new ArrayList<>();
    private final List<String> author = new ArrayList<>();
    private final List<String> body = new ArrayList<>();

    public ZonedTxtParser(File file) {
        try { parse(file); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public List<String> getTitle() {
        return title;
    }

    public List<String> getAuthor() {
        return author;
    }

    public List<String> getBody() {
        return body;
    }

    public int size() {
        return title.size() + author.size() + body.size();
    }

    private void parse(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        parseTitle(reader);
        parseAuthor(reader);
        parseBody(reader);
    }

    private void parseTitle(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            List<String> tokens = tokenizeLine(line);
            if (titleFound(tokens)) {
                addTitleTerms(tokens);
                break;
            }
        }
    }

    private void parseAuthor(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            List<String> tokens = tokenizeLine(line);
            if (authorFound(tokens)) {
                addAuthorTerms(tokens);
                break;
            }
        }
    }

    private void parseBody(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            List<String> tokens = tokenizeLine(line);
            addBodyTerms(tokens);
        }
    }

    private List<String> tokenizeLine(String line) {
        Tokenizer tokenizer = new Tokenizer();
        return tokenizer.tokenize(line);
    }

    private boolean titleFound(List<String> tokens) {
        return !tokens.isEmpty() && tokens.get(0).equals("title");
    }

    private boolean authorFound(List<String> tokens) {
        return !tokens.isEmpty() && tokens.get(0).equals("author");
    }

    private void addTitleTerms(List<String> tokens) {
        tokens.remove(0);
        title.addAll(tokens);
    }

    private void addAuthorTerms(List<String> tokens) {
        tokens.remove(0);
        author.addAll(tokens);
    }

    private void addBodyTerms(List<String> tokens) {
        body.addAll(tokens);
    }

}