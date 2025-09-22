package wepayu.models;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Representa um empregado do tipo Horista, cujo salário é calculado com base nas horas trabalhadas.
 * <p>
 * Esta classe herda de {@link Empregado} e gerencia uma lista de {@link CartaoDePonto}
 * para registrar o trabalho diário e calcular o pagamento, incluindo horas extras.
 *
 * @see Empregado
 * @see CartaoDePonto
 */
public class Horista extends Empregado implements Cloneable {
    private double salarioHora;
    private ArrayList<CartaoDePonto> listaCartoes;

    /**
     * Constrói uma nova instância de Empregado Horista.
     *
     * @param name        O nome do empregado.
     * @param endereco    O endereço do empregado.
     * @param id          O ID único do empregado no sistema.
     * @param salarioHora O valor recebido por hora de trabalho normal.
     * @param tipo        O tipo de empregado (deve ser "horista").
     */
    public Horista(String name, String endereco, String id, double salarioHora, String tipo) {
        super(name, endereco, tipo, id);
        this.salarioHora = salarioHora;
        this.listaCartoes = new ArrayList<>();
    }

    /**
     * Cria e retorna uma cópia profunda deste objeto Empregado Horista.
     * A cópia inclui uma nova lista com clones de todos os {@link CartaoDePonto}.
     *
     * @return Uma cópia (clone) desta instância.
     */
    @Override
    public Horista clone() {
        Horista cloned = (Horista) super.clone();
        cloned.listaCartoes = new ArrayList<>();
        for (CartaoDePonto c : this.listaCartoes) {
            cloned.listaCartoes.add((CartaoDePonto) c.clone());
        }
        return cloned;
    }

    /**
     * Procura por um cartão de ponto em uma data específica.
     *
     * @param data A data a ser procurada.
     * @return O objeto {@link CartaoDePonto} se encontrado, ou {@code null} caso contrário.
     */
    public CartaoDePonto findCartaoPeloDia(LocalDate data) {
        for (CartaoDePonto cartao : this.listaCartoes) {
            if (cartao.getData().isEqual(data)) {
                return cartao;
            }
        }
        return null;
    }

    /**
     * Adiciona um novo cartão de ponto ou atualiza um existente para o mesmo dia.
     * <p>
     * Se já existe um cartão para a data fornecida, as horas do cartão existente
     * são atualizadas. Caso contrário, um novo cartão é adicionado à lista.
     *
     * @param novoCartao O {@link CartaoDePonto} a ser adicionado ou usado para atualização.
     */
    public void addCartaoDePonto(CartaoDePonto novoCartao) {
        for (CartaoDePonto cartaoExistente : this.listaCartoes) {
            if (cartaoExistente.getData().isEqual(novoCartao.getData())) {
                cartaoExistente.setHoras(novoCartao.getHoras());
                return;
            }
        }
        this.listaCartoes.add(novoCartao);
    }

    /**
     * Remove um cartão de ponto específico da lista.
     * Essencial para a funcionalidade de 'undo'.
     *
     * @param cartao O objeto {@link CartaoDePonto} exato a ser removido.
     */
    public void removeCartaoDePonto(CartaoDePonto cartao) {
        this.listaCartoes.remove(cartao);
    }

    /**
     * Retorna o valor do salário por hora do empregado.
     *
     * @return O salário por hora.
     */
    public double getSalarioHora() {
        return salarioHora;
    }

    /**
     * Define ou atualiza o valor do salário por hora.
     *
     * @param salarioHora O novo valor do salário por hora.
     */
    public void setSalarioHora(double salarioHora) {
        this.salarioHora = salarioHora;
    }

    /**
     * Retorna a lista de todos os cartões de ponto associados a este empregado.
     *
     * @return Uma {@link ArrayList} de objetos {@link CartaoDePonto}.
     */
    public ArrayList<CartaoDePonto> getListaCartoes() {
        return listaCartoes;
    }
}