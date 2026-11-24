package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe de domínio: representa uma solicitação de atendimento.
 * É o tipo de objeto armazenado na Fila de espera.
 */
public class Solicitacao {
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final int codigo;
    private final String solicitante;
    private final String descricao;
    private final String categoria;
    private final int prioridade;          // 1 a 5, apenas registrada
    private final LocalDateTime dataHoraAbertura;
    private StatusSolicitacao status;
    private String responsavel;            // definido no atendimento

    public Solicitacao(int codigo, String solicitante, String descricao,
                       String categoria, int prioridade) {
        this.codigo = codigo;
        this.solicitante = solicitante;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.dataHoraAbertura = LocalDateTime.now();
        this.status = StatusSolicitacao.AGUARDANDO;
        this.responsavel = null;
    }

    public int getCodigo() { return codigo; }
    public String getSolicitante() { return solicitante; }
    public String getDescricao() { return descricao; }
    public String getCategoria() { return categoria; }
    public int getPrioridade() { return prioridade; }
    public LocalDateTime getDataHoraAbertura() { return dataHoraAbertura; }
    public StatusSolicitacao getStatus() { return status; }
    public String getResponsavel() { return responsavel; }

    public void setStatus(StatusSolicitacao status) { this.status = status; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | Categoria: %s | Prioridade: %d | Aberta em: %s | Status: %s | Responsável: %s",
                codigo, solicitante, descricao, categoria, prioridade,
                dataHoraAbertura.format(FORMATO), status,
                responsavel == null ? "-" : responsavel);
    }
}
