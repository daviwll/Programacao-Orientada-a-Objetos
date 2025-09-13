package wepayu;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class Sistema {
    private ArrayList<Empregado> empregados;
    private int id = 0;

    public Sistema()
    {
        empregados =  new ArrayList<>();
    }

    public Empregado getEmpregado(String id) throws Exception
    {
        if(id.isEmpty())
        {
            throw new Exception("Identificacao do empregado nao pode ser nula.");
        }
        int idInt;
        try{
            idInt = Integer.parseInt(id);
        }
        catch(NumberFormatException e){
            throw new Exception("Empregado nao existe.");
        }
        if(idInt < 0)
        {
            throw new Exception("Identificacao do empregado nao pode ser nula.");
        }
        for(int i = 0; i < empregados.size(); i++)
        {
            if(Integer.parseInt(empregados.get(i).getId()) == idInt)
            {
                return  empregados.get(i);
            }
        }
        throw new Exception("Empregado nao existe.");
    }

    public String criarEmpregado(String name, String endereco, String tipo, String salario) throws Exception
    {
        if(tipo.equals("comissionado"))
        {
            throw new Exception("Tipo nao aplicavel.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Nome nao pode ser nulo.");
        }
        if (endereco == null || endereco.trim().isEmpty())
        {
            throw new Exception("Endereco nao pode ser nulo.");
        }
        if (salario == null || salario.trim().isEmpty())
        {
            throw new Exception("Salario nao pode ser nulo.");
        }
        salario = salario.replace(",", ".");
        double salarioDouble;
        try {
            salarioDouble = Double.parseDouble(salario);
        } catch (NumberFormatException e) {
            throw new Exception("Salario deve ser numerico.");
        }
        if (salarioDouble < 0)
        {
            throw new Exception("Salario deve ser nao-negativo.");
        }

        if(tipo.equals("horista"))
        {
            this.id += 1;
            Horista novoEmpregado = new Horista(name, endereco, String.valueOf(this.id), salarioDouble, tipo);
            empregados.add(novoEmpregado);
            return novoEmpregado.getId();
        }
        else if(tipo.equals("assalariado"))
        {
            this.id += 1;
            Assalariado novoEmpregado = new Assalariado(name, endereco, String.valueOf(this.id), salarioDouble, tipo);
            empregados.add(novoEmpregado);
            return novoEmpregado.getId();
        }
        throw new Exception("Tipo invalido.");
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception
    {
        if(!tipo.equals("comissionado"))
        {
            throw new Exception("Tipo nao aplicavel.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new Exception("Nome nao pode ser nulo.");
        }
        if (endereco == null || endereco.trim().isEmpty())
        {
            throw new Exception("Endereco nao pode ser nulo.");
        }
        if(comissao == null)
        {
            throw new Exception("Tipo nao aplicavel.");
        }
        if(comissao.equals(""))
        {
            throw new Exception("Comissao nao pode ser nula.");
        }
        if(salario == null || salario.trim().isEmpty())
        {
            throw new Exception("Salario nao pode ser nulo.");
        }

        salario = salario.replace(",", ".");
        double salarioDouble;
        try {
            salarioDouble = Double.parseDouble(salario);
        } catch (NumberFormatException e) {
            throw new Exception("Salario deve ser numerico.");
        }
        if (salarioDouble < 0)
        {
            throw new Exception("Salario deve ser nao-negativo.");
        }

        comissao = comissao.replace(",", ".");
        double comissaoDouble;
        try {
            comissaoDouble = Double.parseDouble(comissao);
        } catch (NumberFormatException e) {
            throw new Exception("Comissao deve ser numerica.");
        }
        if(comissaoDouble < 0)
        {
            throw new Exception("Comissao deve ser nao-negativa.");
        }
        this.id += 1;
        Comissionado novoEmpregado = new Comissionado(nome, endereco, String.valueOf(this.id), salarioDouble, comissaoDouble, tipo);
        empregados.add(novoEmpregado);
        return novoEmpregado.getId();
    }

    public void  removerEmpregado(String id) throws Exception
    {
        Empregado empregado = getEmpregado(id);
        empregados.remove(empregado);
    }

    public void lancaCartao(String id, String data, String horas) throws Exception
    {
        Empregado empregado = getEmpregado(id);
        if (!(empregado instanceof Horista)) {
            throw new Exception("Empregado nao eh horista.");
        }

        LocalDate dataLanc;
        try {
            dataLanc = parseDateBR(data);
        } catch (Exception e) {
            throw new Exception("Data invalida.");
        }

        double horasVal;
        try {
            String norm = horas.replace(",", ".");
            horasVal = Double.parseDouble(norm);
        } catch (NumberFormatException e) {
            throw new Exception("Horas devem ser positivas.");
        }

        if (horasVal <= 0) {
            throw new Exception("Horas devem ser positivas.");
        }

        CartaoDePonto cartao = new CartaoDePonto(dataLanc, horasVal);
        ((Horista) empregado).addCartaoDePonto(cartao);
    }

    public void lancaVenda(String id, String data, String valor) throws Exception
    {
        Empregado empregado = getEmpregado(id);

        if(empregado instanceof Comissionado)
        {
            ResultadoDeVenda venda = new ResultadoDeVenda(LocalDate.parse(data), Double.parseDouble(valor));
            ((Comissionado) empregado).addVenda(venda);
        }
        else
        {
            throw new Exception("Empregado nao e Comissionado.");
        }
    }

    public void lancaTaxaServico(String id, String data, String valor) throws Exception
    {
        Empregado empregado = getEmpregado(id);

        if(empregado.getSindicato() != null)
        {
            TaxaServico taxa = new TaxaServico(LocalDate.parse(data), Double.parseDouble(valor));
            empregado.getSindicato().addTaxa(taxa);
        }
        else
        {
            throw new Exception("Membro nao e sindicato");
        }
    }

    public void alteraEmpregado(String id, String atributo, String valor) throws Exception {
        Empregado empregado = getEmpregado(id);

        if (atributo.equals("nome")) {
            empregado.setName(valor);
            return;
        } else if (atributo.equals("endereco")) {
            empregado.setEndereco(valor);
            return;
        } else if (atributo.equals("tipo")) {
            empregado.setTipo(valor);
        }

        if (empregado.getTipo().equals("comissionado")) {
            if (atributo.equals("salario")) {
                ((Comissionado) empregado).setSalarioMensal(Double.parseDouble(valor));
            } else if (atributo.equals("comissao")) {
                ((Comissionado) empregado).setComissao(Double.parseDouble(valor));
            }
        } else if (empregado.getTipo().equals("Assalariado")) {
            if (atributo.equals("salario")) {
                ((Assalariado) empregado).setSalarioMensal(Double.parseDouble(valor));
            }
        } else if (empregado.getTipo().equals("horista")) {
            if (atributo.equals("salario")) {
                ((Horista) empregado).setSalarioHora(Double.parseDouble(valor));
            }
        } else {
            throw new Exception("Atributo nao existente.");
        }

    }

    public void rodaFolha(String data)
    {
        LocalDate newData = LocalDate.parse(data);
        for (Empregado empregado : empregados)
        {
            if(empregado.getTipo().equals("horista") && newData.getDayOfWeek() == DayOfWeek.FRIDAY)
            {
                double total;

                System.out.println(((Horista) empregado).getSalarioHora());
            }
        }
    }

    public String getAtributoEmpregado(String id, String atributo) throws Exception
    {
        Empregado empregado = getEmpregado(id);
        if (atributo.equalsIgnoreCase("nome")) {
            return empregado.getName();
        } else if (atributo.equalsIgnoreCase("endereco")) {
            return empregado.getEndereco();
        } else if (atributo.equalsIgnoreCase("tipo")) {
            return empregado.getTipo();
        } else if (atributo.equalsIgnoreCase("salario")) {
            if (empregado instanceof Horista) {
                double salario = ((Horista) empregado).getSalarioHora();
                return String.format("%.2f", salario).replace('.', ',');
            } else if (empregado instanceof Assalariado) {
                double salario = ((Assalariado) empregado).getSalarioMensal();
                return String.format("%.2f", salario).replace('.', ',');
            }
        } else if (atributo.equalsIgnoreCase("comissao")) {
            if (empregado instanceof Comissionado) {
                double comissao = ((Comissionado) empregado).getComissao();
                return String.format("%.2f", comissao).replace('.', ',');
            }
        } else if (atributo.equalsIgnoreCase("sindicalizado")) {
            return String.valueOf(empregado.getSindicato() != null);
        }

        throw new Exception("Atributo nao existe.");
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        ArrayList<Empregado> encontrados = new ArrayList<>();

        for (Empregado empregado : this.empregados) {
            if (empregado.getName().contains(nome)) {
                encontrados.add(empregado);
            }
        }

        if (indice > 0 && indice <= encontrados.size()) {
            return encontrados.get(indice - 1).getId();
        }

        throw new Exception("Nao ha empregado com esse nome.");
    }

    public int getId() {
        return id;
    }

    private java.time.LocalDate parseDateBR(String data) {
        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("d/M/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        return java.time.LocalDate.parse(data, fmt);
    }

    private boolean withinInclusive(LocalDate d, LocalDate ini, LocalDate fim) {
        return (d.isEqual(ini) || d.isAfter(ini)) && d.isBefore(fim);
    }

    private String formatHoras(double total) {
        long arred = Math.round(total);
        if (Math.abs(total - arred) < 1e-9) {
            return String.valueOf(arred);
        }
        String s = String.format(java.util.Locale.US, "%.2f", total).replace('.', ',');
        while (s.contains(",") && (s.endsWith("0") || s.endsWith(","))) {
            s = s.substring(0, s.length() - 1);
            if (s.endsWith(",")) { s = s.substring(0, s.length() - 1); break; }
        }
        return s;
    }

    public String getHorasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado emp = getEmpregado(id);
        if (!(emp instanceof Horista)) {
            throw new Exception("Empregado nao eh horista.");
        }

        LocalDate ini;
        try { ini = parseDateBR(dataInicial); }
        catch (Exception e) { throw new Exception("Data inicial invalida."); }

        LocalDate fim;
        try { fim = parseDateBR(dataFinal); }
        catch (Exception e) { throw new Exception("Data final invalida."); }

        if (ini.isAfter(fim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double total = 0.0;
        for (CartaoDePonto c : ((Horista) emp).getListaCartoes()) {
            LocalDate d = c.getData();
            if (withinInclusive(d, ini, fim)) {
                total += c.getHoras();
            }
        }
        return formatHoras(total);
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado emp = getEmpregado(id);
        if (!(emp instanceof Horista)) {
            throw new Exception("Empregado nao eh horista.");
        }

        LocalDate ini;
        try { ini = parseDateBR(dataInicial); }
        catch (Exception e) { throw new Exception("Data inicial invalida."); }

        LocalDate fim;
        try { fim = parseDateBR(dataFinal); }
        catch (Exception e) { throw new Exception("Data final invalida."); }

        if (ini.isAfter(fim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double normais = 0.0;
        for (CartaoDePonto c : ((Horista) emp).getListaCartoes()) {
            LocalDate d = c.getData();
            if (withinInclusive(d, ini, fim)) {
                double h = c.getHoras();
                normais += Math.min(h, 8.0);
            }
        }
        return formatHoras(normais);
    }


    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado emp = getEmpregado(id);
        if (!(emp instanceof Horista)) {
            throw new Exception("Empregado nao eh horista.");
        }

        LocalDate ini;
        try { ini = parseDateBR(dataInicial); }
        catch (Exception e) { throw new Exception("Data inicial invalida."); }

        LocalDate fim;
        try { fim = parseDateBR(dataFinal); }
        catch (Exception e) { throw new Exception("Data final invalida."); }

        if (ini.isAfter(fim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double extras = 0.0;
        for (CartaoDePonto c : ((Horista) emp).getListaCartoes()) {
            LocalDate d = c.getData();
            if (withinInclusive(d, ini, fim)) {
                double h = c.getHoras();
                extras += Math.max(h - 8.0, 0.0);
            }
        }
        return formatHoras(extras);
    }

}
