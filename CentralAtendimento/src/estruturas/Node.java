package estruturas;

/**
 * Nó de uma lista simplesmente encadeada. Código fornecido pelo professor (Aula 5).
 *
 * Cada nó é um objeto com dois atributos: o elemento e uma REFERÊNCIA para o
 * próximo nó — não um índice nem um valor. É isso que substitui a contiguidade
 * do vetor: os nós ficam espalhados na memória e a ordem é lógica, dada pelos
 * ponteiros.
 *
 * head -> [A | next] -> [B | next] -> [C | null]
 *
 * @param <E> tipo genérico do elemento armazenado
 */
public class Node<E> {
    private E element;
    private Node<E> next;

    public Node(E e, Node<E> n) {
        this.element = e;
        this.next = n;
    }

    public E getElement() { return element; }
    public Node<E> getNext() { return next; }
    public void setElement(E e) { this.element = e; }
    public void setNext(Node<E> n) { this.next = n; }
}
