package model;

/**
 * Tipos de operação registrados na Pilha de histórico (seção 12 do enunciado).
 * CANCELAMENTO faz parte do modelo, mas o menu do 1º bimestre não possui
 * opção própria para ele.
 */
public enum TipoOperacao {
    CADASTRO,
    ATENDIMENTO,
    CANCELAMENTO
}
