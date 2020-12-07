package skipList;

public interface List<T> {
    void insert(T data);
    boolean remove(T data);
    boolean search(T data);
    void print();
}
