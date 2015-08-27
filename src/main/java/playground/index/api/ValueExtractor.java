package playground.index.api;


public interface ValueExtractor<D> {

    String columnName();
    Object extract(D data);
}
