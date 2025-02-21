import dictionary.Dictionary;

public class Main {

    public static void main(String[] args) {
        // Dictionary dictionary = Dictionary.loadSerializedDictionary("src/dictionary/saved/");
        // Dictionary dictionary = Dictionary.loadDictionaryFromFile("src/dictionary/saved/");

        Dictionary dictionary = new Dictionary();
        dictionary.addFilesFromFolder("src/collection");
        //dictionary.addFile("src/collection/cranford.txt");
//        if (dictionary == null) return;

        for (String document : dictionary.documentsWithTerm("negative"))
            System.out.println(document);

        System.out.println();

        for (String document : dictionary.documentsWithTerm("grain"))
            System.out.println(document);

        System.out.println();

        for (String document : dictionary.documentsWithTerm("treasure"))
            System.out.println(document);

        System.out.println();

        for (String document : dictionary.documentsFromQuery("negative | grain & treasure"))
            System.out.println(document);

        System.out.println();

//        for (String term : dictionary.getTerms()) {
//           System.out.println(term + ": " + dictionary.getTermCount(term));
//        }
//
//        System.out.println(dictionary.getTermPostings("depression"));
//
//        System.out.println(dictionary.getAllTermsCount());
//        System.out.println(dictionary.getUniqueTermsCount());
//        dictionary.serialize("src/dictionary/saved/");
//        dictionary.writeInFile("src/dictionary/saved/");
//        System.out.println(dictionary.getLastDictionarySize());
    }

}