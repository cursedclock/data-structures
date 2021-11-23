package rangeTree;

import java.util.*;

public class Ytree <X extends Comparable<? super X>, Y extends Comparable<? super Y>>{
    private final Ytree<X, Y> left;
    private final Ytree<X, Y> right;
    private final Y val;
    private final Pair<X, Y> pair;

    public static <X extends Comparable<? super X>, Y extends Comparable<? super Y>>
    Ytree<X, Y> makeTree(List<Pair<X, Y>> pairs){
        Stack<Ytree<X, Y>> back= new Stack<>();
        Stack<Ytree<X, Y>> front = new Stack<>();
        for (Pair<X, Y> p : pairs){
            back.push(new Ytree(p));
        }
        while (back.size()!=1 || front.size()!=0){
            while (!back.isEmpty()){
                Ytree<X, Y> right = back.pop();
                if (back.isEmpty()){
                    front.push(right);
                } else{
                    Ytree<X, Y> left = back.pop();
                    front.push(new Ytree<X, Y>(left, right));
                }
            }
            while (!front.isEmpty()){
                Ytree<X, Y> left = front.pop();
                if (front.isEmpty()){
                    back.push(left);
                } else{
                    back.push(new Ytree<X, Y>(left, front.pop()));
                }
            }
        }
        return back.pop();
    }

    public Ytree(Ytree<X, Y> left, Ytree<X, Y> right){
        this.left = left;
        this.right = right;
        this.val = left.getMax();
        this.pair = null;
    }

    public Ytree(Pair<X, Y> pair){
        this.pair = pair;
        this.val = pair.y;
        left = null;
        right = null;
    }

    public List<Pair<X, Y>> getAll(){
        Queue<Ytree> nodes = new LinkedList<>();
        List<Pair<X, Y>> pairs = new ArrayList<>();
        nodes.add(this);
        while (!nodes.isEmpty()){
            Ytree node = nodes.poll();
            if (node.isLeaf()){
                pairs.add(node.getPair());
            } else {
                nodes.add(node.left);
                nodes.add(node.right);
            }
        }
        return pairs;
    }

    private Pair<X, Y> getPair(){
        verifyLeaf();
        return pair;
    }

    private Y getMax(){
        Ytree<X, Y> current = this;
        while (!current.isLeaf()){
            current = current.right;
        }
        return current.val;
    }

    private boolean isLeaf(){
        return pair==null;
    }

    private void verifyLeaf(){
        if (!isLeaf()){
            throw new RuntimeException("Illegal operation on non-leaf node.");
        }
    }
}
