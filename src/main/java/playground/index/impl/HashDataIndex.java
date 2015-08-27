package playground.index.impl;


import gnu.trove.list.array.TIntArrayList;
import playground.index.api.DataIndex;
import playground.index.api.RowCallback;
import playground.index.api.ValueExtractor;

import java.util.HashMap;
import java.util.Map;

public class HashDataIndex<T> implements DataIndex<T> {

    private final ValueExtractor<T> extractor;
    private final Map<Object, TIntArrayList> indexData = new HashMap<>();

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
        TIntArrayList rowIds = addIfAbsent(key);
        rowIds.add(rowNumber);
    }

    private TIntArrayList addIfAbsent(Object key) {
        TIntArrayList rowIds = indexData.get(key);
        if (rowIds == null) {
            rowIds = new TIntArrayList();
            indexData.put(key, rowIds);
        }
        return rowIds;
    }

    @Override
    public void search(ArrayListDataRepository<T> repository, Object key, RowCallback<T> rowCallback) {

        TIntArrayList rowIds = indexData.get(key);

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
