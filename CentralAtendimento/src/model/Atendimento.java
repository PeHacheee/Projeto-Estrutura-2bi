package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe de domínio do 2º bimestre: representa um atendimento CONCLUÍDO.
 *
 * É o tipo de objeto armazenado na Lista Duplamente Encadeada, que forma o
 * histórico navegável de atendimentos.
 *
 * Enquanto a Solicitacao descreve o pedido, o Atendimento descreve a conclusão
 * dele: quem atendeu e quando.
 */
public class Atendimento {
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Solicitacao solicitacao;
    private final String responsavel;
    private final LocalDateTime dataHoraConclusao;

    public Atendimento(Solicitacao solicitacao, String responsavel) {
        this.solicitacao = solicitacao;
        this.responsavel = responsavel;
        this.dataHoraConclusao = LocalDateTime.now();
    }

    public Solicitacao getSolicitacao() { return solicitacao; }
    public String getResponsavel() { return responsavel; }
    public LocalDateTime getDataHoraConclusao() { return dataHoraConclusao; }

    /** Atalho: o código da solicitação atendida, usado nas buscas do histórico. */
    public int getCodigoSolicitacao() { return solicitacao.getCodigo(); }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | Categoria: %s | Atendida por: %s | Concluída em: %s",
                solicitacao.getCodigo(), solicitacao.getSolicitante(),
                solicitacao.getDescricao(), solicitacao.getCategoria(),
                responsavel, dataHoraConclusao.format(FORMATO));
    }
}
