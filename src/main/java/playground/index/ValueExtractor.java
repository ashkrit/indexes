package playground.index;


public interface ValueExtractor<D> {

    String columnName();

    Object extract(D data);
}
