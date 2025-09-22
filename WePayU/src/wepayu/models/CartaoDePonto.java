package wepayu.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa um Cartão de Ponto, registrando as horas trabalhadas por um empregado em um dia específico.
 * Esta classe é utilizada principalmente por empregados do tipo {@link Horista} para o cálculo de seu salário.
 *
 * @see Horista
 */
public class CartaoDePonto implements Cloneable {
    private LocalDate data;
    private double horas;

    /**
     * Constrói uma nova instância de Cartão de Ponto.
     *
     * @param data A data em que o trabalho foi realizado.
     * @param horas O total de horas trabalhadas no dia.
     */
    public CartaoDePonto(LocalDate data, double horas) {
        this.data = data;
        this.horas = horas;
    }

    /**
     * Cria e retorna uma cópia superficial deste objeto.
     *
     * @return Uma cópia (clone) desta instância.
     */
    @Override
    public CartaoDePonto clone() {
        try {
            return (CartaoDePonto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Não deve acontecer, pois a classe é Cloneable
        }
    }

    /**
     * Retorna a data do cartão de ponto.
     *
     * @return A data do registro.
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Retorna o número de horas trabalhadas.
     *
     * @return O total de horas.
     */
    public double getHoras() {
        return horas;
    }

    /**
     * Define ou atualiza o número de horas trabalhadas.
     *
     * @param horas O novo total de horas.
     */
    public void setHoras(double horas) {
        this.horas = horas;
    }

    /**
     * Compara este CartaoDePonto com outro objeto para verificar igualdade.
     * Dois cartões de ponto são considerados iguais se forem para o mesmo dia,
     * independentemente do número de horas.
     *
     * @param o O objeto a ser comparado.
     * @return {@code true} se os objetos forem do mesmo dia, {@code false} caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaoDePonto that = (CartaoDePonto) o;
        return Objects.equals(data, that.data);
    }

    /**
     * Retorna um código hash para este CartaoDePonto, baseado na data.
     * É consistente com o método equals().
     *
     * @return O código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}