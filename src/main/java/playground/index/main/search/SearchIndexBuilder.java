package playground.index.main.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class SearchIndexBuilder {

    public static void main(String... args) throws Exception {
        String dataLocation = args[0];
        String indexLocation = args[1];

        IndexWriter indexWriter = createIndexWriter(indexLocation);

        long start = System.currentTimeMillis();
        AtomicLong record = new AtomicLong(0);
        Files.lines(Paths.get(dataLocation))
                .map(line -> line.split("\t"))
                .forEach(row -> {
                    try {
                        Document doc = new Document();
                        doc.add(new StringField("id", row[0], Field.Store.YES));
                        doc.add(new TextField("contents", row[1], Field.Store.NO));
                        doc.add(new TextField("desc", row[1], Field.Store.YES));
                        System.out.println("Indexing " + record.incrementAndGet() + "\t" + row[0]);
                        indexWriter.addDocument(doc);

                    } catch (Exception e) {
                        System.out.println("Missed " + Arrays.toString(row));
                        e.printStackTrace();
                    }
                });

        indexWriter.commit();
        indexWriter.close();

        long total = System.currentTimeMillis() - start;

        System.out.println(String.format("Time taken for records %s is %s ms", record.get(), total));
    }

    private static IndexWriter createIndexWriter(String indexLocation) throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexLocation));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(indexDirectory, config);
        indexWriter.deleteAll();
        return indexWriter;
    }
}
