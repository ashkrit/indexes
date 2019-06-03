package playground.index.main.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchEngine {

    public static final String FIELD_CONTENTS = "contents";

    public static void main(String... args) throws Exception {

        String indexLocation = args[0];
        String spellCheckerLocation = args[1];

        IndexReader reader = createIndexReader(indexLocation);
        SpellChecker spellChecker = createSpellChecker(spellCheckerLocation, reader);

        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(FIELD_CONTENTS, new StandardAnalyzer());

        String line;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Search term ?");
        while ((line = scanner.nextLine()) != null) {

            Query query = parser.parse(line);
            TopDocs result = searcher.search(query, 10);
            System.out.println(String.format("Found %s docs", result.totalHits));

            for (ScoreDoc doc : result.scoreDocs) {
                Document content = searcher.doc(doc.doc);
                System.out.println(String.format("%s -> %s -> %s", doc.score, content.get("id"), content.get("desc")));
            }

            List<StringBuffer> suggestTerms = findSuggestedTerms(spellChecker, line);
            rankSearchTerms(searcher, parser, suggestTerms);

            System.out.println("Search term ?");
        }

    }

    private static void rankSearchTerms(IndexSearcher searcher, QueryParser parser, List<StringBuffer> suggestTerms) throws ParseException, IOException {
        TopDocs topResult = null;
        String topTerm = null;

        for (StringBuffer suggestTopic : suggestTerms) {
            Query q = parser.parse(suggestTopic.toString());
            TopDocs r = searcher.search(q, 1);
            if (r.totalHits.value > 0) {
                if (topResult == null || topResult.scoreDocs[0].score < r.scoreDocs[0].score) {
                    topResult = r;
                    topTerm = suggestTopic.toString();
                }

            }
        }

        System.out.println(String.format("Did you mean ` %s `  -> %s", topTerm, topResult.scoreDocs[0].score));
    }

    private static List<StringBuffer> findSuggestedTerms(SpellChecker spellChecker, String line) throws IOException {
        List<StringBuffer> suggestTerms = new ArrayList<>();
        for (String word : line.split(" ")) {
            String[] suggestion = spellChecker.suggestSimilar(word, 2);
            int index = 0;
            for (String s : suggestion) {
                if (suggestTerms.size() <= index) {
                    suggestTerms.add(new StringBuffer(s).append(" "));
                } else {
                    suggestTerms.set(index, suggestTerms.get(index).append(s).append(" "));
                }
                index++;
            }
        }
        return suggestTerms;
    }

    private static IndexReader createIndexReader(String indexLocation) throws IOException {
        Directory dataIndexDirectory = FSDirectory.open(Paths.get(indexLocation));
        return DirectoryReader.open(dataIndexDirectory);
    }

    private static SpellChecker createSpellChecker(String spellCheckerLocation, IndexReader reader) throws IOException {
        Dictionary dict = new LuceneDictionary(reader, FIELD_CONTENTS);
        Directory spellCheckerDirectory = FSDirectory.open(Paths.get(spellCheckerLocation));
        SpellChecker spellChecker = new SpellChecker(spellCheckerDirectory);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        spellChecker.indexDictionary(dict, config, true);
        return spellChecker;
    }
}
