package skipList;

public interface List<T> {
    int size();
    void insert(T data);
    boolean remove(T data);
    boolean search(T data);
}
