package ui;

import java.io.Console;
import java.nio.charset.Charset;
import java.util.Scanner;

import model.Atendimento;
import model.Operacao;
import model.Solicitacao;
import model.StatusSolicitacao;
import service.CentralAtendimento;

/**
 * Interface textual do sistema (versão do 2º BIMESTRE, com 18 opções).
 *
 * Responsável APENAS por entrada e saída: lê e valida o que o usuário digita,
 * delega as regras à CentralAtendimento e exibe os resultados. Não conhece
 * nenhuma estrutura de dados — por isso a troca das versões em vetor pelas
 * encadeadas não exigiu mudança alguma nesta camada.
 *
 * Nenhuma exceção chega ao usuário: todas são convertidas em mensagens.
 */
public class Menu {
    private final CentralAtendimento central;
    private final Scanner scanner;
    private boolean entradaEncerrada = false;   // fim da entrada (Ctrl+D ou arquivo roteirizado)

    public Menu(CentralAtendimento central) {
        this.central = central;
        this.scanner = new Scanner(System.in, codificacaoDaEntrada());
    }

    /**
     * Usa a mesma codificação do console (ex.: cp850 no cmd do Windows, UTF-8 no Linux),
     * para que nomes com acento digitados pelo usuário sejam lidos corretamente.
     * Sem console (IDE ou entrada redirecionada), usa a codificação padrão do Java.
     */
    private static Charset codificacaoDaEntrada() {
        Console console = System.console();
        return (console != null) ? console.charset() : Charset.defaultCharset();
    }

    /** Laço principal: exibe o menu até o usuário escolher 0 (Encerrar). */
    public void executar() {
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            if (entradaEncerrada) {
                opcao = 0;
            }
            System.out.println();
            try {
                switch (opcao) {
                    // ATENDIMENTO
                    case 1:  cadastrar(); break;
                    case 2:  consultarProxima(); break;
                    case 3:  atenderProxima(); break;
                    case 4:  exibirFila(); break;
                    case 5:  exibirQuantidade(); break;
                    // OPERAÇÕES
                    case 6:  consultarUltimaOperacao(); break;
                    case 7:  exibirHistoricoOperacoes(); break;
                    case 8:  desfazer(); break;
                    // CADASTRO GERAL
                    case 9:  exibirCadastroGeral(); break;
                    case 10: pesquisarPorCodigo(); break;
                    case 11: removerSolicitacao(); break;
                    case 12: exibirPorStatus(); break;
                    case 13: exibirPorCategoria(); break;
                    // HISTÓRICO DE ATENDIMENTOS
                    case 14: exibirAtendimentosDireto(); break;
                    case 15: exibirAtendimentosInverso(); break;
                    case 16: navegarHistorico(); break;
                    case 17: pesquisarAtendimento(); break;
                    // RELATÓRIOS
                    case 18: exibirResumo(); break;
                    case 0:  System.out.println("Encerrando o sistema. Até logo!"); break;
                    default: System.out.println("Opção inválida! Digite um número de 0 a 18.");
                }
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Atenção: " + e.getMessage());
            }
            if (opcao != 0) {
                System.out.println();
            }
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("=================================================");
        System.out.println("      CENTRAL DE ATENDIMENTO E SERVIÇOS");
        System.out.println("=================================================");
        System.out.println();
        System.out.println("ATENDIMENTO");
        System.out.println("1  - Cadastrar nova solicitação");
        System.out.println("2  - Consultar próxima solicitação");
        System.out.println("3  - Atender próxima solicitação");
        System.out.println("4  - Exibir fila de espera");
        System.out.println("5  - Quantidade de solicitações aguardando");
        System.out.println();
        System.out.println("OPERAÇÕES");
        System.out.println("6  - Consultar última operação");
        System.out.println("7  - Exibir histórico recente");
        System.out.println("8  - Desfazer última operação");
        System.out.println();
        System.out.println("CADASTRO GERAL");
        System.out.println("9  - Exibir todas as solicitações");
        System.out.println("10 - Pesquisar solicitação por código");
        System.out.println("11 - Remover solicitação");
        System.out.println("12 - Exibir solicitações por status");
        System.out.println("13 - Exibir solicitações por categoria");
        System.out.println();
        System.out.println("HISTÓRICO DE ATENDIMENTOS");
        System.out.println("14 - Exibir do primeiro ao último");
        System.out.println("15 - Exibir do último ao primeiro");
        System.out.println("16 - Navegar pelo histórico");
        System.out.println("17 - Pesquisar atendimento");
        System.out.println();
        System.out.println("RELATÓRIOS");
        System.out.println("18 - Exibir resumo do sistema");
        System.out.println();
        System.out.println("0  - Encerrar");
    }

    // ===================== ATENDIMENTO =====================

    private void cadastrar() {
        System.out.println("--- Cadastro de nova solicitação ---");
        String solicitante = lerTextoObrigatorio("Nome do solicitante: ");
        String descricao = lerTextoObrigatorio("Descrição do problema: ");
        String categoria = lerTextoObrigatorio("Categoria: ");
        int prioridade = lerInteiroNaFaixa("Prioridade (" + CentralAtendimento.PRIORIDADE_MINIMA
                        + " a " + CentralAtendimento.PRIORIDADE_MAXIMA + "): ",
                CentralAtendimento.PRIORIDADE_MINIMA, CentralAtendimento.PRIORIDADE_MAXIMA);
        if (entradaEncerrada) return;

        Solicitacao nova = central.cadastrarSolicitacao(solicitante, descricao, categoria, prioridade);
        System.out.println("Solicitação cadastrada, inserida no cadastro geral e na fila de espera:");
        System.out.println("  " + nova);
    }

    private void consultarProxima() {
        Solicitacao proxima = central.consultarProxima();   // consulta antes de imprimir
        System.out.println("Próxima solicitação a ser atendida:");
        System.out.println("  " + proxima);
    }

    private void atenderProxima() {
        Solicitacao proxima = central.consultarProxima();
        System.out.println("Solicitação a ser atendida: " + proxima);
        String responsavel = lerTextoObrigatorio("Nome do responsável pelo atendimento: ");
        if (entradaEncerrada) return;

        Atendimento atendimento = central.atenderProxima(responsavel);
        System.out.println("Solicitação concluída e registrada no histórico de atendimentos:");
        System.out.println("  " + atendimento);
    }

    private void exibirFila() {
        if (central.filaVazia()) {
            System.out.println("Fila vazia: não há solicitações aguardando.");
            return;
        }
        Solicitacao[] itens = central.listarFila();
        System.out.println("Fila de espera (da frente para o fim):");
        imprimirSolicitacoes(itens, true);
    }

    private void exibirQuantidade() {
        System.out.println("Solicitações aguardando atendimento: " + central.quantidadeAguardando());
        if (central.filaVazia()) {
            System.out.println("Situação da fila: VAZIA.");
        } else {
            System.out.println("Situação da fila: com " + central.quantidadeAguardando()
                    + " solicitação(ões). A fila é dinâmica e não tem limite de capacidade.");
        }
    }

    // ===================== OPERAÇÕES =====================

    private void consultarUltimaOperacao() {
        Operacao ultima = central.consultarUltimaOperacao();
        System.out.println("Última operação realizada (topo da pilha):");
        System.out.println("  " + ultima);
    }

    private void exibirHistoricoOperacoes() {
        if (central.historicoVazio()) {
            System.out.println("Histórico vazio: nenhuma operação foi realizada.");
            return;
        }
        Operacao[] ops = central.listarHistorico();
        System.out.println("Histórico de operações (do topo para a base - mais recente primeiro):");
        for (int i = 0; i < ops.length; i++) {
            String marcador = (i == 0) ? "TOPO -> " : "        ";
            System.out.println(marcador + (i + 1) + ". " + ops[i]);
        }
    }

    private void desfazer() {
        Operacao desfeita = central.desfazerUltimaOperacao();
        System.out.println("Operação desfeita: " + desfeita);
        System.out.println("Situação atual da solicitação: " + desfeita.getSolicitacao());
    }

    // ===================== CADASTRO GERAL =====================

    private void exibirCadastroGeral() {
        if (central.cadastroVazio()) {
            System.out.println("Cadastro geral vazio: nenhuma solicitação registrada.");
            return;
        }
        Solicitacao[] todas = central.listarCadastroGeral();
        System.out.println("Cadastro geral (" + todas.length + " solicitação(ões)):");
        imprimirSolicitacoes(todas, false);
    }

    private void pesquisarPorCodigo() {
        int codigo = lerInteiro("Código da solicitação: ");
        if (entradaEncerrada) return;
        Solicitacao s = central.buscarPorCodigo(codigo);
        if (s == null) {
            System.out.println("Nenhuma solicitação encontrada com o código " + codigo + ".");
            return;
        }
        System.out.println("Solicitação encontrada:");
        System.out.println("  " + s);
    }

    private void removerSolicitacao() {
        int codigo = lerInteiro("Código da solicitação a remover: ");
        if (entradaEncerrada) return;
        Solicitacao removida = central.removerSolicitacao(codigo);
        System.out.println("Solicitação removida do cadastro geral e da fila de espera:");
        System.out.println("  " + removida);
        System.out.println("A remoção foi registrada no histórico e pode ser desfeita (opção 8).");
    }

    private void exibirPorStatus() {
        System.out.println("Status disponíveis:");
        StatusSolicitacao[] valores = StatusSolicitacao.values();
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + " - " + valores[i]);
        }
        int escolha = lerInteiroNaFaixa("Escolha o status: ", 1, valores.length);
        if (entradaEncerrada) return;

        StatusSolicitacao status = valores[escolha - 1];
        Solicitacao[] encontradas = central.buscarPorStatus(status);
        if (encontradas.length == 0) {
            System.out.println("Nenhuma solicitação com status " + status + ".");
            return;
        }
        System.out.println("Solicitações com status " + status + " (" + encontradas.length + "):");
        imprimirSolicitacoes(encontradas, false);
    }

    private void exibirPorCategoria() {
        String categoria = lerTextoObrigatorio("Categoria: ");
        if (entradaEncerrada) return;
        Solicitacao[] encontradas = central.buscarPorCategoria(categoria);
        if (encontradas.length == 0) {
            System.out.println("Nenhuma solicitação na categoria \"" + categoria + "\".");
            return;
        }
        System.out.println("Solicitações da categoria \"" + categoria + "\" (" + encontradas.length + "):");
        imprimirSolicitacoes(encontradas, false);
    }

    // ===================== HISTÓRICO DE ATENDIMENTOS =====================

    private void exibirAtendimentosDireto() {
        if (central.historicoAtendimentosVazio()) {
            System.out.println("Histórico de atendimentos vazio: nenhuma solicitação concluída.");
            return;
        }
        Atendimento[] itens = central.listarAtendimentosDireto();
        System.out.println("Atendimentos concluídos (do primeiro ao último):");
        imprimirAtendimentos(itens);
    }

    private void exibirAtendimentosInverso() {
        if (central.historicoAtendimentosVazio()) {
            System.out.println("Histórico de atendimentos vazio: nenhuma solicitação concluída.");
            return;
        }
        Atendimento[] itens = central.listarAtendimentosInverso();
        System.out.println("Atendimentos concluídos (do último ao primeiro):");
        imprimirAtendimentos(itens);
    }

    /**
     * Navegação pelo histórico usando o cursor da Lista Duplamente Encadeada.
     * É aqui que o encadeamento duplo mostra seu valor: [A] retrocede em O(1),
     * algo que a lista simples não conseguiria fazer sem percorrer tudo de novo.
     */
    private void navegarHistorico() {
        Atendimento atual = central.navegarParaPrimeiro();
        while (true) {
            System.out.println();
            System.out.println("--- Histórico navegável (registro "
                    + central.posicaoAtualNavegacao() + " de " + central.totalAtendimentos() + ") ---");
            System.out.println("Atendimento atual: " + atual);
            System.out.println((central.temAnteriorAtendimento() ? "[A] Anterior" : "[A] Anterior (indisponível)")
                    + "   " + (central.temProximoAtendimento() ? "[P] Próximo" : "[P] Próximo (indisponível)")
                    + "   [S] Sair");

            String comando = lerTextoObrigatorio("Comando: ");
            if (entradaEncerrada) return;
            comando = comando.trim().toUpperCase();

            if (comando.equals("S")) {
                System.out.println("Saindo da navegação.");
                return;
            } else if (comando.equals("A")) {
                if (central.temAnteriorAtendimento()) {
                    atual = central.retrocederAtendimento();
                } else {
                    System.out.println("Já está no primeiro registro.");
                }
            } else if (comando.equals("P")) {
                if (central.temProximoAtendimento()) {
                    atual = central.avancarAtendimento();
                } else {
                    System.out.println("Já está no último registro.");
                }
            } else {
                System.out.println("Comando inválido! Use A, P ou S.");
            }
        }
    }

    private void pesquisarAtendimento() {
        int codigo = lerInteiro("Código da solicitação atendida: ");
        if (entradaEncerrada) return;
        Atendimento a = central.buscarAtendimento(codigo);
        if (a == null) {
            System.out.println("Nenhum atendimento encontrado para a solicitação " + codigo + ".");
            return;
        }
        System.out.println("Atendimento encontrado:");
        System.out.println("  " + a);
    }

    // ===================== RELATÓRIOS =====================

    private void exibirResumo() {
        System.out.println("--- Resumo do sistema ---");
        System.out.println("Solicitações cadastradas (Lista Simples): " + central.totalCadastradas());
        System.out.println("  Aguardando atendimento: " + central.contarPorStatus(StatusSolicitacao.AGUARDANDO));
        System.out.println("  Concluídas            : " + central.contarPorStatus(StatusSolicitacao.CONCLUIDA));
        System.out.println("  Canceladas            : " + central.contarPorStatus(StatusSolicitacao.CANCELADA));
        System.out.println("Solicitações na fila de espera (Fila Encadeada): " + central.quantidadeAguardando());
        System.out.println("Operações no histórico recente (Pilha Encadeada): " + central.quantidadeOperacoes());
        System.out.println("Atendimentos concluídos (Lista Dupla): " + central.totalAtendimentos());
        if (!central.historicoAtendimentosVazio()) {
            System.out.println("  Primeiro atendimento: " + central.primeiroAtendimento());
            System.out.println("  Último atendimento  : " + central.ultimoAtendimento());
        }
    }

    // ===================== IMPRESSÃO =====================

    private void imprimirSolicitacoes(Solicitacao[] itens, boolean marcarPontas) {
        for (int i = 0; i < itens.length; i++) {
            String marcador = "          ";
            if (marcarPontas) {
                if (i == 0) marcador = "FRENTE -> ";
                else if (i == itens.length - 1) marcador = "FIM    -> ";
            }
            System.out.println(marcador + (i + 1) + ". " + itens[i]);
        }
    }

    private void imprimirAtendimentos(Atendimento[] itens) {
        for (int i = 0; i < itens.length; i++) {
            System.out.println("  " + (i + 1) + ". " + itens[i]);
        }
    }

    // ===================== LEITURA E VALIDAÇÃO DE ENTRADAS =====================

    /** Lê uma linha; se a entrada terminar, marca o encerramento e retorna null. */
    private String lerLinha(String mensagem) {
        System.out.print(mensagem);
        if (!scanner.hasNextLine()) {
            entradaEncerrada = true;
            System.out.println();
            return null;
        }
        return scanner.nextLine();
    }

    /** Lê um número inteiro, repetindo a pergunta enquanto a entrada for inválida. */
    private int lerInteiro(String mensagem) {
        while (true) {
            String linha = lerLinha(mensagem);
            if (linha == null) return 0;
            try {
                return Integer.parseInt(linha.trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite apenas números inteiros.");
            }
        }
    }

    private int lerInteiroNaFaixa(String mensagem, int min, int max) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (entradaEncerrada) return min;
            if (valor >= min && valor <= max) return valor;
            System.out.println("Valor fora da faixa! Digite um número de " + min + " a " + max + ".");
        }
    }

    /** Lê um texto não vazio, repetindo a pergunta enquanto estiver em branco. */
    private String lerTextoObrigatorio(String mensagem) {
        while (true) {
            String linha = lerLinha(mensagem);
            if (linha == null) return "";
            if (!linha.trim().isEmpty()) return linha.trim();
            System.out.println("Este campo é obrigatório e não pode ficar vazio.");
        }
    }
}
