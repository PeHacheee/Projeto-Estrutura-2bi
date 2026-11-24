package estruturas;

/**
 * TAD Pilha (contrato). Código fornecido pelo professor.
 * Define O QUE a pilha faz (comportamento LIFO), e não COMO ela faz.
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public interface Stack<E> {
    void push(E element);   // Insere no topo
    E pop();                // Remove e retorna o topo
    E top();                // Consulta o topo (sem remover)
    boolean isEmpty();      // Verifica se está vazia
    int size();             // Retorna a quantidade
}
