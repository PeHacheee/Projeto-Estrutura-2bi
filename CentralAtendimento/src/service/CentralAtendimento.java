package service;

import estruturas.DLinkedList;
import estruturas.LinkedQueue;
import estruturas.LinkedStack;
import estruturas.SLinkedList;
import model.Atendimento;
import model.Operacao;
import model.Solicitacao;
import model.StatusSolicitacao;
import model.TipoOperacao;

/**
 * Regras de negócio da Central de Atendimento (versão do 2º BIMESTRE).
 *
 * É a ÚNICA classe que manipula as estruturas de dados. As quatro são
 * genéricas, dinâmicas e encadeadas, e cada uma está ligada a funcionalidades
 * reais do sistema:
 *
 *  - LinkedQueue<Solicitacao>   fila          : solicitações aguardando atendimento (FIFO);
 *  - LinkedStack<Operacao>      historico     : histórico recente de operações e Desfazer (LIFO);
 *  - SLinkedList<Solicitacao>   cadastroGeral : cadastro geral, com busca e remoção;
 *  - DLinkedList<Atendimento>   atendimentos  : histórico navegável de atendimentos concluídos.
 *
 * Fluxo: nova solicitação -> cadastro geral + fila -> atender -> pilha (operação)
 * + lista dupla (atendimento concluído).
 *
 * As versões em vetor do 1º bimestre (ArrayStack e CircularArrayQueue) continuam
 * no projeto apenas para comparação didática e NÃO são mais usadas aqui.
 *
 * Toda operação é ATÔMICA: as condições são verificadas ANTES de alterar
 * qualquer estrutura. Se a operação não puder ser concluída, nada muda e uma
 * exceção com mensagem legível é lançada para a camada de interface exibir.
 */
public class CentralAtendimento {
    public static final int PRIORIDADE_MINIMA = 1;
    public static final int PRIORIDADE_MAXIMA = 5;
    private static final int CODIGO_INICIAL = 101;

    private final LinkedQueue<Solicitacao> fila;
    private final LinkedStack<Operacao> historico;
    private final SLinkedList<Solicitacao> cadastroGeral;
    private final DLinkedList<Atendimento> atendimentos;
    private int proximoCodigo;

    public CentralAtendimento() {
        this.fila = new LinkedQueue<>();
        this.historico = new LinkedStack<>();
        this.cadastroGeral = new SLinkedList<>();
        this.atendimentos = new DLinkedList<>();
        this.proximoCodigo = CODIGO_INICIAL;
    }

    // ===================== ATENDIMENTO (Fila + Pilha) =====================

    /**
     * Cadastra uma nova solicitação: valida os dados, insere no cadastro geral
     * (Lista Simples), enfileira (Fila) e registra a operação (Pilha).
     * O código sequencial só é consumido quando a operação se completa.
     *
     * Diferente do 1º bimestre, não há verificação de "cheio": as estruturas
     * encadeadas crescem conforme a necessidade.
     */
    public Solicitacao cadastrarSolicitacao(String solicitante, String descricao,
                                            String categoria, int prioridade) {
        validarTexto(solicitante, "Nome do solicitante");
        validarTexto(descricao, "Descrição do problema");
        validarTexto(categoria, "Categoria");
        if (prioridade < PRIORIDADE_MINIMA || prioridade > PRIORIDADE_MAXIMA) {
            throw new IllegalArgumentException("Prioridade deve estar entre "
                    + PRIORIDADE_MINIMA + " e " + PRIORIDADE_MAXIMA + ".");
        }

        Solicitacao nova = new Solicitacao(proximoCodigo, solicitante.trim(),
                descricao.trim(), categoria.trim(), prioridade);
        cadastroGeral.addLast(nova);                            // cadastro geral
        fila.enqueue(nova);                                        // fila de espera
        historico.push(new Operacao(TipoOperacao.CADASTRO, nova)); // histórico de operações
        proximoCodigo++;
        return nova;
    }

    /** Consulta a próxima solicitação (frente da fila) sem removê-la. */
    public Solicitacao consultarProxima() {
        if (fila.isEmpty()) {
            throw new IllegalStateException("Fila vazia: não há solicitações aguardando atendimento.");
        }
        return fila.front();
    }

    /**
     * Atende a próxima solicitação: remove da frente da fila, conclui, registra
     * o atendimento na Lista Dupla e a operação na Pilha.
     */
    public Atendimento atenderProxima(String responsavel) {
        validarTexto(responsavel, "Nome do responsável");
        if (fila.isEmpty()) {
            throw new IllegalStateException("Fila vazia: não há solicitações aguardando atendimento.");
        }

        Solicitacao atendida = fila.dequeue();
        atendida.setResponsavel(responsavel.trim());
        atendida.setStatus(StatusSolicitacao.CONCLUIDA);

        Atendimento atendimento = new Atendimento(atendida, responsavel.trim());
        atendimentos.addLast(atendimento);                      // histórico navegável
        historico.push(new Operacao(TipoOperacao.ATENDIMENTO, atendida, atendimento));
        return atendimento;
    }

    public int quantidadeAguardando() { return fila.size(); }

    public boolean filaVazia() { return fila.isEmpty(); }

    /** Solicitações da fila, da frente para o fim. A fila não é alterada. */
    public Solicitacao[] listarFila() {
        Solicitacao[] resultado = new Solicitacao[fila.size()];
        fila.copiarPara(resultado);
        return resultado;
    }

    // ===================== OPERAÇÕES (Pilha) =====================

    /** Consulta a última operação realizada (topo da pilha) sem removê-la. */
    public Operacao consultarUltimaOperacao() {
        if (historico.isEmpty()) {
            throw new IllegalStateException("Histórico vazio: nenhuma operação foi realizada.");
        }
        return historico.top();
    }

    public int quantidadeOperacoes() { return historico.size(); }

    public boolean historicoVazio() { return historico.isEmpty(); }

    /** Operações do topo para a base (mais recente primeiro). A pilha não é alterada. */
    public Operacao[] listarHistorico() {
        Operacao[] resultado = new Operacao[historico.size()];
        historico.copiarPara(resultado);
        return resultado;
    }

    /**
     * Desfaz a última operação, revertendo seu efeito em TODAS as estruturas
     * envolvidas. A operação só sai da pilha se a reversão der certo.
     *
     *  - CADASTRO    : a solicitação deixa de existir — sai da fila e do cadastro geral.
     *  - ATENDIMENTO : o atendimento sai da lista dupla e a solicitação volta à fila,
     *                  na posição correta, com status AGUARDANDO.
     *  - CANCELAMENTO: a solicitação volta ao cadastro geral e, se estava aguardando,
     *                  volta também à fila, com o status que tinha antes.
     */
    public Operacao desfazerUltimaOperacao() {
        if (historico.isEmpty()) {
            throw new IllegalStateException("Histórico vazio: não há operação para desfazer.");
        }
        Operacao ultima = historico.top();   // consulta sem remover
        Solicitacao s = ultima.getSolicitacao();

        switch (ultima.getTipo()) {
            case CADASTRO:
                if (!removerDaFilaPorCodigo(s.getCodigo())) {
                    throw new IllegalStateException("Não foi possível desfazer: a solicitação "
                            + s.getCodigo() + " não está mais na fila de espera.");
                }
                cadastroGeral.remove(s);
                s.setStatus(StatusSolicitacao.CANCELADA);
                break;

            case ATENDIMENTO:
                Atendimento atendimento = ultima.getAtendimento();
                if (atendimento == null || !atendimentos.remove(atendimento)) {
                    throw new IllegalStateException("Não foi possível desfazer: o atendimento da "
                            + "solicitação " + s.getCodigo() + " não está mais no histórico.");
                }
                s.setStatus(StatusSolicitacao.AGUARDANDO);
                s.setResponsavel(null);
                reinserirNaFilaPorCodigo(s);
                break;

            case CANCELAMENTO:
                reinserirNoCadastroPorCodigo(s);
                if (ultima.isEstavaNaFila()) {
                    reinserirNaFilaPorCodigo(s);
                }
                // Se a solicitação já tinha sido atendida, o atendimento volta ao
                // histórico na posição exata de onde saiu, preservando a ordem
                // cronológica da Lista Dupla.
                if (ultima.getAtendimento() != null) {
                    atendimentos.add(ultima.getPosicaoAtendimento(), ultima.getAtendimento());
                }
                s.setStatus(ultima.getStatusAnterior());
                break;

            default:
                throw new IllegalStateException("Operação do tipo " + ultima.getTipo()
                        + " não pode ser desfeita.");
        }
        return historico.pop();   // reversão concluída: agora sim remove do topo
    }

    // ===================== CADASTRO GERAL (Lista Simples) =====================

    public int totalCadastradas() { return cadastroGeral.size(); }

    public boolean cadastroVazio() { return cadastroGeral.isEmpty(); }

    /** Todas as solicitações cadastradas, na ordem de cadastro. */
    public Solicitacao[] listarCadastroGeral() {
        Solicitacao[] resultado = new Solicitacao[cadastroGeral.size()];
        cadastroGeral.copiarPara(resultado);
        return resultado;
    }

    /**
     * Busca uma solicitação pelo código, percorrendo o cadastro geral.
     * Devolve null se não existir.
     *
     * O percurso é feito pela própria Lista Simples: copiarPara caminha pelos nós
     * seguindo as referências next uma única vez, O(n). O vetor devolvido é apenas
     * o veículo do resultado — a camada de serviço nunca enxerga os nós, que ficam
     * encapsulados na estrutura.
     */
    public Solicitacao buscarPorCodigo(int codigo) {
        Solicitacao[] todas = listarCadastroGeral();
        for (Solicitacao s : todas) {
            if (s.getCodigo() == codigo) return s;
        }
        return null;
    }

    /** Solicitações com o status informado. */
    public Solicitacao[] buscarPorStatus(StatusSolicitacao status) {
        return filtrar(listarCadastroGeral(), status, null);
    }

    /** Solicitações da categoria informada (comparação sem diferenciar maiúsculas). */
    public Solicitacao[] buscarPorCategoria(String categoria) {
        validarTexto(categoria, "Categoria");
        return filtrar(listarCadastroGeral(), null, categoria.trim());
    }

    /**
     * Remove uma solicitação do sistema (opção 11 do menu). A remoção é propagada
     * para TODAS as estruturas em que a solicitação aparece:
     *  - sai do cadastro geral (Lista Simples);
     *  - sai da fila de espera, se ainda estivesse aguardando;
     *  - sai do histórico de atendimentos (Lista Dupla), se já tivesse sido
     *    atendida — a regra do sistema é que o histórico não guarda atendimentos
     *    de solicitações que não existem mais no cadastro.
     *
     * Todo o estado anterior é guardado na operação CANCELAMENTO, para que o
     * Desfazer possa restaurá-lo exatamente como estava.
     */
    public Solicitacao removerSolicitacao(int codigo) {
        Solicitacao s = buscarPorCodigo(codigo);
        if (s == null) {
            throw new IllegalStateException("Não existe solicitação com o código " + codigo + ".");
        }
        boolean estavaNaFila = removerDaFilaPorCodigo(codigo);
        StatusSolicitacao statusAnterior = s.getStatus();

        // Remoção na Lista Dupla, quando permitido pelas regras: só sai do
        // histórico o atendimento cuja solicitação está sendo removida.
        Atendimento atendimentoRemovido = buscarAtendimento(codigo);
        int posicaoAtendimento = -1;
        if (atendimentoRemovido != null) {
            posicaoAtendimento = atendimentos.indexOf(atendimentoRemovido);
            atendimentos.remove(atendimentoRemovido);
        }

        cadastroGeral.remove(s);
        s.setStatus(StatusSolicitacao.CANCELADA);
        historico.push(new Operacao(TipoOperacao.CANCELAMENTO, s, statusAnterior,
                estavaNaFila, atendimentoRemovido, posicaoAtendimento));
        return s;
    }

    // ===================== HISTÓRICO DE ATENDIMENTOS (Lista Dupla) =====================

    public int totalAtendimentos() { return atendimentos.size(); }

    public boolean historicoAtendimentosVazio() { return atendimentos.isEmpty(); }

    /** Atendimentos do primeiro ao último (percurso direto). */
    public Atendimento[] listarAtendimentosDireto() {
        Atendimento[] resultado = new Atendimento[atendimentos.size()];
        atendimentos.copiarDireto(resultado);
        return resultado;
    }

    /** Atendimentos do último ao primeiro (percurso inverso, só possível na lista dupla). */
    public Atendimento[] listarAtendimentosInverso() {
        Atendimento[] resultado = new Atendimento[atendimentos.size()];
        atendimentos.copiarInverso(resultado);
        return resultado;
    }

    public Atendimento primeiroAtendimento() {
        exigirHistoricoDeAtendimentos();
        return atendimentos.first();
    }

    public Atendimento ultimoAtendimento() {
        exigirHistoricoDeAtendimentos();
        return atendimentos.last();
    }

    /** Procura um atendimento pelo código da solicitação. Devolve null se não achar. */
    public Atendimento buscarAtendimento(int codigoSolicitacao) {
        Atendimento[] todos = listarAtendimentosDireto();
        for (Atendimento a : todos) {
            if (a.getCodigoSolicitacao() == codigoSolicitacao) return a;
        }
        return null;
    }

    // --- navegação com cursor (opção 16 do menu) ---

    /** Começa a navegação no primeiro atendimento. */
    public Atendimento navegarParaPrimeiro() {
        exigirHistoricoDeAtendimentos();
        return atendimentos.irParaPrimeiro();
    }

    /** Começa a navegação em um atendimento específico. Devolve null se não achar. */
    public Atendimento navegarParaCodigo(int codigoSolicitacao) {
        Atendimento alvo = buscarAtendimento(codigoSolicitacao);
        if (alvo == null) return null;
        atendimentos.posicionarEm(alvo);
        return alvo;
    }

    public Atendimento atendimentoAtual() { return atendimentos.atual(); }

    public boolean temProximoAtendimento() { return atendimentos.temProximo(); }

    public boolean temAnteriorAtendimento() { return atendimentos.temAnterior(); }

    public Atendimento avancarAtendimento() { return atendimentos.avancar(); }

    public Atendimento retrocederAtendimento() { return atendimentos.retroceder(); }

    /** Posição do cursor começando em 1, para exibir "registro 2 de 5". */
    public int posicaoAtualNavegacao() { return atendimentos.posicaoDoCursor() + 1; }

    // ===================== RELATÓRIO (opção 18) =====================

    /** Conta quantas solicitações do cadastro geral estão em cada status. */
    public int contarPorStatus(StatusSolicitacao status) {
        return buscarPorStatus(status).length;
    }

    // ===================== AUXILIARES PRIVADOS =====================

    /**
     * Remove da fila a solicitação com o código informado, preservando a ordem
     * das demais. Transfere tudo para uma fila auxiliar pulando a removida e
     * devolve. Devolve true se a solicitação estava na fila.
     */
    private boolean removerDaFilaPorCodigo(int codigo) {
        LinkedQueue<Solicitacao> auxiliar = new LinkedQueue<>();
        boolean encontrou = false;
        while (!fila.isEmpty()) {
            Solicitacao s = fila.dequeue();
            if (!encontrou && s.getCodigo() == codigo) {
                encontrou = true;          // não copia: é a solicitação removida
            } else {
                auxiliar.enqueue(s);
            }
        }
        while (!auxiliar.isEmpty()) {
            fila.enqueue(auxiliar.dequeue());
        }
        return encontrou;
    }

    /**
     * Devolve uma solicitação à fila na posição correta, mantendo a ordem
     * crescente de código. Como os códigos são gerados em sequência, essa ordem
     * é exatamente a ordem de chegada (FIFO): a solicitação volta ao lugar que
     * ocupava antes, inclusive à frente da fila quando for a mais antiga.
     *
     * A fila só permite inserir no fim, por isso a reconstrução é feita com uma
     * fila auxiliar — o TAD é respeitado, sem acesso aos nós por fora.
     */
    private void reinserirNaFilaPorCodigo(Solicitacao s) {
        LinkedQueue<Solicitacao> auxiliar = new LinkedQueue<>();
        boolean inserida = false;
        while (!fila.isEmpty()) {
            Solicitacao atual = fila.dequeue();
            if (!inserida && s.getCodigo() < atual.getCodigo()) {
                auxiliar.enqueue(s);
                inserida = true;
            }
            auxiliar.enqueue(atual);
        }
        if (!inserida) {
            auxiliar.enqueue(s);           // é a mais recente: entra no fim
        }
        while (!auxiliar.isEmpty()) {
            fila.enqueue(auxiliar.dequeue());
        }
    }

    /**
     * Devolve uma solicitação ao cadastro geral mantendo a ordem crescente de
     * código. Aqui a Lista Simples mostra sua vantagem: ela insere diretamente
     * na posição desejada, sem precisar reconstruir a estrutura inteira.
     */
    private void reinserirNoCadastroPorCodigo(Solicitacao s) {
        int posicao = cadastroGeral.size();
        Solicitacao[] todas = listarCadastroGeral();
        for (int i = 0; i < todas.length; i++) {
            if (s.getCodigo() < todas[i].getCodigo()) {
                posicao = i;
                break;
            }
        }
        cadastroGeral.add(posicao, s);
    }

    /** Filtra por status OU por categoria, conforme o parâmetro preenchido. */
    private Solicitacao[] filtrar(Solicitacao[] todas, StatusSolicitacao status, String categoria) {
        int quantidade = 0;
        for (Solicitacao s : todas) {
            if (combina(s, status, categoria)) quantidade++;
        }
        Solicitacao[] resultado = new Solicitacao[quantidade];
        int i = 0;
        for (Solicitacao s : todas) {
            if (combina(s, status, categoria)) resultado[i++] = s;
        }
        return resultado;
    }

    private boolean combina(Solicitacao s, StatusSolicitacao status, String categoria) {
        if (status != null) return s.getStatus() == status;
        return s.getCategoria().equalsIgnoreCase(categoria);
    }

    private void exigirHistoricoDeAtendimentos() {
        if (atendimentos.isEmpty()) {
            throw new IllegalStateException("Histórico de atendimentos vazio: "
                    + "nenhuma solicitação foi concluída ainda.");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio.");
        }
    }
}
