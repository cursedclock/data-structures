package skipList;

public class SkipList<T> implements List<T>{
    private Node<T> head;
    private int size;

    public SkipList(){
        size = 0;
    }

    @Override
    public void insert(T data) {

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

    }
}
