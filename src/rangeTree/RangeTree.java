package rangeTree;

public interface RangeTree<X extends Comparable<? super X>, Y extends Comparable<? super Y>> {
    Pair<X, Y>[] queryRange(X lowerx, Y lowery, X upperx, Y uppery);
}
