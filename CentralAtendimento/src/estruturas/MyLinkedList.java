package estruturas;

/**
 * TAD Lista Encadeada (contrato). Código fornecido pelo professor (Aula 5).
 *
 * Define O QUE a lista deve fazer, não COMO. O nome MyLinkedList evita conflito
 * com a java.util.LinkedList, cujo uso é proibido neste projeto.
 *
 * Duas implementações diferentes cumprem este mesmo contrato:
 *  - SLinkedList: encadeamento simples (cada nó conhece só o próximo);
 *  - DLinkedList: encadeamento duplo com sentinelas (cada nó conhece os dois vizinhos).
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public interface MyLinkedList<E> {
    int size();
    boolean isEmpty();
    E first();
    E last();
    void addFirst(E element);
    void addLast(E element);
    E removeFirst();
}
