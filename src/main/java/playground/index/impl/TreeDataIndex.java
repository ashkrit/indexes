package playground.index.impl;


import gnu.trove.list.array.TIntArrayList;
import playground.index.ArrayListDataRepository;
import playground.index.DataIndex;
import playground.index.RowCallback;
import playground.index.ValueExtractor;

import java.util.Map;
import java.util.TreeMap;

public class TreeDataIndex<T> implements DataIndex<T> {

    private final ValueExtractor<T> extractor;
    private final TreeMap<Comparable, TIntArrayList> indexData = new TreeMap<>();

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
        TIntArrayList rowIds = addIfAbsent(key);
        rowIds.add(rowNumber);
    }

    private TIntArrayList addIfAbsent(Comparable key) {
        TIntArrayList rowIds = indexData.get(key);
        if (rowIds == null) {
            rowIds = new TIntArrayList();
            indexData.put(key, rowIds);
        }
        return rowIds;
    }

    @Override
    public void search(ArrayListDataRepository<T> repository, Object key, RowCallback<T> rowCallback) {
        Range range = (Range) key;
        Map<Comparable, TIntArrayList> keys = search(range);
        int rowCount = forEachRow(repository, rowCallback, keys);
        rowCallback.onComplete(rowCount);


    }

    private Map<Comparable, TIntArrayList> search(Range range) {
        Map<Comparable, TIntArrayList> keys;
        if (gt(range)) {
            keys = indexData.tailMap(range.start);
        } else if (lt(range)) {
            keys = indexData.headMap(range.end);
        } else {
            keys = indexData.tailMap(range.start).headMap(range.end);
        }
        return keys;
    }

    private int forEachRow(ArrayListDataRepository<T> repository, RowCallback<T> rowCallback, Map<Comparable, TIntArrayList> gtValues) {
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
