package wepayu.models;

/**
 * Representa um empregado do tipo Assalariado, que recebe um salário fixo mensal.
 * Esta classe herda os atributos básicos de {@link Empregado} e adiciona o salário mensal.
 *
 * @see Empregado
 * @see Comissionado
 */
public class Assalariado extends Empregado implements Cloneable {
    private double salarioMensal;

    /**
     * Constrói uma nova instância de Empregado Assalariado.
     *
     * @param name O nome do empregado.
     * @param endereco O endereço do empregado.
     * @param id O ID único do empregado no sistema.
     * @param salarioMensal O valor do salário fixo mensal.
     * @param tipo O tipo de empregado (geralmente "assalariado").
     */
    public Assalariado(String name, String endereco, String id, double salarioMensal, String tipo) {
        super(name, endereco, tipo, id);
        this.salarioMensal = salarioMensal;
    }

    /**
     * Cria e retorna uma cópia (clone) desta instância de Assalariado.
     * A clonagem é "superficial" para os atributos primitivos, o que é suficiente neste caso.
     *
     * @return Uma cópia deste objeto.
     */
    @Override
    public Assalariado clone() {
        return (Assalariado) super.clone();
    }

    /**
     * Retorna o valor do salário mensal do empregado.
     *
     * @return O salário mensal.
     */
    public double getSalarioMensal() {
        return salarioMensal;
    }

    /**
     * Define ou atualiza o valor do salário mensal do empregado.
     *
     * @param salarioMensal O novo valor do salário mensal.
     */
    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }
}