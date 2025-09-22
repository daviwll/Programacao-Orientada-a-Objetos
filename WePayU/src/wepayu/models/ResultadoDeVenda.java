package wepayu.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa o resultado de uma venda realizada por um empregado Comissionado.
 * <p>
 * Esta classe armazena a data e o valor de uma venda, informações essenciais
 * para o cálculo da comissão de um {@link Comissionado}.
 *
 * @see Comissionado
 */
public class ResultadoDeVenda implements Cloneable {
    private LocalDate date;
    private double valor;

    /**
     * Constrói uma nova instância de ResultadoDeVenda.
     *
     * @param date  A data em que a venda foi realizada.
     * @param valor O valor monetário total da venda.
     */
    public ResultadoDeVenda(LocalDate date, double valor) {
        this.date = date;
        this.valor = valor;
    }

    /**
     * Cria e retorna uma cópia superficial deste objeto.
     *
     * @return Uma cópia (clone) desta instância.
     */
    @Override
    public ResultadoDeVenda clone() {
        try {
            return (ResultadoDeVenda) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Não deve acontecer
        }
    }

    /**
     * Retorna a data da venda.
     *
     * @return A data da venda.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Retorna o valor da venda.
     *
     * @return O valor monetário da venda.
     */
    public double getValor() {
        return valor;
    }

    /**
     * Compara este ResultadoDeVenda com outro objeto para verificar igualdade.
     * Duas vendas são consideradas iguais se ocorreram na mesma data e tiveram o mesmo valor.
     *
     * @param o O objeto a ser comparado.
     * @return {@code true} se os objetos forem iguais, {@code false} caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultadoDeVenda that = (ResultadoDeVenda) o;
        return Double.compare(that.valor, valor) == 0 && Objects.equals(date, that.date);
    }

    /**
     * Retorna um código hash para este ResultadoDeVenda, baseado na data e no valor.
     *
     * @return O código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(date, valor);
    }
}