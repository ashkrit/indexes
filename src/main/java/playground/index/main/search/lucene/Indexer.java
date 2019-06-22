package playground.index.main.search.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import playground.index.time.ApplicationTimer;

import java.io.*;
import java.nio.file.Paths;
import java.util.function.Predicate;

public class Indexer implements Closeable {

    private final FSDirectory dir;
    private final IndexWriter writer;

    public Indexer(String indexLocation) {
        try {
            this.dir = FSDirectory.open(Paths.get(indexLocation));
            this.writer = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() {
        try {
            this.writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void println(String line) {
        System.out.println(line);
    }

    public void index(String dataFolder, Predicate<File> filterFilter) {

        ApplicationTimer.time(String.format("Index %s", dataFolder), () -> {
            println(String.format("Start Indexing of %s", dataFolder));
            File[] files = new File(dataFolder).listFiles(file -> filterFilter.test(file));
            for (File file : files) {
                if (file.isFile()) {
                    println(String.format("Indexing %s", file));
                    Document doc = createDocument(file);
                    insertDocument(doc);
                    println(String.format("Document %s indexed", file));
                }
            }
            return null;
        });


    }

    private void insertDocument(Document doc) {
        try {
            this.writer.addDocument(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document createDocument(File file) {
        try {
            Document document = new Document();
            document.add(new TextField("content", new FileReader(file)));
            document.add(new StringField("filename", file.getName(), Field.Store.YES));
            document.add(new StringField("filepath", file.getAbsolutePath(), Field.Store.YES));
            return document;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
