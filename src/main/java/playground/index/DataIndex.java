package playground.index;


public interface DataIndex<T> {
    String indexName();

    void add(int rowNumber, T row);

    void search(DataRepository<T> repository, Object key, RowCallback<T> rowCallback);
}
