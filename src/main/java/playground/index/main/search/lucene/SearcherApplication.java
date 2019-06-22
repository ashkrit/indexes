package playground.index.main.search.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.util.Scanner;

public class SearcherApplication {

    public static void main(String... args) {

        if (args.length != 1) {
            throw new IllegalArgumentException(String.format("Usage java %s <indexfolder>", SearcherApplication.class.getName()));
        }

        String indexLocation = args[0];

        Searcher search = new Searcher(indexLocation);

        Scanner scanner = new Scanner(System.in);
        String line;

        System.out.println("Enter search term ?");
        while ((line = scanner.nextLine()) != null) {
            TopDocs docs = search.search(line, 10);
            System.out.println(String.format("Search returned %s", docs.totalHits));
            for (ScoreDoc matchedDocs : docs.scoreDocs) {
                Document doc = search.byId(matchedDocs.doc);
                System.out.println(String.format("Score %s file %s , path %s", matchedDocs.score, doc.get("filename"), doc.get("filepath")));
            }
            System.out.println();
            System.out.println("Enter search term ?");
        }

    }
}
