package playground.index.impl;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;
import com.googlecode.concurrenttrees.suffix.SuffixTree;
import gnu.trove.list.array.TIntArrayList;
import playground.index.DataIndex;
import playground.index.DataRepository;
import playground.index.RowCallback;
import playground.index.ValueExtractor;


public class ContainsDataIndex<T> implements DataIndex<T> {

    private final ValueExtractor<T> extractor;
    private final SuffixTree<TIntArrayList> indexValues = new ConcurrentSuffixTree<>(new DefaultCharArrayNodeFactory());

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
        TIntArrayList rowIds = indexValues.getValueForExactKey(key);
        if (rowIds == null) {
            rowIds = new TIntArrayList();
            indexValues.put(key, rowIds);
        }
        rowIds.add(rowNumber);
    }

    @Override
    public void search(DataRepository<T> repository, Object key, RowCallback<T> rowCallback) {

        Iterable<CharSequence> matchedKeys = indexValues.getKeysContaining((String) key);
        int rowCount = 0;
        for (CharSequence k : matchedKeys) {
            TIntArrayList rowIds = indexValues.getValueForExactKey(k);
            rowIds.forEach(rowId -> {
                rowCallback.onRow(rowId, repository.rowId(rowId));
                return true;
            });
            rowCount += rowIds.size();
        }
        rowCallback.onComplete(rowCount);
    }
}
