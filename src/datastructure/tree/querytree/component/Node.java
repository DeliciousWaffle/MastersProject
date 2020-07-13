package datastructure.tree.querytree.component;

public class Node<E> {

    private E element;
    private Node<E> next;

    public Node(E element) {
        this.element = element;
        this.next = null;
    }
}
