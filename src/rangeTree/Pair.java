package rangeTree;

import java.util.Comparator;

public class Pair<X, Y> {
    public final X x;
    public final Y y;
    public Pair(X x, Y y){
        this.x = x;
        this.y = y;
    }
}

class PairComparator<X extends Comparable<? super X>, Y extends Comparable<? super Y>>
        implements Comparator<Pair<X, Y>>{
    @Override
    public int compare(Pair<X, Y> o1, Pair<X,Y> o2) {
        int comp = o1.x.compareTo(o2.x);
        if (comp==0){
            return o1.y.compareTo(o2.y);
        }
        return comp;
    }
}