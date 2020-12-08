package skipList;

public class SkipList<T> implements List<T>{
    private Node<T> head;
    private int size;

    public SkipList(){
        size = 0;
    }

    @Override
    public void insert(T data) {
        if (size == 0) {
            head = new Node<>(data);
            size++;
        }
        // Todo main insertion logic
    }

    @Override
    public boolean remove(T data) {
        return false;
    }

    @Override
    public boolean search(T data) {
        return false;
    }

    @Override
    public void print() {
        if (size == 0){
            System.out.println("empty");
            return;
        }
        Node<T> node=head.downMost();
        for (;node.right != null; node = node.right){
            System.out.print(node.get()+" ");
        }
        System.out.println(node.get().toString());
    }
}
