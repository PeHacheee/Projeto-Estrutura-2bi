package estruturas;

/**
 * Nó de uma lista duplamente encadeada. Código fornecido pelo professor (Aula 6).
 *
 * Em relação ao Node simples, ganha o ponteiro prev. É essa referência a mais
 * que permite navegar para trás e remover o último em O(1).
 *
 * null <- [prev | A | next] <-> [prev | B | next] -> null
 *
 * @param <E> tipo genérico do elemento armazenado
 */
public class DNode<E> {
    private E element;
    private DNode<E> next;
    private DNode<E> prev;

    public DNode(E e, DNode<E> p, DNode<E> n) {
        this.element = e;
        this.prev = p;
        this.next = n;
    }

    public E getElement() { return element; }
    public DNode<E> getNext() { return next; }
    public DNode<E> getPrev() { return prev; }
    public void setElement(E e) { this.element = e; }
    public void setNext(DNode<E> n) { this.next = n; }
    public void setPrev(DNode<E> p) { this.prev = p; }
}
