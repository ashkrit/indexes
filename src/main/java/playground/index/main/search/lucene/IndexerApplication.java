package playground.index.main.search.lucene;

public class IndexerApplication {


    public static void main(String... args) {

        if (args.length != 2) {
            throw new IllegalArgumentException(String.format("Usage java %s <indexfolder> <datafolder>", IndexerApplication.class.getName()));
        }

        String indexFolder = args[0];
        String dataFolder = args[1];

        try (Indexer i = new Indexer(indexFolder)) {
            i.index(dataFolder, f -> f.getName().endsWith("txt"));
        }

    }


}
