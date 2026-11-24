package testes;

import estruturas.ArrayStack;
import estruturas.CircularArrayQueue;
import estruturas.DLinkedList;
import estruturas.LinkedQueue;
import estruturas.LinkedStack;
import estruturas.SLinkedList;
import model.Atendimento;
import model.Operacao;
import model.Solicitacao;
import model.StatusSolicitacao;
import model.TipoOperacao;
import service.CentralAtendimento;

/**
 * Testes automatizados (sem JUnit) das estruturas ENCADEADAS do 2º bimestre e
 * da integração do sistema.
 *
 * Blocos:
 *  - Pilha Encadeada  (T01-T09)
 *  - Fila Encadeada   (T10-T18)
 *  - Lista Simples    (T19-T32)
 *  - Lista Dupla      (T33-T47)
 *  - Integração       (T48-T65)
 *  - Vetor x Encadeada (T66-T67): comprova a diferença entre as duas estratégias.
 *
 * Cada teste imprime ID, estrutura, situação, esperado, obtido e APROVADO/REPROVADO.
 */
public class TestesEstruturas {
    private static int total = 0;
    private static int aprovados = 0;

    public static void main(String[] args) {
        System.out.println("=== TESTES AUTOMATIZADOS - CENTRAL DE ATENDIMENTO (2º BIMESTRE) ===");
        testesPilhaEncadeada();
        testesFilaEncadeada();
        testesListaSimples();
        testesListaDupla();
        testesIntegracao();
        testesComparacao();
        int reprovados = total - aprovados;
        System.out.println();
        System.out.println("=== RESULTADO CONSOLIDADO ===");
        System.out.println("Total de testes executados: " + total);
        System.out.println("Testes aprovados: " + aprovados);
        System.out.println("Testes reprovados: " + reprovados);
        System.out.printf("Percentual de aprovação: %.1f%%%n", total == 0 ? 0.0 : aprovados * 100.0 / total);
    }

    // ===================== PILHA ENCADEADA =====================

    private static void testesPilhaEncadeada() {
        System.out.println();
        System.out.println("--- PILHA GENÉRICA ENCADEADA (alocação dinâmica) ---");

        LinkedStack<String> p = new LinkedStack<>();
        verificar("T01", "Pilha", "Pilha recém-criada está vazia", "new LinkedStack()",
                "isEmpty=true, tamanho=0", "isEmpty=" + p.isEmpty() + ", tamanho=" + p.size());

        p.push("A");
        verificar("T02", "Pilha", "Empilhamento do primeiro elemento (nó vira o topo)", "push(A)",
                "topo=A, tamanho=1", "topo=" + p.top() + ", tamanho=" + p.size());

        p.push("B"); p.push("C");
        verificar("T03", "Pilha", "Empilhamento de vários elementos (ordem LIFO)", "push A, B, C",
                "topo=C, tamanho=3", "topo=" + p.top() + ", tamanho=" + p.size());

        verificar("T04", "Pilha", "Consulta ao topo sem remover", "top() duas vezes",
                "C e C, tamanho=3", p.top() + " e " + p.top() + ", tamanho=" + p.size());

        verificar("T05", "Pilha", "Desempilhamento (o topo avança para o próximo nó)", "pop()",
                "removido=C, novo topo=B, tamanho=2",
                "removido=" + p.pop() + ", novo topo=" + p.top() + ", tamanho=" + p.size());

        LinkedStack<String> suc = new LinkedStack<>();
        suc.push("A"); suc.push("B"); suc.push("C");
        verificar("T06", "Pilha", "Desempilhamentos sucessivos (LIFO)", "push A, B, C",
                "C, B, A", suc.pop() + ", " + suc.pop() + ", " + suc.pop());

        verificar("T07", "Pilha", "Esvaziamento completo", "após desempilhar os 3",
                "isEmpty=true, tamanho=0", "isEmpty=" + suc.isEmpty() + ", tamanho=" + suc.size());

        verificar("T08", "Pilha", "Consulta e remoção em pilha vazia", "top() e pop() com pilha vazia",
                "Exceção: Pilha vazia / Exceção: Pilha vazia",
                erro(() -> suc.top()) + " / " + erro(() -> suc.pop()));

        LinkedStack<String> grande = new LinkedStack<>();
        for (int i = 1; i <= 1000; i++) grande.push("op" + i);
        verificar("T09", "Pilha", "Crescimento dinâmico sem limite de capacidade", "1000 push seguidos",
                "sem erro, tamanho=1000, topo=op1000",
                "sem erro, tamanho=" + grande.size() + ", topo=" + grande.top());
    }

    // ===================== FILA ENCADEADA =====================

    private static void testesFilaEncadeada() {
        System.out.println();
        System.out.println("--- FILA GENÉRICA ENCADEADA (alocação dinâmica) ---");

        LinkedQueue<Solicitacao> f = new LinkedQueue<>();
        verificar("T10", "Fila", "Fila recém-criada está vazia", "new LinkedQueue()",
                "isEmpty=true, tamanho=0", "isEmpty=" + f.isEmpty() + ", tamanho=" + f.size());

        f.enqueue(sol(101));
        verificar("T11", "Fila", "Inserção do primeiro elemento (início e fim no mesmo nó)", "enqueue(101)",
                "frente=101, tamanho=1", "frente=" + f.front().getCodigo() + ", tamanho=" + f.size());

        f.enqueue(sol(102)); f.enqueue(sol(103));
        verificar("T12", "Fila", "Inserção de vários elementos no fim", "enqueue 101, 102, 103",
                "frente=101, tamanho=3", "frente=" + f.front().getCodigo() + ", tamanho=" + f.size());

        verificar("T13", "Fila", "Consulta ao primeiro sem remover", "front() duas vezes",
                "101 e 101, tamanho=3",
                f.front().getCodigo() + " e " + f.front().getCodigo() + ", tamanho=" + f.size());

        verificar("T14", "Fila", "Remoção do primeiro elemento", "dequeue()",
                "removido=101, nova frente=102, tamanho=2",
                "removido=" + f.dequeue().getCodigo() + ", nova frente=" + f.front().getCodigo()
                        + ", tamanho=" + f.size());

        LinkedQueue<Solicitacao> ordem = new LinkedQueue<>();
        ordem.enqueue(sol(101)); ordem.enqueue(sol(102)); ordem.enqueue(sol(103));
        verificar("T15", "Fila", "Ordem FIFO em remoções sucessivas", "enqueue 101, 102, 103",
                "101, 102, 103", ordem.dequeue().getCodigo() + ", " + ordem.dequeue().getCodigo()
                        + ", " + ordem.dequeue().getCodigo());

        verificar("T16", "Fila", "Esvaziamento completo (início e fim voltam a null)", "após remover os 3",
                "isEmpty=true, tamanho=0", "isEmpty=" + ordem.isEmpty() + ", tamanho=" + ordem.size());

        verificar("T17", "Fila", "Consulta e remoção em fila vazia", "front() e dequeue() com fila vazia",
                "Exceção: Fila vazia / Exceção: Fila vazia",
                erro(() -> ordem.front()) + " / " + erro(() -> ordem.dequeue()));

        LinkedQueue<Solicitacao> reuso = new LinkedQueue<>();
        reuso.enqueue(sol(201)); reuso.dequeue(); reuso.enqueue(sol(202));
        verificar("T18", "Fila", "Reutilização após esvaziar (fim reatribuído corretamente)",
                "enqueue 201, dequeue, enqueue 202",
                "frente=202, tamanho=1", "frente=" + reuso.front().getCodigo() + ", tamanho=" + reuso.size());
    }

    // ===================== LISTA SIMPLES =====================

    private static void testesListaSimples() {
        System.out.println();
        System.out.println("--- LISTA ENCADEADA SIMPLES GENÉRICA ---");

        SLinkedList<Solicitacao> l = new SLinkedList<>();
        verificar("T19", "Lista Simples", "Lista recém-criada está vazia", "new SLinkedList()",
                "isEmpty=true, tamanho=0", "isEmpty=" + l.isEmpty() + ", tamanho=" + l.size());

        verificar("T20", "Lista Simples", "Consulta em lista vazia", "primeiro() e obter(0)",
                "Exceção: Lista vazia / Exceção: Lista vazia",
                erro(() -> l.first()) + " / " + erro(() -> l.get(0)));

        l.addLast(sol(101));
        verificar("T21", "Lista Simples", "Lista com um único elemento", "inserirFim(101)",
                "primeiro=101, ultimo=101, tamanho=1",
                "primeiro=" + l.first().getCodigo() + ", ultimo=" + l.last().getCodigo()
                        + ", tamanho=" + l.size());

        l.addLast(sol(102)); l.addLast(sol(103));
        verificar("T22", "Lista Simples", "Inserção no fim mantém a ordem de cadastro", "inserirFim 102 e 103",
                "códigos 101, 102, 103", codigos(vetorDe(l)));

        l.addFirst(sol(100));
        verificar("T23", "Lista Simples", "Inserção no início", "inserirInicio(100)",
                "códigos 100, 101, 102, 103", codigos(vetorDe(l)));

        l.add(2, sol(150));
        verificar("T24", "Lista Simples", "Inserção em posição intermediária", "inserirNaPosicao(2, 150)",
                "códigos 100, 101, 150, 102, 103", codigos(vetorDe(l)));

        verificar("T25", "Lista Simples", "Consulta por posição e busca por conteúdo", "obter(0) e indiceDe(150)",
                "obter(0)=100, indice=2",
                "obter(0)=" + l.get(0).getCodigo() + ", indice=" + l.indexOf(vetorDe(l)[2]));

        verificar("T26", "Lista Simples", "Elemento inexistente", "indiceDe(solicitação fora da lista)",
                "indice=-1, contem=false",
                "indice=" + l.indexOf(sol(999)) + ", contem=" + l.contains(sol(999)));

        verificar("T27", "Lista Simples", "Posição inválida", "obter(99) em lista de 5 elementos",
                "Exceção: Posição inválida: 99", erro(() -> l.get(99)));

        SLinkedList<Solicitacao> rem = new SLinkedList<>();
        for (int c : new int[]{101, 102, 103, 104}) rem.addLast(sol(c));
        rem.removeAt(0);
        verificar("T28", "Lista Simples", "Remoção do primeiro", "removerPosicao(0) em 101-104",
                "códigos 102, 103, 104; primeiro=102",
                codigos(vetorDe(rem)) + "; primeiro=" + rem.first().getCodigo());

        rem.removeAt(1);
        verificar("T29", "Lista Simples", "Remoção intermediária (religa anterior e próximo)", "removerPosicao(1)",
                "códigos 102, 104, tamanho=2", codigos(vetorDe(rem)) + ", tamanho=" + rem.size());

        rem.removeAt(rem.size() - 1);
        verificar("T30", "Lista Simples", "Remoção do último (o fim recua)", "removerPosicao(último)",
                "códigos 102, ultimo=102", codigos(vetorDe(rem)) + ", ultimo=" + rem.last().getCodigo());

        rem.removeAt(0);
        verificar("T31", "Lista Simples", "Remoção do único elemento restante", "removerPosicao(0)",
                "isEmpty=true, tamanho=0",
                "isEmpty=" + rem.isEmpty() + ", tamanho=" + rem.size());

        SLinkedList<Solicitacao> busca = new SLinkedList<>();
        Solicitacao alvo = sol(202);
        busca.addLast(sol(201)); busca.addLast(alvo); busca.addLast(sol(203));
        boolean removeu = busca.remove(alvo);
        boolean removeuInexistente = busca.remove(sol(999));
        verificar("T32", "Lista Simples", "Remoção por conteúdo (existente e inexistente)", "remover(202) e remover(999)",
                "true e false; códigos 201, 203",
                removeu + " e " + removeuInexistente + "; " + codigos(vetorDe(busca)));
    }

    // ===================== LISTA DUPLA =====================

    private static void testesListaDupla() {
        System.out.println();
        System.out.println("--- LISTA DUPLAMENTE ENCADEADA GENÉRICA ---");

        DLinkedList<Atendimento> d = new DLinkedList<>();
        verificar("T33", "Lista Dupla", "Lista recém-criada está vazia", "new DLinkedList()",
                "isEmpty=true, tamanho=0", "isEmpty=" + d.isEmpty() + ", tamanho=" + d.size());

        verificar("T34", "Lista Dupla", "Consulta e navegação em lista vazia", "primeiro() e irParaPrimeiro()",
                "Exceção: Lista vazia / Exceção: Lista vazia",
                erro(() -> d.first()) + " / " + erro(() -> d.irParaPrimeiro()));

        d.addLast(atend(101));
        verificar("T35", "Lista Dupla", "Lista com um único elemento", "inserirFim(101)",
                "primeiro=101, ultimo=101, tamanho=1",
                "primeiro=" + d.first().getCodigoSolicitacao() + ", ultimo="
                        + d.last().getCodigoSolicitacao() + ", tamanho=" + d.size());

        d.addLast(atend(103)); d.addLast(atend(105)); d.addLast(atend(109));
        verificar("T36", "Lista Dupla", "Percurso do início para o fim", "inserirFim 103, 105, 109",
                "códigos 101, 103, 105, 109", codigosAtend(diretoDe(d)));

        verificar("T37", "Lista Dupla", "Percurso do fim para o início (encadeamento duplo)", "percurso inverso",
                "códigos 109, 105, 103, 101", codigosAtend(inversoDe(d)));

        verificar("T38", "Lista Dupla", "Consulta ao primeiro e ao último", "primeiro() e ultimo()",
                "primeiro=101, ultimo=109",
                "primeiro=" + d.first().getCodigoSolicitacao()
                        + ", ultimo=" + d.last().getCodigoSolicitacao());

        verificar("T39", "Lista Dupla", "Localizar atendimento existente e inexistente", "indiceDe(105) e contem(999)",
                "indice=2, contem=false",
                "indice=" + d.indexOf(diretoDe(d)[2]) + ", contem=" + d.contains(atend(999)));

        d.irParaPrimeiro();
        String nav = String.valueOf(d.atual().getCodigoSolicitacao());
        nav += " -> " + d.avancar().getCodigoSolicitacao();
        nav += " -> " + d.avancar().getCodigoSolicitacao();
        verificar("T40", "Lista Dupla", "Navegação para o registro seguinte [P]", "irParaPrimeiro, avancar, avancar",
                "101 -> 103 -> 105", nav);

        String volta = String.valueOf(d.retroceder().getCodigoSolicitacao());
        volta += " -> " + d.retroceder().getCodigoSolicitacao();
        verificar("T41", "Lista Dupla", "Navegação para o registro anterior [A]", "retroceder, retroceder",
                "103 -> 101", volta);

        verificar("T42", "Lista Dupla", "Limite inicial da navegação", "retroceder no primeiro registro",
                "temAnterior=false, Exceção: Já está no primeiro registro",
                "temAnterior=" + d.temAnterior() + ", " + erro(() -> d.retroceder()));

        d.irParaUltimo();
        verificar("T43", "Lista Dupla", "Limite final da navegação", "avancar no último registro",
                "atual=109, temProximo=false, Exceção: Já está no último registro",
                "atual=" + d.atual().getCodigoSolicitacao() + ", temProximo=" + d.temProximo()
                        + ", " + erro(() -> d.avancar()));

        d.irParaPrimeiro();
        d.avancar();
        verificar("T44", "Lista Dupla", "Posição do cursor durante a navegação", "irParaPrimeiro, avancar",
                "posicao=1 (segundo registro)", "posicao=" + d.posicaoDoCursor() + " (segundo registro)");

        DLinkedList<Atendimento> rem = new DLinkedList<>();
        Atendimento a1 = atend(101), a2 = atend(102), a3 = atend(103);
        rem.addLast(a1); rem.addLast(a2); rem.addLast(a3);
        rem.remove(a2);
        verificar("T45", "Lista Dupla", "Remoção intermediária (religa os dois vizinhos)", "remover(102)",
                "direto 101, 103; inverso 103, 101",
                "direto " + codigosAtend(diretoDe(rem)).substring(8) + "; inverso "
                        + codigosAtend(inversoDe(rem)).substring(8));

        rem.remove(a1);
        verificar("T46", "Lista Dupla", "Remoção do primeiro (o início avança)", "remover(101)",
                "códigos 103, primeiro=103, ultimo=103",
                codigosAtend(diretoDe(rem)) + ", primeiro=" + rem.first().getCodigoSolicitacao()
                        + ", ultimo=" + rem.last().getCodigoSolicitacao());

        rem.remove(a3);
        verificar("T47", "Lista Dupla", "Remoção do último elemento restante", "remover(103)",
                "isEmpty=true, tamanho=0",
                "isEmpty=" + rem.isEmpty() + ", tamanho=" + rem.size());
    }

    // ===================== INTEGRAÇÃO =====================

    private static void testesIntegracao() {
        System.out.println();
        System.out.println("--- INTEGRAÇÃO DO SISTEMA (CentralAtendimento com as 4 estruturas) ---");

        CentralAtendimento c = new CentralAtendimento();
        Solicitacao s1 = c.cadastrarSolicitacao("Ana", "Impressora sem tinta", "Hardware", 3);
        verificar("T48", "Sistema", "Cadastro insere na Lista Simples, na Fila e na Pilha",
                "Ana / Hardware / prioridade 3",
                "código=101, status=AGUARDANDO, cadastro=1, fila=1, topo=CADASTRO 101",
                "código=" + s1.getCodigo() + ", status=" + s1.getStatus()
                        + ", cadastro=" + c.totalCadastradas() + ", fila=" + c.quantidadeAguardando()
                        + ", topo=" + c.consultarUltimaOperacao().getTipo() + " "
                        + c.consultarUltimaOperacao().getSolicitacao().getCodigo());

        c.cadastrarSolicitacao("Bruno", "Sem acesso ao e-mail", "Software", 5);
        c.cadastrarSolicitacao("Clara", "Rede lenta", "Rede", 2);
        verificar("T49", "Sistema", "Código automático e sequencial", "três cadastros",
                "códigos 101, 102, 103", codigos(c.listarCadastroGeral()));

        verificar("T50", "Sistema", "Consulta da próxima (FIFO) sem remover", "opção 2",
                "101, fila=3", c.consultarProxima().getCodigo() + ", fila=" + c.quantidadeAguardando());

        Atendimento at = c.atenderProxima("Carlos");
        verificar("T51", "Sistema", "Atendimento registra na Pilha e na Lista Dupla", "responsável Carlos",
                "atendida=101, status=CONCLUIDA, fila=2, atendimentos=1, topo=ATENDIMENTO 101",
                "atendida=" + at.getCodigoSolicitacao() + ", status=" + at.getSolicitacao().getStatus()
                        + ", fila=" + c.quantidadeAguardando() + ", atendimentos=" + c.totalAtendimentos()
                        + ", topo=" + c.consultarUltimaOperacao().getTipo() + " "
                        + c.consultarUltimaOperacao().getSolicitacao().getCodigo());

        verificar("T52", "Sistema", "Exibições não alteram as estruturas", "listar fila e cadastro duas vezes",
                "102, 103 | 101, 102, 103 | fila=2, cadastro=3",
                codigos(c.listarFila()).substring(8) + " | " + codigos(c.listarCadastroGeral()).substring(8)
                        + " | fila=" + c.listarFila().length + ", cadastro=" + c.listarCadastroGeral().length);

        verificar("T53", "Sistema", "Busca por código (existente e inexistente)", "buscar 102 e 999",
                "102 e null",
                c.buscarPorCodigo(102).getCodigo() + " e " + c.buscarPorCodigo(999));

        verificar("T54", "Sistema", "Busca por status", "AGUARDANDO e CONCLUIDA",
                "aguardando=códigos 102, 103; concluidas=códigos 101",
                "aguardando=" + codigos(c.buscarPorStatus(StatusSolicitacao.AGUARDANDO))
                        + "; concluidas=" + codigos(c.buscarPorStatus(StatusSolicitacao.CONCLUIDA)));

        verificar("T55", "Sistema", "Busca por categoria (sem diferenciar maiúsculas)", "categoria \"rede\"",
                "códigos 103", codigos(c.buscarPorCategoria("rede")));

        Operacao[] hist = c.listarHistorico();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hist.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(hist[i].getTipo()).append(" ").append(hist[i].getSolicitacao().getCodigo());
        }
        verificar("T56", "Sistema", "Histórico de operações em ordem inversa (topo -> base)", "opção 7",
                "ATENDIMENTO 101, CADASTRO 103, CADASTRO 102, CADASTRO 101; pilha mantém 4",
                sb + "; pilha mantém " + c.quantidadeOperacoes());

        // Remoção de solicitação (opção 11) e seu desfazer
        c.removerSolicitacao(103);
        verificar("T57", "Sistema", "Remover solicitação tira da Lista Simples e da Fila",
                "remover 103 (estava aguardando)",
                "cadastro=códigos 101, 102; fila=códigos 102; topo=CANCELAMENTO 103",
                "cadastro=" + codigos(c.listarCadastroGeral()) + "; fila=" + codigos(c.listarFila())
                        + "; topo=" + c.consultarUltimaOperacao().getTipo() + " "
                        + c.consultarUltimaOperacao().getSolicitacao().getCodigo());

        c.desfazerUltimaOperacao();
        verificar("T58", "Sistema", "Desfazer CANCELAMENTO restaura cadastro, fila e status",
                "opção 8 (topo = CANCELAMENTO 103)",
                "cadastro=códigos 101, 102, 103; fila=códigos 102, 103; status=AGUARDANDO",
                "cadastro=" + codigos(c.listarCadastroGeral()) + "; fila=" + codigos(c.listarFila())
                        + "; status=" + c.buscarPorCodigo(103).getStatus());

        c.desfazerUltimaOperacao();   // topo agora é o ATENDIMENTO 101
        verificar("T59", "Sistema", "Desfazer ATENDIMENTO devolve à FRENTE da fila e limpa a Lista Dupla",
                "opção 8 (topo = ATENDIMENTO 101)",
                "fila=códigos 101, 102, 103; status=AGUARDANDO; responsável=null; atendimentos=0",
                "fila=" + codigos(c.listarFila()) + "; status=" + c.buscarPorCodigo(101).getStatus()
                        + "; responsável=" + c.buscarPorCodigo(101).getResponsavel()
                        + "; atendimentos=" + c.totalAtendimentos());

        c.desfazerUltimaOperacao();   // topo agora é o CADASTRO 103
        verificar("T60", "Sistema", "Desfazer CADASTRO remove da Fila e do cadastro geral",
                "opção 8 (topo = CADASTRO 103)",
                "cadastro=códigos 101, 102; fila=códigos 101, 102; busca 103=null",
                "cadastro=" + codigos(c.listarCadastroGeral()) + "; fila=" + codigos(c.listarFila())
                        + "; busca 103=" + c.buscarPorCodigo(103));

        CentralAtendimento vazia = new CentralAtendimento();
        verificar("T61", "Sistema", "Operações com o sistema vazio", "opções 2, 3, 6, 8 e 14 sem dados",
                "fila vazia / fila vazia / histórico vazio / histórico vazio / atendimentos vazio",
                resumo(erro(() -> vazia.consultarProxima())) + " / " + resumo(erro(() -> vazia.atenderProxima("X")))
                        + " / " + resumo(erro(() -> vazia.consultarUltimaOperacao()))
                        + " / " + resumo(erro(() -> vazia.desfazerUltimaOperacao()))
                        + " / " + resumo(erro(() -> vazia.primeiroAtendimento())));

        verificar("T62", "Sistema", "Validações do cadastro", "prioridade 0, prioridade 6 e nome vazio",
                "faixa 1 a 5 / faixa 1 a 5 / campo vazio; cadastro inalterado",
                resumoFaixa(erro(() -> vazia.cadastrarSolicitacao("Ana", "x", "y", 0))) + " / "
                        + resumoFaixa(erro(() -> vazia.cadastrarSolicitacao("Ana", "x", "y", 6))) + " / "
                        + resumoVazio(erro(() -> vazia.cadastrarSolicitacao("  ", "x", "y", 3)))
                        + "; cadastro " + (vazia.cadastroVazio() ? "inalterado" : "alterado"));

        // Remoção de registro na Lista Dupla, quando permitido pelas regras:
        // remover uma solicitação já atendida tira também o atendimento do histórico.
        CentralAtendimento cr = new CentralAtendimento();
        cr.cadastrarSolicitacao("Ana", "Impressora", "Hardware", 3);    // 101
        cr.cadastrarSolicitacao("Bruno", "E-mail", "Software", 4);      // 102
        cr.cadastrarSolicitacao("Clara", "Rede", "Rede", 2);            // 103
        cr.atenderProxima("Carlos");   // 101 concluída
        cr.atenderProxima("Daniela");  // 102 concluída
        cr.atenderProxima("Eduardo");  // 103 concluída
        cr.removerSolicitacao(102);    // remove a do MEIO do histórico
        verificar("T63", "Sistema", "Remover solicitação atendida tira o registro da Lista Dupla",
                "remover 102, que já estava concluída",
                "cadastro=códigos 101, 103; histórico=códigos 101, 103; total=2",
                "cadastro=" + codigos(cr.listarCadastroGeral())
                        + "; histórico=" + codigosAtend(cr.listarAtendimentosDireto())
                        + "; total=" + cr.totalAtendimentos());

        cr.desfazerUltimaOperacao();
        verificar("T64", "Sistema", "Desfazer devolve o registro à posição original da Lista Dupla",
                "opção 8 (topo = CANCELAMENTO 102)",
                "direto 101, 102, 103; inverso 103, 102, 101; status=CONCLUIDA",
                "direto " + codigosAtend(cr.listarAtendimentosDireto()).substring(8)
                        + "; inverso " + codigosAtend(cr.listarAtendimentosInverso()).substring(8)
                        + "; status=" + cr.buscarPorCodigo(102).getStatus());

        verificar("T65", "Sistema", "Consistência entre cadastro geral e histórico de atendimentos",
                "após remover e desfazer",
                "concluídas=3, atendimentos=3, busca 102=encontrado",
                "concluídas=" + cr.contarPorStatus(StatusSolicitacao.CONCLUIDA)
                        + ", atendimentos=" + cr.totalAtendimentos()
                        + ", busca 102=" + (cr.buscarAtendimento(102) != null ? "encontrado" : "ausente"));
    }

    // ===================== VETOR x ENCADEADA =====================

    private static void testesComparacao() {
        System.out.println();
        System.out.println("--- COMPARAÇÃO: VETOR (1º BIMESTRE) x ENCADEADA (2º BIMESTRE) ---");

        ArrayStack<String> pilhaVetor = new ArrayStack<>(3);
        pilhaVetor.push("a"); pilhaVetor.push("b"); pilhaVetor.push("c");
        LinkedStack<String> pilhaEnc = new LinkedStack<>();
        for (int i = 0; i < 4; i++) pilhaEnc.push("x" + i);
        verificar("T66", "Comparação", "Capacidade fixa (vetor) x crescimento dinâmico (encadeada)",
                "4º push em pilha de capacidade 3 e em pilha encadeada",
                "vetor: Exceção: Pilha cheia | encadeada: sem erro, tamanho=4",
                "vetor: " + erro(() -> pilhaVetor.push("d")) + " | encadeada: sem erro, tamanho=" + pilhaEnc.size());

        CircularArrayQueue<String> filaVetor = new CircularArrayQueue<>(2);
        filaVetor.enqueue("a"); filaVetor.enqueue("b");
        LinkedQueue<String> filaEnc = new LinkedQueue<>();
        filaEnc.enqueue("a"); filaEnc.enqueue("b"); filaEnc.enqueue("c");
        verificar("T67", "Comparação", "Mesmo contrato (Stack/MyQueue), implementações diferentes",
                "3º enqueue em fila de capacidade 2 e em fila encadeada",
                "vetor: Exceção: Fila cheia, isFull=true | encadeada: sem erro, tamanho=3, frente=a",
                "vetor: " + erro(() -> filaVetor.enqueue("c")) + ", isFull=" + filaVetor.isFull()
                        + " | encadeada: sem erro, tamanho=" + filaEnc.size() + ", frente=" + filaEnc.front());
    }

    // ===================== AUXILIARES DOS TESTES =====================

    private static void verificar(String id, String estrutura, String situacao, String entrada,
                                  String esperado, String obtido) {
        total++;
        boolean ok = esperado.equals(obtido);
        if (ok) aprovados++;
        System.out.println();
        System.out.println(id + " [" + estrutura + "] " + situacao);
        System.out.println("   Entrada : " + entrada);
        System.out.println("   Esperado: " + esperado);
        System.out.println("   Obtido  : " + obtido);
        System.out.println("   Situação: " + (ok ? "APROVADO" : "REPROVADO"));
    }

    /** Executa uma ação e devolve a mensagem da exceção lançada (ou "sem erro"). */
    private static String erro(Runnable acao) {
        try {
            acao.run();
            return "sem erro";
        } catch (RuntimeException e) {
            return "Exceção: " + e.getMessage();
        }
    }

    private static String resumo(String msgErro) {
        if (msgErro.equals("sem erro")) return msgErro;
        String m = msgErro.toLowerCase();
        if (m.contains("fila vazia")) return "fila vazia";
        if (m.contains("histórico vazio")) return "histórico vazio";
        if (m.contains("histórico de atendimentos vazio")) return "atendimentos vazio";
        return msgErro;
    }

    private static String resumoFaixa(String msgErro) {
        return msgErro.contains("entre 1 e 5") ? "faixa 1 a 5" : msgErro;
    }

    private static String resumoVazio(String msgErro) {
        return msgErro.contains("não pode ser vazio") ? "campo vazio" : msgErro;
    }

    private static Solicitacao sol(int codigo) {
        return new Solicitacao(codigo, "Teste", "Descrição", "Categoria", 1);
    }

    private static Atendimento atend(int codigo) {
        return new Atendimento(sol(codigo), "Responsável");
    }

    private static Solicitacao[] vetorDe(SLinkedList<Solicitacao> lista) {
        Solicitacao[] v = new Solicitacao[lista.size()];
        lista.copiarPara(v);
        return v;
    }

    private static Atendimento[] diretoDe(DLinkedList<Atendimento> lista) {
        Atendimento[] v = new Atendimento[lista.size()];
        lista.copiarDireto(v);
        return v;
    }

    private static Atendimento[] inversoDe(DLinkedList<Atendimento> lista) {
        Atendimento[] v = new Atendimento[lista.size()];
        lista.copiarInverso(v);
        return v;
    }

    private static String codigos(Solicitacao[] v) {
        StringBuilder sb = new StringBuilder("códigos ");
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(v[i].getCodigo());
        }
        return sb.toString();
    }

    private static String codigosAtend(Atendimento[] v) {
        StringBuilder sb = new StringBuilder("códigos ");
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(v[i].getCodigoSolicitacao());
        }
        return sb.toString();
    }
}
