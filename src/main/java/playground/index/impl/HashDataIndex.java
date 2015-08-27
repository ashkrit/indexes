package playground.index.impl;


import gnu.trove.list.array.TIntArrayList;
import playground.index.DataIndex;
import playground.index.DataRepository;
import playground.index.RowCallback;
import playground.index.ValueExtractor;

import java.util.HashMap;
import java.util.Map;

public class HashDataIndex<T> implements DataIndex<T> {

    private final ValueExtractor<T> extractor;
    private final Map<Object, TIntArrayList> indexValues = new HashMap<>();

    public HashDataIndex(ValueExtractor<T> extractor) {
        this.extractor = extractor;
    }

    @Override
    public String indexName() {
        return extractor.columnName();
    }

    @Override
    public void add(int rowNumber, T row) {
        Object key = extractor.extract(row);
        TIntArrayList rowIds = indexValues.get(key);
        if (rowIds == null) {
            rowIds = new TIntArrayList();
            indexValues.put(key, rowIds);
        }
        rowIds.add(rowNumber);
    }

    @Override
    public void search(DataRepository<T> repository, Object key, RowCallback<T> rowCallback) {

        TIntArrayList rowIds = indexValues.get(key);

        if (rowIds != null) {
            rowIds.forEach(value -> {
                rowCallback.onRow(value, repository.rowId(value));
                return true;
            });

        }
        int size = rowIds == null ? 0 : rowIds.size();
        rowCallback.onComplete(size);

    }
}
