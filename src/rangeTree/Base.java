package rangeTree;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.*;

class FastWriter{
    BufferedWriter out;

    public FastWriter(){
        try{
            out = new BufferedWriter(new OutputStreamWriter(System.out), 512);
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    public void print(String str){
        try {
            out.write(str);
        } catch (Exception e){
        }
    }

    public void println(String str){
        try {
            out.write(str+"\n");
        } catch (Exception e){
        }
    }

    public void flush(){
        try {
            out.flush();
        } catch (Exception e){
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // read input and build range tree
        int n = scanner.nextInt();
        double[][] inputs = new double[2][n];
        Point[] points = new Point[n];

        for (int x=0; x<n; x++){
            inputs[0][x] = scanner.nextDouble();
        } for (int y=0; y<n; y++){
            inputs[1][y] = scanner.nextDouble();
        } for (int i=0; i<n; i++){
            points[i] = new Point(inputs[0][i], inputs[1][i]);
        }
        RangeTree list = new RangeTree(points);

        // read input queries and print results
        n = scanner.nextInt();
        FastWriter out = new FastWriter();
        for (int i=0; i<n; i++){
            Point[] pointsInRange = list.queryRange(new Point(scanner.nextDouble(), scanner.nextDouble()),
                    new Point(scanner.nextDouble(), scanner.nextDouble()));
            if (pointsInRange.length==0){
                out.println("None");
            } else{
                for (Point p : pointsInRange){
                    out.print(p.x+" ");
                }
                out.print("\n");
                for (Point p : pointsInRange){
                    out.print(p.y+" ");
                }
                out.print("\n");
            }
            out.flush();
        }
    }
}

class RangeTree{
    private final Xnode root;

    public RangeTree(Point[] points){
        root = Xnode.makeTree(points);
    }

    public Point[] queryRange(Point ll, Point ur){
        Ynode[] ytrees = root.queryRange(ll.x, ur.x);
        Point[][] points = new Point[ytrees.length][];
        for (int i=0; i< ytrees.length; i++){
            points[i] = ytrees[i].queryRange(ll.y, ur.y);
        }
        //Point[][] points = Arrays.stream(root.queryRange(ll.x, ur.x)).map(y -> y.queryRange(ll.y, ur.y)).toArray(Point[][]::new);
        return Util.merge(points);
    }
}

class Xnode{
    private Ynode auxTree;
    private final Xnode left;
    private final Xnode right;
    private final double val;
    private final Point point;

    public static Xnode makeTree(Point[] points){
        points = points.clone();
        Arrays.sort(points, new Xcomparator());

        Stack<Xnode> back = new Stack<>();
        Stack<Xnode> front = new Stack<>();
        for (Point p : points){
            back.push(new Xnode(p));
        }
        while (back.size()>1 || front.size()>0){
            while (!back.isEmpty()){
                Xnode right = back.pop();
                if (back.isEmpty()){
                    front.push(right);
                } else{
                    Xnode left = back.pop();
                    front.push(new Xnode(left, right));
                }
            }
            while (!front.isEmpty()){
                Xnode left = front.pop();
                if (front.isEmpty()){
                    back.push(left);
                } else{
                    back.push(new Xnode(left, front.pop()));
                }
            }
        }
        return back.pop();
    }

    public Xnode(Point point){
        this.point = point;
        this.val = point.x;
        auxTree = new Ynode(point);
        left = null;
        right = null;
    }

    public Xnode(Xnode left, Xnode right){
        this.left = left;
        this.right = right;
        this.val = left.getMax();
        point = null;
    }

    private double getMax(){
        Xnode current = this;
        while (!current.isLeaf()) {
            current = current.right;
        }
        return current.getPoint().x;
    }

    private boolean isLeaf(){
        return point!=null;
    }

    private void verifyLeaf(){
        if (!isLeaf()){
            throw new RuntimeException("Illegal operation on non-leaf node.");
        }
    }

    public Point getPoint() {
        verifyLeaf();
        return point;
    }

    public Ynode getAuxTree(){
        if (auxTree==null){
            auxTree = Ynode.merge(left.getAuxTree(), right.getAuxTree());
        }
        return auxTree;
    }

    public Ynode[] queryRange(double l, double u) {
        Xnode commonParent = getCommonParent(l, u);
        // if <= 1 point lies in given range
        if (commonParent.isLeaf()){
            if (commonParent.val>=l && commonParent.val<=u){
                Ynode[] outp = new Ynode[1];
                outp[0] = commonParent.getAuxTree();
                return outp;
            } else{
                return new Ynode[0];
            }
        }
        // if >1 point lies in given range
        Ynode[] left = commonParent.left.getLargerThan(l);
        Ynode[] right = commonParent.right.getSmallerThan(u);
        Ynode[] outp = new Ynode[left.length+right.length];
        System.arraycopy(left, 0, outp, 0, left.length);
        System.arraycopy(right, 0, outp, left.length, right.length);
        return outp;
    }

    private Xnode getCommonParent(double l, double u){
        Xnode commonParent = this;
        while (!commonParent.isLeaf()){
            boolean compl = commonParent.val < l;
            boolean compu = commonParent.val <= u;
            if (compl && compu && commonParent.right!=null){
                commonParent = commonParent.right;
            } else if (!compl && !compu && commonParent.left!=null){
                commonParent = commonParent.left;
            } else {
                return commonParent;
            }
        }
        return commonParent;
    }

    private Ynode[] getLargerThan(double l){
        Stack<Ynode> ytrees = new Stack<>();
        Xnode current = this;
        while (current!=null && !current.isLeaf()){
            if (l<=current.val){
                ytrees.add(current.right.getAuxTree());
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (current.isLeaf() && current.val>=l){
            ytrees.add(current.getAuxTree());
        }
        Ynode[] outp = new Ynode[ytrees.size()];
        for (int i=0; i<outp.length; i++){
            outp[i] = ytrees.pop();
        }
        return outp;
    }

    private Ynode[] getSmallerThan(double u){
        Xnode current = this;
        ArrayList<Ynode> output = new ArrayList<>();
        while (!current.isLeaf()){
            if (u >= current.val){
                output.add(current.left.getAuxTree());
                current = current.right;
            } else {
                current = current.left;
            }
        }
        if (current.isLeaf() && current.val<=u){
            output.add(current.getAuxTree());
        }
        return output.toArray(new Ynode[output.size()]);
    }
}

class Ynode{
    private final Ynode left;
    private final Ynode right;
    private final double val;
    private final Point point;

    // merge two balanced bts's into one
    public static Ynode merge(Ynode left, Ynode right){
        Point[] leftPoints = left.getAllPoints();
        Point[] rightPoints = right.getAllPoints();
        Point[] merged = Util.merge(rightPoints, leftPoints);
        return makeTree(merged);
    }

    // make balanced bst from sorted array of points
    public static Ynode makeTree(Point[] points){
        Stack<Ynode> back = new Stack<>();
        Stack<Ynode> front = new Stack<>();
        for (Point p : points){
            back.push(new Ynode(p));
        }
        while (back.size()>1 || front.size()>0){
            while (!back.isEmpty()){
                Ynode right = back.pop();
                if (back.isEmpty()){
                    front.push(right);
                } else{
                    Ynode left = back.pop();
                    front.push(new Ynode(left, right));
                }
            }
            while (!front.isEmpty()){
                Ynode left = front.pop();
                if (front.isEmpty()){
                    back.push(left);
                } else{
                    back.push(new Ynode(left, front.pop()));
                }
            }
        }
        return back.pop();
    }

    // leaf constructor
    public Ynode(Point point){
        this.point = point;
        this.val = point.y;
        left = null;
        right = null;
    }

    // non-leaf node constructor
    public Ynode(Ynode left, Ynode right){
        this.left = left;
        this.right = right;
        this.val = left.getMax();
        point = null;
    }

    public Point getPoint() {
        verifyLeaf();
        return point;
    }

    // get all points stored in (sub) tree in order
    public Point[] getAllPoints(){
        Queue<Ynode> nodes = new LinkedList<>();
        List<Point> points = new ArrayList<>();
        nodes.add(this);
        while (!nodes.isEmpty()){
            Ynode node = nodes.poll();
            if (node.isLeaf()){
                points.add(node.getPoint());
            } else {
                nodes.add(node.left);
                nodes.add(node.right);
            }
        }
        return points.toArray(new Point[points.size()]);
    }

    // get all points with y value in given range (inclusive)
    public Point[] queryRange(double l, double u) {
        Ynode commonParent = getCommonParent(l, u);
        // if <=1 poins lie between l and u
        if (commonParent.isLeaf()){
            if (commonParent.val>=l && commonParent.val<=u){
                Point[] outp = new Point[1];
                outp[0] = commonParent.getPoint();
                return outp;
            } else{
                return new Point[0];
            }
        }
        // if >1 points lie between l and u
        Point[] left = commonParent.left.getLargerThan(l);
        Point[] right = commonParent.right.getSmallerThan(u);
        Point[] outp = new Point[left.length+right.length];
        System.arraycopy(left, 0, outp, 0, left.length);
        System.arraycopy(right, 0, outp, left.length, right.length);
        return outp;
    }

    // get the highest common parent where the paths for searching for l and u split
    private Ynode getCommonParent(double l, double u){
        Ynode commonParent = this;
        while (!commonParent.isLeaf()){
            boolean compl = commonParent.val < l;
            boolean compu = commonParent.val <= u;
            if (compl && compu && commonParent.right!=null){
                commonParent = commonParent.right;
            } else if (!compl && !compu && commonParent.left!=null){
                commonParent = commonParent.left;
            } else {
                return commonParent;
            }
        }
        return commonParent;
    }

    private Point[] getLargerThan(double l){
        Stack<Ynode> trees = new Stack<>();
        Ynode current = this;
        while (current!=null && !current.isLeaf()) {
            if (l <= current.val) {
                trees.add(current.right);
                current = current.left;
            } else {
                current = current.right;
            }
        }
        ArrayList<Point> points = new ArrayList<>();
        if (current.isLeaf() && current.val>=l){
            points.add(current.getPoint());
        }
        while (!trees.isEmpty()){
            Point[] temp = trees.pop().getAllPoints();
            for (Point p : temp){
                points.add(p);
            }
        }
        return points.toArray(new Point[points.size()]);
    }

    private Point[] getSmallerThan(double u){
        Ynode current = this;
        ArrayList<Point> output = new ArrayList<>();
        while (current!=null && !current.isLeaf()){
            if (u >= current.val){
                for (Point p : current.left.getAllPoints()) {
                    output.add(p);
                }
                current = current.right;
            } else{
                current = current.left;
            }
        }
        if (current.isLeaf() && current.val<=u){
            output.add(current.getPoint());
        }
        return output.toArray(new Point[output.size()]);
    }

    private double getMax(){
        Ynode current = this;
        while (!current.isLeaf()){
            current = current.right;
        }
        return current.getPoint().y;
    }

    private boolean isLeaf(){
        return point!=null;
    }

    private void verifyLeaf(){
        if (!isLeaf()){
            throw new RuntimeException("Illegal operation on non-leaf node.");
        }
    }
}

class Point {
    public final double x;
    public final double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return String.format("(%f,%f)",x,y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)){
            return false;
        }
        return x==((Point)obj).x && y==((Point)obj).y;
    }
}

class Xcomparator implements Comparator<Point>{
    @Override
    public int compare(Point o1, Point o2) {
        int comp = Double.compare(o1.x, o2.x);
        if (comp==0){
            return Double.compare(o1.y, o2.y);
        }
        return comp;
    }
}

class Ycomparator implements Comparator<Point>{
    @Override
    public int compare(Point o1, Point o2) {
        int comp = Double.compare(o1.y, o2.y);
        if (comp==0){
            return Double.compare(o1.x, o2.x);
        }
        return comp;
    }
}

class Util {
    public static Point[] merge(Point[] a, Point[] b){
        int i=0, j=0, k=0;
        Point[] merged = new Point[a.length+b.length];
        Comparator<Point> comp = new Ycomparator();
        while (i<a.length && j< b.length){
            if (comp.compare(a[i], b[j])<0){
                merged[k] = a[i];
                k++;
                i++;
            } else{
                merged[k] = b[j];
                k++;
                j++;
            }
        }
        if (i<a.length){
            System.arraycopy(a, i, merged, k, a.length-i);
        }
        if (j<b.length){
            System.arraycopy(b, j, merged, k, b.length-j);
        }
        return merged;
    }

    public static Point[] merge(Point[]... points){
        if (points.length==0){
            return new Point[0];
        }
        Queue<Point[]> queue = new LinkedList<>();
        for (Point[] p : points){
            queue.add(p);
        }
        while (queue.size()>1){
            queue.add(merge(queue.poll(), queue.poll()));
        }
        return queue.poll();
    }
}


