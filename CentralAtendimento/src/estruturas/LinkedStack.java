package estruturas;

/**
 * Pilha genérica implementada sobre LISTA SIMPLESMENTE ENCADEADA.
 * Código fornecido pelo professor (Aula 7).
 *
 * Implementa exatamente a mesma interface Stack<E> que a ArrayStack do 1º
 * bimestre: o contrato (LIFO) não muda, só a implementação. É a "grande sacada
 * do TAD" — o programa cliente não precisa ser reescrito ao trocar a
 * implementação, e de fato a CentralAtendimento continuou funcionando igual.
 *
 * Mapeamento escolhido — TOPO NA CABEÇA da lista:
 *   push(e) = addFirst(e)   O(1)
 *   pop()   = removeFirst() O(1)
 *   top()   = first()       O(1)
 *
 * A outra opção (topo na cauda) foi descartada em aula porque removeLast é O(n)
 * na lista simples.
 *
 * Diferença prática em relação à ArrayStack: não existe "pilha cheia". O limite
 * deixa de ser a capacidade do vetor e passa a ser a memória disponível.
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public class LinkedStack<E> implements Stack<E> {
    private final SLinkedList<E> list;   // a pilha DELEGA todo o trabalho à lista

    public LinkedStack() {
        list = new SLinkedList<>();
    }

    @Override
    public void push(E element) {
        list.addFirst(element);
    }

    @Override
    public E pop() {
        if (isEmpty()) throw new IllegalStateException("Pilha vazia");
        return list.removeFirst();
    }

    @Override
    public E top() {
        if (isEmpty()) throw new IllegalStateException("Pilha vazia");
        return list.first();
    }

    @Override
    public boolean isEmpty() { return list.isEmpty(); }

    @Override
    public int size() { return list.size(); }

    /**
     * Adicionado pelo grupo: copia os elementos do TOPO para a BASE no vetor
     * recebido, sem alterar a pilha. Como a lista é percorrida diretamente, a
     * pilha auxiliar que a versão em vetor exigia deixa de ser necessária.
     */
    public void copiarPara(E[] destino) {
        list.copiarPara(destino);
    }
}
