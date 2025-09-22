package wepayu.models;

/**
 * Classe abstrata que representa um Empregado, a entidade base do sistema de folha de pagamento.
 * <p>
 * Define os atributos e comportamentos comuns a todos os tipos de empregados,
 * como nome, endereço, ID, filiação sindical e método de pagamento.
 * Esta classe deve ser estendida por tipos de empregados específicos.
 *
 * @author davi
 * @see Horista
 * @see Assalariado
 */
public abstract class Empregado implements Cloneable {
    private String nome;
    private String endereco;
    private String tipo;
    private String id;
    private MembroSindicato sindicato;
    private String metodoPagamento = "emMaos";
    private String banco;
    private String agencia;
    private String contaCorrente;

    /**
     * Construtor base para inicializar um novo empregado.
     *
     * @param nome     O nome completo do empregado.
     * @param endereco O endereço residencial do empregado.
     * @param tipo     A classificação do empregado (ex: "horista", "assalariado").
     * @param id       O identificador único do empregado.
     */
    public Empregado(String nome, String endereco, String tipo, String id) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.id = id;
        this.sindicato = null;
    }

    /**
     * Cria e retorna uma cópia profunda deste empregado.
     * <p>
     * Este método garante que o objeto {@link MembroSindicato} associado também seja
     * clonado, evitando que a cópia e o original compartilhem a mesma referência de sindicato.
     *
     * @return Uma cópia (clone) desta instância de empregado.
     */
    @Override
    public Empregado clone() {
        try {
            Empregado cloned = (Empregado) super.clone();
            if (this.sindicato != null) {
                cloned.sindicato = this.sindicato.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Não deve acontecer, pois a classe é Cloneable
        }
    }

    // --- Getters e Setters ---

    /**
     * Retorna o nome do empregado.
     * @return o nome do empregado.
     */
    public String getName() {
        return this.nome;
    }

    /**
     * Retorna o endereço do empregado.
     * @return o endereço do empregado.
     */
    public String getEndereco() {
        return this.endereco;
    }

    /**
     * Retorna o tipo do empregado (ex: "horista").
     * @return o tipo do empregado.
     */
    public String getTipo() {
        return this.tipo;
    }

    /**
     * Retorna o ID único do empregado.
     * @return o ID do empregado.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Retorna os dados sindicais do empregado.
     * @return um objeto {@link MembroSindicato}, ou {@code null} se não for sindicalizado.
     */
    public MembroSindicato getSindicato() {
        return sindicato;
    }

    /**
     * Retorna o método de pagamento preferido do empregado.
     * @return o método de pagamento (ex: "emMaos", "banco", "correios").
     */
    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    /**
     * Retorna o nome do banco para depósito.
     * @return o nome do banco.
     */
    public String getBanco() {
        return banco;
    }

    /**
     * Retorna a agência bancária para depósito.
     * @return o número da agência.
     */
    public String getAgencia() {
        return agencia;
    }

    /**
     * Retorna a conta corrente para depósito.
     * @return o número da conta corrente.
     */
    public String getContaCorrente() {
        return contaCorrente;
    }

    /**
     * Verifica se o empregado é filiado ao sindicato.
     * @return {@code true} se o empregado for filiado, {@code false} caso contrário.
     */
    public boolean isSindicalizado() {
        return this.sindicato != null;
    }

    /**
     * Atualiza o nome do empregado.
     * @param name o novo nome.
     */
    public void setName(String name) {
        this.nome = name;
    }

    /**
     * Atualiza o endereço do empregado.
     * @param endereco o novo endereço.
     */
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    /**
     * Atualiza o tipo do empregado.
     * @param tipo o novo tipo.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Define a filiação sindical do empregado.
     * @param sindicato o objeto {@link MembroSindicato} ou {@code null} para remover a filiação.
     */
    public void setSindicato(MembroSindicato sindicato) {
        this.sindicato = sindicato;
    }

    /**
     * Atualiza o método de pagamento do empregado.
     * @param metodoPagamento o novo método de pagamento.
     */
    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    /**
     * Atualiza o nome do banco para depósito.
     * @param banco o novo nome do banco.
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }

    /**
     * Atualiza a agência para depósito.
     * @param agencia a nova agência.
     */
    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    /**
     * Atualiza a conta corrente para depósito.
     * @param contaCorrente a nova conta corrente.
     */
    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }
}