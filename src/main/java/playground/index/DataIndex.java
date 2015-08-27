package playground.index;


public interface DataIndex<T> {
    String indexName();
    void add(int rowNumber, T row);

    void search(ArrayListDataRepository<T> repository, Object key, RowCallback<T> rowCallback);
}
