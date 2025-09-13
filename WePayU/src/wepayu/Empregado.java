package wepayu;

/**
 *
 * @author davi
 */
public abstract class Empregado {
    private String nome;
    private String endereco;
    private String tipo;
    private String id;
    private MembroSindicato sindicato;


    public Empregado(String nome, String endereco, String tipo, String id)
    {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.id = id;
        this.sindicato = null;
    }

    public String getName() {
        return this.nome;
    }

    public String getEndereco() {
        return this.endereco;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getId() {
        return this.id;
    }

    public MembroSindicato getSindicato() {
        return sindicato;
    }

    public void setName(String name) {
        this.nome = name;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSindicato(MembroSindicato sindicato) {
        this.sindicato = sindicato;
    }
}