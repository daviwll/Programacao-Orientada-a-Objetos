package wepayu;

public class Assalariado extends Empregado {
    private double salarioMensal;


    public Assalariado(String name, String endereco, String id, double salarioMensal, String tipo) {
        super(name, endereco, tipo, id);
        this.salarioMensal = salarioMensal;
    }
    public double getSalarioMensal() {
        return salarioMensal;
    }
    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }
}
