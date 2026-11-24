package estruturas;

/**
 * Fila genérica implementada sobre LISTA SIMPLESMENTE ENCADEADA.
 * Código fornecido pelo professor (Aula 7).
 *
 * Implementa a mesma interface MyQueue<E> que a CircularArrayQueue do 1º
 * bimestre: o contrato (FIFO) não muda, só a implementação.
 *
 * Mapeamento — entra no fim, sai do início:
 *   enqueue(e) = addLast(e)    O(1)  (só porque a lista mantém o tail)
 *   dequeue()  = removeFirst() O(1)
 *   front()    = first()       O(1)
 *
 * Sem o tail, addLast percorreria a lista inteira e seria O(n); por isso a Aula 7
 * exige uma lista com tail mantido.
 *
 * Comparada à CircularArrayQueue, esta versão dispensa o operador % da aritmética
 * circular, o controle manual de count e a checagem de "fila cheia": o espaço
 * cresce conforme a necessidade, então não há posições a reaproveitar.
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public class LinkedQueue<E> implements MyQueue<E> {
    private final SLinkedList<E> list;   // a fila DELEGA todo o trabalho à lista

    public LinkedQueue() {
        list = new SLinkedList<>();
    }

    @Override
    public void enqueue(E element) {
        list.addLast(element);
    }

    @Override
    public E dequeue() {
        if (isEmpty()) throw new IllegalStateException("Fila vazia");
        return list.removeFirst();
    }

    @Override
    public E front() {
        if (isEmpty()) throw new IllegalStateException("Fila vazia");
        return list.first();
    }

    @Override
    public boolean isEmpty() { return list.isEmpty(); }

    @Override
    public int size() { return list.size(); }

    /**
     * Adicionado pelo grupo: copia os elementos da FRENTE para o FIM no vetor
     * recebido, sem alterar a fila. A rotação que a fila em vetor exigia
     * (desenfileirar e reenfileirar size() vezes) deixa de ser necessária.
     */
    public void copiarPara(E[] destino) {
        list.copiarPara(destino);
    }
}
