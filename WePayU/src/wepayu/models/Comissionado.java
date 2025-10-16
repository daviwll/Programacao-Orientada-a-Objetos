package wepayu.models;

import java.util.ArrayList;

/**
 * Representa um empregado do tipo Comissionado.
 * Este tipo de empregado recebe um salário base mensal (herdado de {@link Assalariado})
 * mais uma comissão percentual sobre as vendas que realiza.
 *
 * @see Assalariado
 * @see Empregado
 * @see ResultadoDeVenda
 */
public class Comissionado extends Assalariado implements Cloneable {
    private double comissao;
    private ArrayList<ResultadoDeVenda> listaVendas;

    /**
     * Constrói uma nova instância de Empregado Comissionado.
     *
     * @param name Nome do empregado.
     * @param endereco Endereço do empregado.
     * @param id ID único do empregado no sistema.
     * @param salarioMensal O valor do salário base mensal.
     * @param comissao A taxa de comissão sobre as vendas (ex: 0.05 para 5%).
     * @param tipo O tipo de empregado (deve ser "comissionado").
     */
    public Comissionado(String name, String endereco, String id, double salarioMensal, double comissao, String tipo) {
        super(name, endereco, id, salarioMensal, tipo);
        this.comissao = comissao;
        this.listaVendas = new ArrayList<>();
    }

    /**
     * Cria e retorna uma cópia profunda deste objeto Empregado Comissionado.
     * A cópia inclui uma nova lista com clones de todos os {@link ResultadoDeVenda}.
     *
     * @return Uma cópia (clone) desta instância.
     */
    @Override
    public Comissionado clone() {
        Comissionado cloned = (Comissionado) super.clone();
        cloned.listaVendas = new ArrayList<>();
        for (ResultadoDeVenda v : this.listaVendas) {
            cloned.listaVendas.add(v.clone());
        }
        return cloned;
    }

    /**
     * Retorna a taxa de comissão do empregado.
     *
     * @return A taxa de comissão (ex: 0.05 para 5%).
     */
    public double getComissao() {
        return comissao;
    }

    /**
     * Retorna a lista de resultados de venda associados a este empregado.
     *
     * @return Uma {@link ArrayList} contendo os objetos {@link ResultadoDeVenda}.
     */
    public ArrayList<ResultadoDeVenda> getListaVendas() {
        return listaVendas;
    }

    /**
     * Define ou atualiza a taxa de comissão do empregado.
     *
     * @param comissao A nova taxa de comissão.
     */
    public void setComissao(double comissao) {
        this.comissao = comissao;
    }

    /**
     * Adiciona um novo resultado de venda ao histórico do empregado.
     *
     * @param venda O objeto {@link ResultadoDeVenda} a ser adicionado.
     */
    public void addVenda(ResultadoDeVenda venda) {
        this.listaVendas.add(venda);
    }

    /**
     * Remove o resultado de venda mais recente do histórico.
     * Este método é frágil e deve ser usado com cuidado.
     *
     * @see #removeVendaEspecifica(ResultadoDeVenda)
     */
    public void removeVenda() {
        if (!listaVendas.isEmpty()) {
            this.listaVendas.remove(listaVendas.size() - 1);
        }
    }

    /**
     * Remove um resultado de venda específico do histórico do empregado.
     * Essencial para a funcionalidade de 'undo'.
     *
     * @param venda O objeto {@link ResultadoDeVenda} exato a ser removido.
     */
    public void removeVendaEspecifica(ResultadoDeVenda venda) {
        if (venda == null || listaVendas.isEmpty()) return;

        for (int i = 0; i < listaVendas.size(); i++) {
            if (listaVendas.get(i) == venda) {
                listaVendas.remove(i);
                return;
            }
        }
        for (int i = 0; i < listaVendas.size(); i++) {
            if (listaVendas.get(i).equals(venda)) {
                listaVendas.remove(i);
                return;
            }
        }
    }
}