package playground.index.impl;


import gnu.trove.list.array.TIntArrayList;
import playground.index.DataIndex;
import playground.index.DataRepository;
import playground.index.RowCallback;
import playground.index.ValueExtractor;

import java.util.Map;
import java.util.TreeMap;

public class TreeDataIndex<T> implements DataIndex<T> {

    private final ValueExtractor<T> extractor;
    private final TreeMap<Comparable, TIntArrayList> indexValues = new TreeMap<>();

    public TreeDataIndex(ValueExtractor<T> extractor) {
        this.extractor = extractor;
    }


    @Override
    public String indexName() {
        return extractor.columnName();
    }

    @Override
    public void add(int rowNumber, T row) {
        Comparable key = (Comparable) extractor.extract(row);
        TIntArrayList rowIds = indexValues.get(key);
        if (rowIds == null) {
            rowIds = new TIntArrayList();
            indexValues.put(key, rowIds);
        }
        rowIds.add(rowNumber);
    }

    @Override
    public void search(DataRepository<T> repository, Object key, RowCallback<T> rowCallback) {
        Range range = (Range) key;
        Map<Comparable, TIntArrayList> keys;
        if (gt(range)) {
            keys = indexValues.tailMap(range.start);
        } else if (lt(range)) {
            keys = indexValues.headMap(range.end);
        } else {
            keys = indexValues.tailMap(range.start).headMap(range.end);
        }

        int rowCount = forEachRow(repository, rowCallback, keys);
        rowCallback.onComplete(rowCount);


    }

    private int forEachRow(DataRepository<T> repository, RowCallback<T> rowCallback, Map<Comparable, TIntArrayList> gtValues) {
        int rowCount = 0;
        for (TIntArrayList rowIds : gtValues.values()) {
            rowIds.forEach(rowId -> {
                rowCallback.onRow(rowId, repository.rowId(rowId));
                return true;
            });
            rowCount += rowIds.size();
        }
        return rowCount;
    }

    private boolean lt(Range range) {
        return range.start == null && range.end != null;
    }

    private boolean gt(Range range) {
        return range.start != null && range.end == null;
    }

    public static class Range {
        Comparable start;
        Comparable end;

        public Range(Comparable start, Comparable end) {
            this.start = start;
            this.end = end;
        }
    }
}
