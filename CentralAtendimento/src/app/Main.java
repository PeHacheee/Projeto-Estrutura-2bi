package app;

import service.CentralAtendimento;
import ui.Menu;

/**
 * Ponto de entrada do Sistema de Atendimento e Gerenciamento de Serviços
 * (versão do 2º bimestre).
 *
 * Diferença em relação ao 1º bimestre: a central não recebe mais capacidades,
 * porque as quatro estruturas agora são dinâmicas e encadeadas — crescem
 * conforme a necessidade, sem limite fixo.
 */
public class Main {
    public static void main(String[] args) {
        CentralAtendimento central = new CentralAtendimento();
        new Menu(central).executar();
    }
}
