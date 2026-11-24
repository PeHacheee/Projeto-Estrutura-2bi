# Central de Atendimento – Projeto Integrador EDI (2º Bimestre)

Universidade Presbiteriana Mackenzie – Estruturas de Dados I – 3º CC

Integrantes:
- Caio Borges Ribas – RA 10427969
- Pedro Henrique Marques Ferreira – RA 10723681

Sistema de Atendimento e Gerenciamento de Serviços em Java. No 2º bimestre, as
estruturas em vetor foram substituídas por versões **genéricas, dinâmicas e
encadeadas**, e foram incorporadas a Lista Simples e a Lista Duplamente Encadeada.

## As quatro estruturas e seu uso no sistema

| Estrutura | Classe | Para que serve |
|---|---|---|
| Fila encadeada | `LinkedQueue` sobre `SLinkedList` | Solicitações aguardando atendimento (FIFO) |
| Pilha encadeada | `LinkedStack` sobre `SLinkedList` | Histórico recente de operações e Desfazer (LIFO) |
| Lista simples | `SLinkedList` | Cadastro geral: busca por código, status e categoria; remoção |
| Lista dupla | `DLinkedList` (com sentinelas) | Histórico navegável de atendimentos concluídos |

Fluxo: nova solicitação → cadastro geral + fila de espera → atender → pilha de
operações + histórico de atendimentos.

## Estrutura do projeto

```
src/
├── app/Main.java                      ponto de entrada
├── model/                             Solicitacao, Atendimento, Operacao,
│                                      StatusSolicitacao, TipoOperacao
├── service/CentralAtendimento.java    regras de negócio (única classe que usa as estruturas)
├── estruturas/                        código do professor:
│   ├── Node.java, SLinkedList.java, MyLinkedList.java      (Aula 5)
│   ├── DNode.java, DLinkedList.java                        (Aula 6)
│   ├── LinkedStack.java, LinkedQueue.java                  (Aula 7)
│   ├── Stack.java, MyQueue.java                            (Aulas 2 e 3 – interfaces)
│   └── ArrayStack.java, CircularArrayQueue.java            (1º bimestre – só comparação)
├── ui/Menu.java                       menu textual com 18 opções
└── testes/TestesEstruturas.java       64 testes automatizados (sem JUnit)
```

`ArrayStack` e `CircularArrayQueue` permanecem apenas para comparação didática,
como permite a seção 27 do enunciado. A aplicação final **não as utiliza**: elas
aparecem somente nos testes T63 e T64, que comprovam a diferença entre as duas
estratégias de armazenamento.

As interfaces `Stack` e `MyQueue` são as mesmas do 1º bimestre — a troca da
implementação não exigiu nenhuma mudança no `Menu` nem na `CentralAtendimento`.

## Requisitos

JDK 17 ou superior. Nenhuma biblioteca externa. Nenhuma classe da Java Collections
Framework é usada.

## Como executar no VS Code / GitHub Codespaces

1. Abra a pasta do projeto (com a extensão *Extension Pack for Java* instalada).
2. Na aba **Run and Debug**, escolha **Sistema (app.Main)** ou
   **Testes (testes.TestesEstruturas)** e clique em ▶.

## Como executar pelo terminal (na pasta do projeto)

Linux / macOS / Codespaces:
```
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out app.Main
java -cp out testes.TestesEstruturas
```

Windows (PowerShell):
```
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out app.Main
java -cp out testes.TestesEstruturas
```

Se os acentos aparecerem errados no terminal do Windows, execute `chcp 65001` antes.
