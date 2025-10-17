# WePayU — Sistema de Folha de Pagamento

Projeto em **Java** para gerenciar empregados, registrar cartões de ponto, vendas, taxas de serviço, processar a **folha de pagamento** (com geração de relatório) e controlar **agendas de pagamento**.
Inclui suporte a **Undo/Redo** (desfazer/refazer) via Memento + Command.

---

## Funcionalidades Principais

As operações são expostas pela **`wepayu.command.Facade`**:

### Gerenciamento de Empregados

* `criarEmpregado` → Cria um novo empregado (**horista**, **assalariado** ou **comissionado**).
* `removerEmpregado` → Remove um empregado existente.
* `alteraEmpregado` → Altera atributos (nome, endereço, tipo, salário, comissão, sindicalização, método de pagamento, agenda).
* `getAtributoEmpregado` → Retorna o valor textual de um atributo específico.
* `getEmpregadoPorNome` → Busca o ID de empregado pelo nome (por posição no resultado).

### Lançamentos

* `lancaCartao` → Registra cartão de ponto (horas normais e extras; apenas para horistas).
* `lancaVenda` → Registra venda (apenas para comissionados).
* `lancaTaxaServico` → Registra taxa de serviço para um membro sindical.

### Folha de Pagamento

* `totalFolha` → Calcula o total bruto da folha em uma data (sem efetivar pagamentos).
* `rodaFolha` → Processa a folha do dia, gera arquivo `.txt` com as seções por tipo e atualiza estados internos (ex.: dívida sindical do horista).

### Agendas de Pagamento

* `criarAgendaDePagamentos` → Cria agendas personalizadas (validadas por regras), por exemplo:

  * `mensal $` (último dia útil do mês)
  * `mensal 15` (dia 15)
  * `semanal 5` (toda sexta)
  * `semanal 2 5` (a cada 2 semanas, sexta)
* Padrões por tipo:

  * Horista → `semanal 5`
  * Assalariado → `mensal $`
  * Comissionado → `semanal 2 5`
* Empregados podem mudar para qualquer agenda disponível.

### Undo/Redo

* `undo` → Desfaz o último comando que alterou estado.
* `redo` → Refaz a última operação desfeita.

---

## Regras de Negócio (resumo)

* **Horista**: pago às **sextas**; considera **Sáb..Sex**; horas > 8/dia são extras a **1,5×**.
* **Assalariado**: pago no **último dia útil** do mês.
* **Comissionado**: pago em **sextas alternadas**; recebe base proporcional + **comissão** das vendas do período.

**Agendas customizadas**:

* `mensal $` → último dia útil do mês
* `mensal N` → dia N do mês (1..28)
* `semanal [N] D` → a cada N semanas no dia da semana D (1=Seg..7=Dom), ancorado na contratação:

  * Horista: menor data de cartão de ponto
  * Assalariado/Comissionado: 2005-01-01

**Sindicato**:

* ID de membro **único**, taxa **diária** ≥ 0, e **taxas de serviço** por data.
* Descontos: taxa diária proporcional ao período + taxas de serviço do intervalo.

---

## Formatação e Arredondamento

* **Moeda**: vírgula como separador decimal, **2 casas**.
* **Datas (entrada)**: formato **estrito** `d/M/uuuu`.
* **Arredondamento**:

  * Proporções (ex.: mensal → quinzenal/semanal): regra típica **FLOOR** com 2 casas onde especificado.
  * Comissões: geralmente **FLOOR(2)** antes de somar ao total.
  * Exibição e totais do relatório: **HALF_UP(2)** salvo quando indicado no cálculo.

---

## Estrutura do Código

```
wepayu/
├─ command/
│  ├─ Facade.java              // API simplificada
│  ├─ Invoker.java             // histórico de comandos (undo/redo)
│  ├─ Command.java             // contrato de comandos
│  ├─ SnapshotCommand.java     // salva/rehidrata snapshots (Memento)
│  ├─ SistemaMemento.java      // snapshot do Sistema
│  ├─ ... Commands concretos (CriarEmpregadoCommand, RemoverEmpregadoCommand, etc.)
├─ services/
│  └─ Sistema.java             // núcleo de negócios (cálculos, agendas, lançamentos)
├─ models/
│  ├─ Empregado.java           // base
│  ├─ Horista.java
│  ├─ Assalariado.java
│  ├─ Comissionado.java
│  ├─ CartaoDePonto.java
│  ├─ ResultadoDeVenda.java
│  ├─ MembroSindicato.java
│  └─ TaxaServico.java
└─ exceptions/
   └─ ... exceções de domínio (validações, estados inválidos, etc.)
```

---

## ⚙️ Como Executar

1. **Pré-requisitos**: Instalar o **JDK** (Java Development Kit).

2. **Testes de Aceitação**:

   * Execute a classe `Main.java`.
   * O projeto usa **EasyAccept** (`easyaccept.jar`) para rodar os testes em `tests/`.

3. **Relatórios**:

   * Resultados da folha de pagamento são gerados em arquivos `.txt`.


---

## Validações e Exceções

* Campos obrigatórios, tipos corretos e valores **não negativos** (salário, comissão, horas, taxas).
* Operações coerentes com o tipo (ex.: cartão de ponto apenas para horista).
* **IDs sindicais únicos** entre sindicalizados.
* Datas no formato estrito `d/M/uuuu`.
* Quando o sistema está “encerrado”, comandos de ação disparam a exceção específica (ex.: `NaoPodeComandosAposEncerrarSistemaException`).
* Demais erros são sinalizados por exceções em `wepayu.exceptions`.

---

## Notas

* **Undo/Redo**: apenas comandos que **alteram estado** criam checkpoints; ao aplicar um novo comando após `undo`, a pilha de `redo` é limpa (histórico linear).
* **Âncoras de agenda semanal**: a primeira ocorrência conta a partir da “data de contratação” definida nas regras.
* **Formatação**: todos os valores exibidos com vírgula decimal e 2 casas.

---

## Sobre

Projeto desenvolvido para a disciplina Programação Orientada a Objetos (Programação 2) do curso de Ciência da Computação - UFAL. Professor: Mario Hozano.
