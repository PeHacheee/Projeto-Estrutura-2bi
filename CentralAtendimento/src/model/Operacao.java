package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe de domínio: representa uma operação realizada no sistema.
 * É o tipo de objeto armazenado na Pilha de histórico.
 *
 * Além do tipo e da data/hora, a operação guarda tudo o que é preciso para
 * DESFAZÊ-LA e devolver o sistema ao estado anterior:
 *  - a Solicitacao envolvida (sempre);
 *  - o Atendimento gerado, quando o tipo é ATENDIMENTO;
 *  - no CANCELAMENTO: o status anterior, se a solicitação estava na fila e,
 *    caso ela já tivesse sido atendida, o Atendimento retirado do histórico e
 *    a posição que ele ocupava na Lista Dupla.
 */
public class Operacao {
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final TipoOperacao tipo;
    private final Solicitacao solicitacao;
    private final LocalDateTime dataHora;

    private final Atendimento atendimento;          // ATENDIMENTO e CANCELAMENTO
    private final StatusSolicitacao statusAnterior; // só em CANCELAMENTO
    private final boolean estavaNaFila;             // só em CANCELAMENTO
    private final int posicaoAtendimento;           // só em CANCELAMENTO (-1 = não havia)

    /** Operação de CADASTRO. */
    public Operacao(TipoOperacao tipo, Solicitacao solicitacao) {
        this(tipo, solicitacao, null, null, false, -1);
    }

    /** Operação de ATENDIMENTO: guarda o atendimento criado. */
    public Operacao(TipoOperacao tipo, Solicitacao solicitacao, Atendimento atendimento) {
        this(tipo, solicitacao, atendimento, null, false, -1);
    }

    /**
     * Operação de CANCELAMENTO: guarda o estado anterior da solicitação e, se
     * ela já havia sido atendida, o atendimento retirado do histórico e a
     * posição que ele ocupava, para que o Desfazer possa recolocá-lo no lugar.
     */
    public Operacao(TipoOperacao tipo, Solicitacao solicitacao, StatusSolicitacao statusAnterior,
                    boolean estavaNaFila, Atendimento atendimentoRemovido, int posicaoAtendimento) {
        this(tipo, solicitacao, atendimentoRemovido, statusAnterior, estavaNaFila, posicaoAtendimento);
    }

    private Operacao(TipoOperacao tipo, Solicitacao solicitacao, Atendimento atendimento,
                     StatusSolicitacao statusAnterior, boolean estavaNaFila, int posicaoAtendimento) {
        this.tipo = tipo;
        this.solicitacao = solicitacao;
        this.atendimento = atendimento;
        this.statusAnterior = statusAnterior;
        this.estavaNaFila = estavaNaFila;
        this.posicaoAtendimento = posicaoAtendimento;
        this.dataHora = LocalDateTime.now();
    }

    public TipoOperacao getTipo() { return tipo; }
    public Solicitacao getSolicitacao() { return solicitacao; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Atendimento getAtendimento() { return atendimento; }
    public StatusSolicitacao getStatusAnterior() { return statusAnterior; }
    public boolean isEstavaNaFila() { return estavaNaFila; }
    public int getPosicaoAtendimento() { return posicaoAtendimento; }

    @Override
    public String toString() {
        String acao;
        switch (tipo) {
            case CADASTRO:    acao = "cadastrada"; break;
            case ATENDIMENTO: acao = "atendida";   break;
            default:          acao = "cancelada";  break;
        }
        return String.format("[%s] %s: Solicitação %d %s",
                dataHora.format(FORMATO), tipo, solicitacao.getCodigo(), acao);
    }
}
