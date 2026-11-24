package estruturas;

/**
 * TAD Fila (contrato). Código fornecido pelo professor.
 * A interface é o "o quê"; a implementação é o "como". Comportamento FIFO.
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public interface MyQueue<E> {
    void enqueue(E element);   // Insere no final
    E dequeue();               // Remove e retorna o início
    E front();                 // Consulta só o início da fila
    boolean isEmpty();         // Verifica se está vazia
    int size();                // Retorna a quantidade
}
