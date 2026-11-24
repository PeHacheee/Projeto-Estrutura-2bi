package estruturas;

/**
 * Lista Simplesmente Encadeada genérica. Código fornecido pelo professor (Aula 5).
 *
 * Mantém três atributos:
 *  - head: primeiro nó (null se vazia);
 *  - tail: último nó, o que permite addLast em O(1);
 *  - size: quantidade de elementos.
 *
 * head                                   tail
 *  v                                      v
 * [101] -> [102] -> [103] -> [104] -> null
 *
 * Complexidades (Aula 5): addFirst O(1), removeFirst O(1), addLast O(1) graças
 * ao tail, removeLast O(n) porque é preciso localizar o penúltimo nó, e acesso
 * por índice O(n), já que não existe acesso direto como no vetor.
 *
 * No sistema, é a base da Pilha, da Fila e do cadastro geral de solicitações.
 *
 * Os métodos marcados "// Adicionado pelo grupo" foram acrescentados para as
 * funcionalidades do projeto (busca, remoção em qualquer posição e percurso),
 * sem alterar a lógica dos métodos originais.
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public class SLinkedList<E> implements MyLinkedList<E> {
    private Node<E> head;
    private Node<E> tail;
    private int size;

    public SLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /** Insere no início: o novo nó aponta para o antigo head. O(1). */
    @Override
    public void addFirst(E element) {
        Node<E> novo = new Node<>(element, head);
        head = novo;
        if (size == 0) tail = novo;      // lista estava vazia: sincroniza o tail
        size++;
    }

    /** Insere no fim: o antigo tail passa a apontar para o novo nó. O(1). */
    @Override
    public void addLast(E element) {
        Node<E> novo = new Node<>(element, null);
        if (size == 0) head = novo;
        else tail.setNext(novo);
        tail = novo;
        size++;
    }

    /** Remove e devolve o primeiro elemento: o head avança. O(1). */
    @Override
    public E removeFirst() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        E elemento = head.getElement();
        head = head.getNext();
        size--;
        if (size == 0) tail = null;      // esvaziou: o tail também é zerado
        return elemento;
    }

    /**
     * Remove e devolve o último elemento. O(n): como cada nó conhece apenas o
     * próximo, é preciso percorrer até o penúltimo para desligar o tail.
     * É exatamente esse custo que a lista duplamente encadeada resolve.
     */
    public E removeLast() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        if (size == 1) return removeFirst();
        Node<E> atual = head;
        while (atual.getNext() != tail) {
            atual = atual.getNext();
        }
        E elemento = tail.getElement();
        atual.setNext(null);
        tail = atual;
        size--;
        return elemento;
    }

    @Override
    public E first() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        return head.getElement();
    }

    @Override
    public E last() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        return tail.getElement();
    }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public int size() { return size; }

    // ===================== ACRÉSCIMOS DO GRUPO =====================

    /**
     * Adicionado pelo grupo: devolve o elemento da posição indicada (0 = primeiro).
     * O(n) — na lista encadeada não há acesso direto por índice, é preciso
     * percorrer a partir do head seguindo os next (Aula 5).
     */
    public E get(int index) {
        validarIndice(index);
        return noNaPosicao(index).getElement();
    }

    /** Adicionado pelo grupo: posição da primeira ocorrência, ou -1. O(n). */
    public int indexOf(E element) {
        Node<E> atual = head;
        int i = 0;
        while (atual != null) {
            if (atual.getElement().equals(element)) return i;
            atual = atual.getNext();
            i++;
        }
        return -1;
    }

    /** Adicionado pelo grupo: diz se o elemento está na lista. O(n). */
    public boolean contains(E element) { return indexOf(element) >= 0; }

    /**
     * Adicionado pelo grupo: insere na posição indicada (0 = início, size = fim).
     * Para inserir no meio é preciso localizar o nó ANTERIOR e religar as
     * referências: anterior -> novo -> antigo. O(n).
     */
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Posição inválida: " + index);
        }
        if (index == 0) {
            addFirst(element);
        } else if (index == size) {
            addLast(element);
        } else {
            Node<E> anterior = noNaPosicao(index - 1);
            anterior.setNext(new Node<>(element, anterior.getNext()));
            size++;
        }
    }

    /**
     * Adicionado pelo grupo: remove e devolve o elemento da posição indicada.
     * Trata os três casos clássicos: remoção do primeiro, do meio e do último. O(n).
     */
    public E removeAt(int index) {
        validarIndice(index);
        if (index == 0) return removeFirst();                 // remoção do PRIMEIRO
        Node<E> anterior = noNaPosicao(index - 1);
        Node<E> removido = anterior.getNext();                // MEIO ou ÚLTIMO
        anterior.setNext(removido.getNext());
        if (removido == tail) tail = anterior;                // era o último: o tail recua
        removido.setNext(null);                               // solta as referências
        E elemento = removido.getElement();
        removido.setElement(null);
        size--;
        return elemento;
    }

    /** Adicionado pelo grupo: remove a primeira ocorrência. Devolve true se removeu. O(n). */
    public boolean remove(E element) {
        int index = indexOf(element);
        if (index < 0) return false;
        removeAt(index);
        return true;
    }

    /**
     * Adicionado pelo grupo: copia os elementos, do início para o fim, no vetor
     * recebido, sem alterar a lista. Percorre a cadeia uma única vez, O(n).
     * O vetor é criado por quem chama, com o tipo concreto, o que evita o
     * problema de genéricos com arrays visto na Aula 2.
     */
    public void copiarPara(E[] destino) {
        Node<E> atual = head;
        int i = 0;
        while (atual != null && i < destino.length) {
            destino[i++] = atual.getElement();
            atual = atual.getNext();
        }
    }

    // ===================== AUXILIARES PRIVADOS =====================

    /** Caminha pela cadeia até a posição pedida: é o preço do acesso sequencial. */
    private Node<E> noNaPosicao(int index) {
        Node<E> atual = head;
        for (int i = 0; i < index; i++) {
            atual = atual.getNext();
        }
        return atual;
    }

    private void validarIndice(int index) {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Posição inválida: " + index);
        }
    }
}
