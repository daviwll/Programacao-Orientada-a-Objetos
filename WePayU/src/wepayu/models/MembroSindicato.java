package wepayu.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a filiação de um {@link Empregado} a um sindicato.
 * <p>
 * Esta classe armazena o ID do membro no sindicato, sua taxa sindical diária,
 * e gerencia taxas de serviço adicionais. Ela também implementa a lógica
 * de "dívida sindical" para o pagamento de horistas e controla o último dia
 * em que o pagamento foi processado para evitar cálculos duplicados.
 *
 * @see Empregado
 * @see TaxaServico
 */
public class MembroSindicato implements Cloneable {
    private String idMembro;
    private double taxaSindical;
    private double dividaSindical = 0.0;
    private List<TaxaServico> taxasDeServicos = new ArrayList<>();
    private LocalDate ultimoDiaPago;

    /**
     * Constrói uma nova instância de MembroSindicato.
     *
     * @param idMembro O ID único do empregado no sindicato.
     * @param taxaSindical O valor da taxa sindical diária.
     */
    public MembroSindicato(String idMembro, double taxaSindical) {
        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
    }

    /**
     * Cria e retorna uma cópia profunda deste objeto.
     * <p>
     * Garante que a lista de {@link TaxaServico} também seja clonada, criando
     * uma instância totalmente independente.
     *
     * @return Uma cópia (clone) desta instância.
     */
    @Override
    public MembroSindicato clone() {
        try {
            MembroSindicato cloned = (MembroSindicato) super.clone();
            // Cópia profunda da lista de taxas de serviço
            cloned.taxasDeServicos = new ArrayList<>();
            for (TaxaServico t : this.taxasDeServicos) {
                cloned.taxasDeServicos.add((TaxaServico) t.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Não deve acontecer
        }
    }

    /**
     * Retorna o ID do membro no sindicato.
     *
     * @return O ID do membro.
     */
    public String getIdMembro() {
        return idMembro;
    }

    /**
     * Retorna o valor da taxa sindical diária.
     *
     * @return O valor da taxa diária.
     */
    public double getTaxaSindical() {
        return taxaSindical;
    }

    /**
     * Retorna a data do último pagamento processado para este membro.
     * Usado para evitar o reprocessamento de dívidas sindicais.
     *
     * @return A data do último pagamento, ou {@code null} se nunca foi pago.
     */
    public LocalDate getUltimoDiaPago() {
        return ultimoDiaPago;
    }

    /**
     * Define a data do último pagamento processado.
     *
     * @param ultimoDiaPago A data do dia de pagamento.
     */
    public void setUltimoDiaPago(LocalDate ultimoDiaPago) {
        this.ultimoDiaPago = ultimoDiaPago;
    }

    /**
     * Retorna a lista de todas as taxas de serviço cobradas deste membro.
     *
     * @return Uma {@link List} de objetos {@link TaxaServico}.
     */
    public List<TaxaServico> getTotalTaxas() {
        return taxasDeServicos;
    }

    /**
     * Adiciona uma nova taxa de serviço ao histórico do membro.
     *
     * @param taxa O objeto {@link TaxaServico} a ser adicionado.
     */
    public void addTaxa(TaxaServico taxa) {
        this.taxasDeServicos.add(taxa);
    }

    /**
     * Remove uma taxa de serviço específica do histórico.
     * Essencial para a funcionalidade de 'undo'.
     *
     * @param taxa O objeto {@link TaxaServico} a ser removido.
     */
    public void removeTaxa(TaxaServico taxa) {
        this.taxasDeServicos.remove(taxa);
    }

    /**
     * Retorna o valor atual da dívida sindical acumulada.
     * Usado principalmente para horistas cujo pagamento pode não cobrir as taxas.
     *
     * @return O valor da dívida acumulada.
     */
    public double getDividaSindical() {
        return dividaSindical;
    }

    /**
     * Define ou atualiza o valor da dívida sindical.
     *
     * @param dividaSindical O novo valor da dívida.
     */
    public void setDividaSindical(double dividaSindical) {
        this.dividaSindical = dividaSindical;
    }
}