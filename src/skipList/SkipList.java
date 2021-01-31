package skipList;

import java.security.InvalidParameterException;
import  java.util.Random;

public class SkipList<T extends Comparable<T>> implements List<T>{
    private Node<T> head;
    private final int base;
    private int size;
    private int height;
    private int heightLimit;
    private Random random;

    public SkipList(int base, int heightLimit){
        if (heightLimit<0){
            throw new InvalidParameterException("Height limit cannot be smaller than 0.");
        }
        this.base = base;
        this.heightLimit = heightLimit;
        size = 0;
        height = 0;
        random = new Random();
        head = new Node<>(null);
        head.right = new Node<>(null, null, null, head, null);
    }

    public SkipList(){
        this(2, Integer.MAX_VALUE);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void insert(T data){
        verifyNotNull(data);
        int level = generateLevel();
        Node<T> newNode = buildPillar(data, level);
        adjustHeight(level);
        Node<T> currentNode = head;
        int currentHeight = height;
        while (currentHeight>level){
            if (isTail(currentNode.right) || data.compareTo(currentNode.right.get())<0){
                currentNode = currentNode.down;
                currentHeight--;
            } else {
                currentNode = currentNode.right;
            }
        }
        while (currentHeight>=0){
            if (isTail(currentNode.right) || data.compareTo(currentNode.right.get())<0){
                currentNode.right.left = newNode;
                newNode.right = currentNode.right;
                currentNode.right = newNode;
                newNode.left = currentNode;
                newNode = newNode.down;
                currentNode = currentNode.down;
                currentHeight--;
            } else {
                currentNode = currentNode.right;
            }
        }
        size++;
    }

    @Override
    public boolean remove(T data) {
        verifyNotNull(data);
        if (head.right==null){
            return false;
        }
        Node<T> toRemove = find(data);
        if (toRemove!=null){
            deleteValue(toRemove);
            return true;
        }
        return false;
    }

    @Override
    public boolean search(T data) {
        verifyNotNull(data);
        if (size == 0){
            return false;
        }
        return find(data)!=null;
    }

    @Override
    public String toString() {
        if (size == 0){
            return "empty";
        }
        int capacity = size*(3+head.right.toString().length());
        StringBuilder outp = new StringBuilder();
        Node<T> node=head.downMost().right;
        while (node.right.get() != null){
            outp.append(node.get());
            outp.append(" ");
            node = node.right;
        }
        outp.append(node.get().toString());
        return outp.toString();
    }

    private int generateLevel(){
        int level = 0;
        while (random.nextInt(base)==0 && level < heightLimit){
            level++;
        }
        return level;
    }

    private Node<T> find(T data){
        Node<T> currentNode = head;
        while (currentNode!=null){
            if (data.equals(currentNode.right.get())){
                return currentNode.right;
            }/*
            if (data.compareTo(currentNode.right.get())>0){
                currentNode = currentNode.right;
            } else{
                currentNode = currentNode.down;
            }*/
            if (isTail(currentNode.right) || data.compareTo(currentNode.right.get())<0){
                currentNode = currentNode.down;
            } else {
                currentNode = currentNode.right;
            }
        }
        return null;
    }

    private void deleteValue(Node<T> node){
        // delete all non-bottom nodes with value of given node
        while (node.down!=null){
            Node<T> leftmost = firstDifferentLeft(node);
            Node<T> rightmost = firstDifferentRight(node);
            if (rightmost.get()==null && leftmost.get()==null && height>0){
                head = head.down;
                height--;
            }
            leftmost.right = rightmost;
            rightmost.left = leftmost;
            node=node.down;
        }
        // delete bottom nodes and calculate new size
        int deletedNodes = 1;
        Node<T> leftmost = node;
        while (leftmost.left.get()!=null && leftmost.get().equals(leftmost.left.get())){
            leftmost = leftmost.left;
            deletedNodes++;
        }
        leftmost = leftmost.left;

        Node<T> rightmost = node;
        while (rightmost.right.get()!=null && rightmost.get().equals(rightmost.right.get())){
            rightmost = rightmost.right;
            deletedNodes++;
        }
        rightmost = rightmost.right;
        leftmost.right = rightmost;
        rightmost.left = leftmost;
        size -= deletedNodes;
    }

    private Node<T> buildPillar(T data, int level){
        Node<T> outp = new Node<>(data);
        for (Node<T> temp = outp; level>0; level--){
            temp.down = new Node<>(data);
            temp = temp.down;
        }
        return outp;
    }

    private Node<T> firstDifferentLeft(Node<T> node){
        while (node.left.get()!=null && node.get().equals(node.left.get())){
            node = node.left;
        }
        return node.left;
    }

    private Node<T> firstDifferentRight(Node<T> node){
        while (node.right.get()!=null && node.get().equals(node.right.get())){
            node = node.right;
        }
        return node.right;
    }

    private void adjustHeight(int level){
        while (height<level) {
            Node<T> tail = head.rightMost();
            head = new Node<>(null, null, head, null, null);
            tail = new Node<>(null, null, tail, null, null);
            head.right = tail;
            tail.left = head;
            height++;
        }
    }

    private boolean isTail(Node<T> node){
        return node.right == null;
    }

    private void verifyNotNull(Object o){
        if (o==null){
            throw new NullPointerException();
        }
    }
}
