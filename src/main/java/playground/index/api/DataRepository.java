package playground.index.api;


public interface DataRepository<T> {

    void insert(T row);

    void query(String indexName, Object searchKey, RowCallback<T> rowCallback);

    T rowId(Integer rowId);
}
