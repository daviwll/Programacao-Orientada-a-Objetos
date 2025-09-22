package wepayu.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa uma taxa de serviço pontual cobrada pelo sindicato a um de seus membros.
 * <p>
 * Diferente da taxa sindical regular, as taxas de serviço são cobranças
 * [cite_start]avulsas por serviços adicionais (como creche, etc.) prestados ao {@link MembroSindicato}. [cite: 152]
 *
 * @see MembroSindicato
 */
public class TaxaServico implements Cloneable {
    private LocalDate data;
    private double valor;

    /**
     * Constrói uma nova instância de TaxaServico.
     *
     * @param data  A data em que a taxa foi gerada.
     * @param valor O valor monetário da taxa de serviço.
     */
    public TaxaServico(LocalDate data, double valor) {
        this.data = data;
        this.valor = valor;
    }

    /**
     * Cria e retorna uma cópia superficial deste objeto.
     *
     * @return Uma cópia (clone) desta instância.
     */
    @Override
    public TaxaServico clone() {
        try {
            return (TaxaServico) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Não deve acontecer
        }
    }

    /**
     * Retorna a data da taxa de serviço.
     *
     * @return A data da taxa.
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Retorna o valor da taxa de serviço.
     *
     * @return O valor monetário da taxa.
     */
    public double getValor() {
        return valor;
    }

    /**
     * Compara esta TaxaServico com outro objeto para verificar igualdade.
     * Duas taxas de serviço são consideradas iguais se ocorreram na mesma data e tiveram o mesmo valor.
     *
     * @param o O objeto a ser comparado.
     * @return {@code true} se os objetos forem iguais, {@code false} caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxaServico that = (TaxaServico) o;
        return Double.compare(that.valor, valor) == 0 && Objects.equals(data, that.data);
    }

    /**
     * Retorna um código hash para esta TaxaServico, baseado na data e no valor.
     *
     * @return O código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(data, valor);
    }
}