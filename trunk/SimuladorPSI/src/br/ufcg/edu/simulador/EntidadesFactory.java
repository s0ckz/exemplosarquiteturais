package br.ufcg.edu.simulador;
import java.util.List;
import java.util.Map;

import br.ufcg.edu.simulador.entidades.ClienteGenerico;
import br.ufcg.edu.simulador.entidades.NoCBMG;
import br.ufcg.edu.simulador.entidades.OperacaoEnum;
import br.ufcg.edu.simulador.entidades.RecursoEnum;
import br.ufcg.edu.simulador.entidades.RecursoGenerico;
import eduni.simjava.distributions.ContinuousGenerator;


public class EntidadesFactory {

	public static void criarCliente(RecursoEnum cliente, String sufixo, NoCBMG cbmg, double taxa) {
		System.out.println("Criando cliente: " + cliente.getIdentificador());
		new ClienteGenerico(getNomeRecurso(cliente, sufixo), cbmg, taxa);
	}
	
	public static void criarServidor(RecursoEnum recursoCpu, RecursoEnum recursoHd, String sufixo, 
			NoCBMG cbmg, Map<OperacaoEnum, List<ContinuousGenerator>> geradoresTempoServico, 
			Map<OperacaoEnum, ContinuousGenerator> geradoresTempoPensamento) {
		System.out.println("Criando servidor: " + recursoCpu);
		RecursoGenerico cpu = new RecursoGenerico(getNomeRecurso(recursoCpu, sufixo), recursoCpu, cbmg);
		cpu.addGeradoresTempoServico(geradoresTempoServico);
		cpu.setGeradoresTempoPensamento(geradoresTempoPensamento);
		
		System.out.println("Criando servidor: " + recursoHd);
		RecursoGenerico hd = new RecursoGenerico(getNomeRecurso(recursoHd, sufixo), recursoHd, cbmg);
		hd.addGeradoresTempoServico(geradoresTempoServico);
		hd.setGeradoresTempoPensamento(geradoresTempoPensamento);
	}

	public static String getNomeRecurso(RecursoEnum recurso, String sufixo) {
		return recurso.getIdentificador() + sufixo;
	}

}
