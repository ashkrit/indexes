package playground.index.main.search.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Searcher {

    private final IndexSearcher indexSearcher;

    public Searcher(String indexLocation) {
        try {
            FSDirectory dir = FSDirectory.open(Paths.get(indexLocation));
            DirectoryReader indexReader = DirectoryReader.open(dir);
            this.indexSearcher = new IndexSearcher(indexReader);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Document byId(int doc) {
        try {
            return this.indexSearcher.doc(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TopDocs search(String q, int top) {

        try {
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query query = parser.parse(q);

            return this.indexSearcher.search(query, top);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
