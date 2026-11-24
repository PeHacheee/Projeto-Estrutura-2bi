package estruturas;

/**
 * NOTA DO 2º BIMESTRE: esta classe permanece no projeto apenas para comparação
 * didática com a LinkedStack (pilha encadeada). A aplicação final NÃO a utiliza mais —
 * o histórico de operações passou a usar a versão com alocação dinâmica.
 *
 * Pilha genérica implementada em vetor. Código fornecido pelo professor.
 *
 * Decisões da implementação (vistas em aula):
 *  1. O topo fica no FIM do vetor (índice topIndex), então push e pop são O(1).
 *  2. Capacidade fixa (estrutura estática).
 *  3. Posições liberadas recebem null (evita memory leak).
 *
 * topIndex == -1 significa pilha vazia.
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public class ArrayStack<E> implements Stack<E> {
    // <E> é um tipo genérico: funciona com qualquer tipo
    private E[] elements;                          // Vetor que armazena os dados
    private int topIndex;                          // Índice do último elemento (topo)
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public ArrayStack() {
        // Java não permite new E[]: cria um vetor de Object e faz cast para E[]
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        topIndex = -1; // -1 significa: pilha vazia!
    }

    // Adicionado pelo grupo: construtor com capacidade customizada.
    // Permite usar uma capacidade maior no sistema e capacidades pequenas nos testes de limite.
    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser positiva");
        }
        elements = (E[]) new Object[capacity];
        topIndex = -1;
    }

    @Override
    public void push(E element) {
        if (topIndex == elements.length - 1) {
            throw new IllegalStateException("Pilha cheia");
        }
        topIndex++;
        elements[topIndex] = element;
    }

    @Override
    public E pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Pilha vazia");
        }
        E element = elements[topIndex];
        elements[topIndex] = null; // Libera referência
        topIndex--;
        return element;
    }

    @Override
    public E top() {
        if (isEmpty()) throw new IllegalStateException("Pilha vazia");
        return elements[topIndex];
    }

    @Override
    public boolean isEmpty() { return topIndex == -1; }

    @Override
    public int size() { return topIndex + 1; }

    // Adicionado pelo grupo: indica se a pilha atingiu a capacidade máxima.
    // Usado para verificar o limite ANTES de alterar qualquer estrutura (operações atômicas).
    public boolean isFull() { return topIndex == elements.length - 1; }

    // Adicionado pelo grupo: retorna a capacidade total do vetor.
    public int capacity() { return elements.length; }
}
