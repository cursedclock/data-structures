package skipList;

public class Node<T> {

    private final T data;
    public Node<T> up;
    public Node<T> down;
    public Node<T> left;
    public Node<T> right;

    public Node(T data){
        this.data = data;
    }

    public Node(T data, Node<T> up, Node<T> down, Node<T> left, Node<T> right){
        this(data);
        setConnections(up, down, left, right);
    }

    public void setConnections(Node<T> up, Node<T> down, Node<T> left, Node<T> right){
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }

    public T get(){
        return data;
    }
}
