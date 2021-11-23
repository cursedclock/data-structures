package rangeTree;

import java.util.*;

public class Util {
    public static<X extends Comparable<? super X>, Y extends Comparable<? super Y>>
    List<Pair<X, Y>> merge(List<Pair<X, Y>> l1,List<Pair<X, Y>> l2){
        List<Pair<X, Y>> outp = new ArrayList<>(l1.size()+l2.size());
        Iterator<Pair<X, Y>> iter1 = l1.iterator();
        Iterator<Pair<X, Y>> iter2 = l2.iterator();
        while (iter1.hasNext() && iter2.hasNext()){
            // TODO merge two lists
        }
    }
}
