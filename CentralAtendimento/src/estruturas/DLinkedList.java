package estruturas;

/**
 * Lista Duplamente Encadeada genérica com SENTINELAS.
 * Código fornecido pelo professor (Aula 6).
 *
 * Em vez de ponteiros null nas pontas, a lista mantém dois nós fixos que não
 * guardam dados: header e trailer. Com eles, toda operação tem sempre um vizinho
 * válido, e o código de inserção e remoção não precisa tratar os casos especiais
 * de "lista vazia" ou "primeiro/último nó".
 *
 * header <-> [101] <-> [103] <-> [105] <-> [109] <-> trailer
 *
 * Lista vazia: header.next = trailer e trailer.prev = header.
 *
 * Toda inserção passa por addBetween e toda remoção por remove(node), por isso
 * addFirst, addLast, removeFirst e removeLast são O(1) — inclusive o removeLast,
 * que na lista simples custava O(n).
 *
 * No sistema, guarda o histórico navegável de atendimentos concluídos.
 *
 * Os métodos marcados "// Adicionado pelo grupo" foram acrescentados para as
 * funcionalidades do projeto (percurso bidirecional, busca e navegação com
 * cursor), sem alterar a lógica dos métodos originais.
 *
 * @param <E> tipo genérico dos elementos armazenados
 */
public class DLinkedList<E> implements MyLinkedList<E> {
    private DNode<E> header;
    private DNode<E> trailer;
    private int size;

    // Adicionado pelo grupo: posição atual da navegação pelo histórico.
    private DNode<E> cursor;

    public DLinkedList() {
        header = new DNode<>(null, null, null);
        trailer = new DNode<>(null, header, null);
        header.setNext(trailer);
        size = 0;
        cursor = null;
    }

    /** Insere entre dois nós já existentes. É a base de TODA inserção. O(1). */
    private void addBetween(E e, DNode<E> pred, DNode<E> succ) {
        DNode<E> novo = new DNode<>(e, pred, succ);
        pred.setNext(novo);
        succ.setPrev(novo);
        size++;
    }

    @Override
    public void addFirst(E e) {
        addBetween(e, header, header.getNext());
    }

    @Override
    public void addLast(E e) {
        addBetween(e, trailer.getPrev(), trailer);
    }

    /** Remove um nó "pulando-o": liga o antecessor direto ao sucessor. O(1). */
    private E remove(DNode<E> node) {
        DNode<E> pred = node.getPrev();
        DNode<E> succ = node.getNext();
        pred.setNext(succ);
        succ.setPrev(pred);
        size--;
        // Adicionado pelo grupo: o cursor não pode ficar apontando para um nó removido.
        if (cursor == node) {
            cursor = (succ != trailer) ? succ : (pred != header ? pred : null);
        }
        node.setNext(null);
        node.setPrev(null);
        return node.getElement();
    }

    @Override
    public E removeFirst() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        return remove(header.getNext());
    }

    /** Remove o último. O(1): graças ao prev, o trailer sabe quem é o penúltimo. */
    public E removeLast() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        return remove(trailer.getPrev());
    }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public int size() { return size; }

    // ===================== ACRÉSCIMOS DO GRUPO =====================

    /** Adicionado pelo grupo: primeiro elemento real (logo após o header). O(1). */
    @Override
    public E first() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        return header.getNext().getElement();
    }

    /** Adicionado pelo grupo: último elemento real (logo antes do trailer). O(1). */
    @Override
    public E last() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        return trailer.getPrev().getElement();
    }

    /**
     * Adicionado pelo grupo: insere na posição indicada (0 = início, size = fim).
     * Reaproveita o addBetween: localiza o nó que ocupa a posição e insere o novo
     * entre ele e seu antecessor. O(n) para localizar, O(1) para ligar.
     * Usado para devolver um atendimento ao histórico, no lugar exato de onde saiu.
     */
    public void add(int index, E e) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Posição inválida: " + index);
        }
        if (index == size) {
            addLast(e);
        } else {
            DNode<E> sucessor = noNaPosicao(index);
            addBetween(e, sucessor.getPrev(), sucessor);
        }
    }

    /** Adicionado pelo grupo: elemento da posição indicada. O(n). */
    public E get(int index) {
        validarIndice(index);
        return noNaPosicao(index).getElement();
    }

    /** Adicionado pelo grupo: posição da primeira ocorrência, ou -1. O(n). */
    public int indexOf(E element) {
        DNode<E> atual = header.getNext();
        int i = 0;
        while (atual != trailer) {
            if (atual.getElement().equals(element)) return i;
            atual = atual.getNext();
            i++;
        }
        return -1;
    }

    public boolean contains(E element) { return indexOf(element) >= 0; }

    /** Adicionado pelo grupo: remove o elemento da posição indicada. O(n) para achar, O(1) para desligar. */
    public E removeAt(int index) {
        validarIndice(index);
        return remove(noNaPosicao(index));
    }

    /** Adicionado pelo grupo: remove a primeira ocorrência. Devolve true se removeu. */
    public boolean remove(E element) {
        DNode<E> atual = header.getNext();
        while (atual != trailer) {
            if (atual.getElement().equals(element)) {
                remove(atual);
                return true;
            }
            atual = atual.getNext();
        }
        return false;
    }

    /** Adicionado pelo grupo: copia os elementos do PRIMEIRO para o ÚLTIMO. O(n). */
    public void copiarDireto(E[] destino) {
        DNode<E> atual = header.getNext();
        int i = 0;
        while (atual != trailer && i < destino.length) {
            destino[i++] = atual.getElement();
            atual = atual.getNext();
        }
    }

    /**
     * Adicionado pelo grupo: copia os elementos do ÚLTIMO para o PRIMEIRO. O(n).
     * Só é possível por causa do ponteiro prev: na lista simples, percorrer de
     * trás para frente seria impossível sem refazer o caminho a cada passo.
     */
    public void copiarInverso(E[] destino) {
        DNode<E> atual = trailer.getPrev();
        int i = 0;
        while (atual != header && i < destino.length) {
            destino[i++] = atual.getElement();
            atual = atual.getPrev();
        }
    }

    // --- navegação com cursor (histórico navegável) ---

    /** Adicionado pelo grupo: posiciona o cursor no primeiro registro. */
    public E irParaPrimeiro() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        cursor = header.getNext();
        return cursor.getElement();
    }

    /** Adicionado pelo grupo: posiciona o cursor no último registro. */
    public E irParaUltimo() {
        if (isEmpty()) throw new IllegalStateException("Lista vazia");
        cursor = trailer.getPrev();
        return cursor.getElement();
    }

    /** Adicionado pelo grupo: registro sob o cursor. */
    public E atual() {
        if (cursor == null) throw new IllegalStateException("Nenhum registro selecionado");
        return cursor.getElement();
    }

    /** Adicionado pelo grupo: há registro seguinte? (o trailer marca o fim). */
    public boolean temProximo() {
        return cursor != null && cursor.getNext() != trailer;
    }

    /** Adicionado pelo grupo: há registro anterior? (o header marca o início). */
    public boolean temAnterior() {
        return cursor != null && cursor.getPrev() != header;
    }

    /** Adicionado pelo grupo: avança o cursor um registro. O(1). */
    public E avancar() {
        if (!temProximo()) throw new IllegalStateException("Já está no último registro");
        cursor = cursor.getNext();
        return cursor.getElement();
    }

    /** Adicionado pelo grupo: retrocede o cursor um registro. O(1) — a vantagem do prev. */
    public E retroceder() {
        if (!temAnterior()) throw new IllegalStateException("Já está no primeiro registro");
        cursor = cursor.getPrev();
        return cursor.getElement();
    }

    /** Adicionado pelo grupo: posiciona o cursor sobre um elemento. true se o achou. */
    public boolean posicionarEm(E element) {
        DNode<E> atual = header.getNext();
        while (atual != trailer) {
            if (atual.getElement().equals(element)) {
                cursor = atual;
                return true;
            }
            atual = atual.getNext();
        }
        return false;
    }

    /** Adicionado pelo grupo: posição do cursor (0 = primeiro), ou -1 se não há cursor. */
    public int posicaoDoCursor() {
        if (cursor == null) return -1;
        DNode<E> atual = header.getNext();
        int i = 0;
        while (atual != cursor) {
            atual = atual.getNext();
            i++;
        }
        return i;
    }

    // ===================== AUXILIARES PRIVADOS =====================

    /**
     * Caminha a partir da ponta mais próxima: até a metade usa o next, depois
     * usa o prev. Só é possível por causa do encadeamento duplo.
     */
    private DNode<E> noNaPosicao(int index) {
        DNode<E> atual;
        if (index <= size / 2) {
            atual = header.getNext();
            for (int i = 0; i < index; i++) atual = atual.getNext();
        } else {
            atual = trailer.getPrev();
            for (int i = size - 1; i > index; i--) atual = atual.getPrev();
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
