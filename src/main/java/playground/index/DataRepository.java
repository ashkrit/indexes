package playground.index;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataRepository<T> {

    private List<T> data = new ArrayList<>();
    private int rowCount = 0;
    private List<DataIndex<T>> indexes;

    public DataRepository(List<DataIndex<T>> indexes) {
        this.indexes = indexes;
    }

    public void insert(T row) {
        data.add(row);
        indexes.forEach(dataIndex -> dataIndex.add(rowCount, row));
        rowCount++;
    }

    public void query(String indexName, Object value, RowCallback<T> rowCallback) {

        Optional<DataIndex<T>> indexObject = indexes.stream().filter(index -> index.indexName().equals(indexName)).findFirst();
        if (!indexObject.isPresent()) {
            throw new IllegalArgumentException(String.format("Unable to search index %s", indexName));
        }

        indexObject.get().search(this, value, rowCallback);
    }

    public T rowId(Integer rowId) {
        return data.get(rowId);
    }
}
