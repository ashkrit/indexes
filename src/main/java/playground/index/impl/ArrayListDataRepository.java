package playground.index.impl;


import playground.index.api.DataIndex;
import playground.index.api.DataRepository;
import playground.index.api.RowCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArrayListDataRepository<T> implements DataRepository<T> {

    private List<T> data = new ArrayList<>();
    private int rowCount = 0;
    private List<DataIndex<T>> indexes;

    public ArrayListDataRepository(List<DataIndex<T>> indexes) {
        this.indexes = indexes;
    }

    @Override
    public void insert(T row) {
        data.add(row);
        indexes.forEach(dataIndex -> dataIndex.add(rowCount, row));
        rowCount++;
    }

    @Override
    public void query(String indexName, Object searchKey, RowCallback<T> rowCallback) {

        Optional<DataIndex<T>> indexObject = indexes.stream().filter(index -> index.indexName().equals(indexName)).findFirst();
        checkIndex(indexName, indexObject);
        indexObject.get().search(this, searchKey, rowCallback);
    }

    private void checkIndex(String indexName, Optional<DataIndex<T>> indexObject) {
        if (!indexObject.isPresent()) {
            throw new IllegalArgumentException(String.format("Unable to search index %s", indexName));
        }
    }

    @Override
    public T rowId(Integer rowId) {
        return data.get(rowId);
    }
}
