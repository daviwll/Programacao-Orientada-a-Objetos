package wepayu.command;

import wepayu.models.Empregado;
import wepayu.models.MembroSindicato;
import wepayu.services.Sistema;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Comando para executar a folha de pagamento para uma data específica.
 * <p>
 * Esta classe encapsula a ação de rodar a folha, que é uma operação que
 * modifica o estado do sistema (ex: a dívida sindical de um horista).
 * Ela permite que a operação seja desfeita (undo), restaurando o estado
 * dos empregados para como estavam antes do pagamento.
 *
 * @see Command
 * @see Sistema
 */
public class RodaFolhaCommand implements Command {
    private Sistema sistema;
    private String data;
    private String saida;
    private Map<String, Double> dividasAnteriores;
    private Map<String, LocalDate> ultimosPagamentosAnteriores;

    /**
     * Constrói o comando para rodar a folha de pagamento.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param data A data para a qual a folha deve ser rodada (ex: "dd/MM/yyyy").
     * @param saida O caminho do arquivo onde o resultado da folha será salvo.
     */
    public RodaFolhaCommand(Sistema sistema, String data, String saida) {
        this.sistema = sistema;
        this.data = data;
        this.saida = saida;
        this.dividasAnteriores = new HashMap<>();
        this.ultimosPagamentosAnteriores = new HashMap<>();
    }

    /**
     * Executa o processamento da folha de pagamento.
     * <p>
     * Antes de rodar a folha, salva o estado relevante dos membros do sindicato
     * (dívida sindical e data do último pagamento) para permitir que a ação seja desfeita.
     *
     * @throws Exception se a data for inválida ou ocorrer um erro ao gerar o arquivo.
     */
    @Override
    public void execute() throws Exception {
        for (Empregado emp : sistema.getEmpregados()) {
            if (emp.isSindicalizado()) {
                MembroSindicato s = emp.getSindicato();
                dividasAnteriores.put(s.getIdMembro(), s.getDividaSindical());
                ultimosPagamentosAnteriores.put(s.getIdMembro(), s.getUltimoDiaPago());
            }
        }
        sistema.rodaFolha(data, saida);
    }

    /**
     * Desfaz o processamento da folha de pagamento.
     * <p>
     * Restaura o estado da dívida sindical e da data do último pagamento dos membros
     * do sindicato para os valores que possuíam antes da execução do comando.
     *
     * @throws Exception se ocorrer um erro ao localizar os empregados.
     */
    @Override
    public void undo() throws Exception {
        for (Empregado emp : sistema.getEmpregados()) {
            if (emp.isSindicalizado()) {
                MembroSindicato s = emp.getSindicato();
                if (dividasAnteriores.containsKey(s.getIdMembro())) {
                    s.setDividaSindical(dividasAnteriores.get(s.getIdMembro()));
                    s.setUltimoDiaPago(ultimosPagamentosAnteriores.get(s.getIdMembro()));
                }
            }
        }
    }
}