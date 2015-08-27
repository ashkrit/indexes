package playground.index.impl;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;
import com.googlecode.concurrenttrees.suffix.SuffixTree;
import gnu.trove.list.array.TIntArrayList;
import playground.index.ArrayListDataRepository;
import playground.index.DataIndex;
import playground.index.RowCallback;
import playground.index.ValueExtractor;


public class ContainsDataIndex<T> implements DataIndex<T> {

    private final ValueExtractor<T> extractor;
    private final SuffixTree<TIntArrayList> indexData = new ConcurrentSuffixTree<>(new DefaultCharArrayNodeFactory());

    public ContainsDataIndex(ValueExtractor<T> extractor) {
        this.extractor = extractor;
    }

    @Override
    public String indexName() {
        return extractor.columnName();
    }

    @Override
    public void add(int rowNumber, T row) {
        String key = (String) extractor.extract(row);
        TIntArrayList rowIds = addIfAbsent(key);
        rowIds.add(rowNumber);
    }

    private TIntArrayList addIfAbsent(String key) {
        TIntArrayList rowIds = indexData.getValueForExactKey(key);
        if (rowIds == null) {
            rowIds = new TIntArrayList();
            indexData.put(key, rowIds);
        }
        return rowIds;
    }

    @Override
    public void search(ArrayListDataRepository<T> repository, Object key, RowCallback<T> rowCallback) {

        Iterable<CharSequence> matchedKeys = indexData.getKeysContaining((String) key);
        int rowCount = 0;
        for (CharSequence k : matchedKeys) {

            TIntArrayList rowIds = indexData.getValueForExactKey(k);
            rowIds.forEach(rowId -> {
                rowCallback.onRow(rowId, repository.rowId(rowId));
                return true;
            });

            rowCount += rowIds.size();
        }
        rowCallback.onComplete(rowCount);
    }
}
