package playground.index;


public interface RowCallback<T> {
    void onRow(int rowId, T row);

    void onComplete(int recordCount);
}
