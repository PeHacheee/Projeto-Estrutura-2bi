package estruturas;

/**
 * NOTA DO 2º BIMESTRE: esta classe permanece no projeto apenas para comparação
 * didática com a LinkedQueue (fila encadeada). A aplicação final NÃO a utiliza mais —
 * a fila de espera passou a usar a versão com alocação dinâmica.
 *
 * Fila genérica circular implementada em vetor. Código fornecido pelo professor.
 *
 * Ideia: front e rear "dão a volta" no vetor usando o resto da divisão (%),
 * o que elimina o deslocamento O(n) da fila linear e reaproveita as posições livres.
 *
 *  - Avançar um índice: (indice + 1) % capacidade
 *  - Fila vazia: count == 0
 *  - Fila cheia: count == capacidade
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public class CircularArrayQueue<E> implements MyQueue<E> {
    private E[] elements;      // Vetor que armazena os dados
    private int front;         // Índice do início (frente)
    private int rear;          // Índice do final (trás)
    private int count;         // Quantos elementos
    private static final int DEFAULT_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public CircularArrayQueue() {
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        front = 0; rear = -1; // -1 significa: fila vazia!
        count = 0;
    }

    // Adicionado pelo grupo: construtor com capacidade customizada.
    // Permite demonstrar "fila cheia" e o reaproveitamento circular com poucos elementos.
    @SuppressWarnings("unchecked")
    public CircularArrayQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser positiva");
        }
        elements = (E[]) new Object[capacity];
        front = 0; rear = -1;
        count = 0;
    }

    @Override
    public void enqueue(E element) {
        if (count == elements.length) {
            throw new IllegalStateException("Fila cheia");
        }
        rear = (rear + 1) % elements.length;
        elements[rear] = element;
        count++;
    }

    @Override
    public E dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Fila vazia");
        }
        E element = elements[front];
        elements[front] = null; // Libera referência
        front = (front + 1) % elements.length;
        count--;
        return element;
    }

    @Override
    public E front() {
        if (isEmpty()) throw new IllegalStateException("Fila vazia");
        return elements[front];
    }

    @Override
    public boolean isEmpty() { return count == 0; }

    @Override
    public int size() { return count; }

    // Adicionado pelo grupo: indica se a fila atingiu a capacidade máxima.
    // Usado para verificar o limite ANTES de alterar qualquer estrutura (operações atômicas).
    public boolean isFull() { return count == elements.length; }

    // Adicionado pelo grupo: retorna a capacidade total do vetor.
    public int capacity() { return elements.length; }

    // Adicionado pelo grupo: índices internos (somente leitura), usados nos testes
    // para demonstrar o reaproveitamento circular de posições.
    public int frontIndex() { return front; }
    public int rearIndex() { return rear; }
}
