package br.ufcg.edu.simulador.entidades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufcg.edu.simulador.EstatisticasUtil;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;
import eduni.simjava.distributions.ContinuousGenerator;
import eduni.simjava.distributions.Generator;

public class RecursoGenerico extends Sim_entity {
	
    public static boolean DEBUG = false;

	private Sim_stat stat;
	
	private Map<OperacaoEnum, List<ContinuousGenerator>> geradoresTempoServico = new HashMap<OperacaoEnum, List<ContinuousGenerator>>();
	
	private Map<OperacaoEnum, ContinuousGenerator> geradoresTempoPensamento = new HashMap<OperacaoEnum, ContinuousGenerator>();
	
	private RecursoEnum recurso;

	public RecursoGenerico(String nome, RecursoEnum recurso, NoCBMG cbmg) {
		super(nome);
		
		this.recurso = recurso;
		
		add_port(new Sim_port(ConstantesSimulador.PORTA_ENTRADA));
		addSaidas(cbmg, new HashSet<NoCBMG>());

		stat = new Sim_stat();
		stat.add_measure(Sim_stat.UTILISATION);
		stat.add_measure(Sim_stat.QUEUE_LENGTH);
		stat.add_measure(Sim_stat.THROUGHPUT);
		stat.add_measure(Sim_stat.WAITING_TIME);
		stat.add_measure(Sim_stat.SERVICE_TIME);
		stat.add_measure(Sim_stat.RESIDENCE_TIME);
		stat.add_measure(Sim_stat.ARRIVAL_RATE);
		set_stat(stat);
	}

	public void setGeradoresTempoPensamento(
			Map<OperacaoEnum, ContinuousGenerator> geradoresTempoPensamento) {
		this.geradoresTempoPensamento = geradoresTempoPensamento;
	}

	public void addGeradoresTempoServico(Map<OperacaoEnum, List<ContinuousGenerator>> mapaGeradores) {
		for (Entry<OperacaoEnum, List<ContinuousGenerator>> entry : mapaGeradores.entrySet()) {
			addGeradoresTempoServico(entry.getKey(), entry.getValue());
		}
	}
	
	public void addGeradoresTempoServico(OperacaoEnum operacao, List<ContinuousGenerator> geradores) {
		List<RecursoEnum> recursos = operacao.getRecursos();

		if (recursos.size() != geradores.size()) {
			throw new RuntimeException("operacao.getRecursos().size() != geradores.size()");
		} else {
			geradoresTempoServico.put(operacao, geradores);
			
			for (Generator gerador : geradores) {
				add_generator(gerador);
			}
		}
	}

    public void body() {
    	while (Sim_system.running()) {
            Sim_event e = new Sim_event();
            sim_get_next(e);
            
            //TODO: Ver o que fazer aqui, pois no final da simulação, chegam eventos desconhecidos.
            Object dados = e.get_data();
			
            if (dados != null) {
				EventoGenericoCBMG eventoGenerico = (EventoGenericoCBMG) dados;

				double tempoEspera = Sim_system.sim_clock() - eventoGenerico.getRelogioUltimaResposta();
				
				if (eventoGenerico.getUltimoTempoPensamento() != 0.0) {
					tempoEspera -= eventoGenerico.getUltimoTempoPensamento();
					eventoGenerico.setUltimoTempoPensamento(0.0);
				}
				
				double sample = getAmostraTempoServico(eventoGenerico);
				debug("[" + recurso + "] Processando evento " + eventoGenerico.hashCode() + ". " +
    					"Processamento: " + sample);
				sim_process(sample);
				
				if (!eventoGenerico.isSessaoIniciada()) {
					EstatisticasUtil.getInstance().adicionarSessao();
					eventoGenerico.setSessaoIniciada(true);
					eventoGenerico.setRelogioInicioSessao(Sim_system.clock());
				}
				
				eventoGenerico.setTempoResposta(eventoGenerico.getTempoResposta() + tempoEspera + sample);
				eventoGenerico.setRelogioUltimaResposta(Sim_system.sim_clock());
				
				EstatisticasUtil.getInstance().adicionarAmostraSessoesExistentes();
				
				OperacaoEnum operacaoAnterior = eventoGenerico.getOperacao();
				if (eventoGenerico.proximoRecurso()) {
					double tempoPensamento = 0.0;
					if (operacaoAnterior != eventoGenerico.getOperacao()) {
						EstatisticasUtil.getInstance().adicionarAmostraTempoResposta(operacaoAnterior, eventoGenerico.getTempoResposta());
						eventoGenerico.setTempoResposta(0.0);
						tempoPensamento = getAmostraTempoPensamento(operacaoAnterior);
						eventoGenerico.setUltimoTempoPensamento(tempoPensamento);
					}
					
					debug("[" + recurso + "] Saída: " + getSaida(eventoGenerico));
					sim_schedule(getSaida(eventoGenerico), tempoPensamento, 0, dados);
				} else {
					EstatisticasUtil.getInstance().removerSessao();
					EstatisticasUtil.getInstance().adicionarAmostraTempoResposta(operacaoAnterior, eventoGenerico.getTempoResposta());
					eventoGenerico.setRelogioFimSessao(Sim_system.clock());
					EstatisticasUtil.getInstance().adicionarAmostraTempoSessao(eventoGenerico.getTempoSessao());
				}
            } else {
            	sim_process(0);
            }
            
            sim_completed(e);
    	}
    }


	protected void addSaidas(NoCBMG no, Collection<NoCBMG> nosVisitados) {
		if (!nosVisitados.contains(no)) {
			nosVisitados.add(no);
			if (no.getOperacao() != null) {
				if (no.getArcos().isEmpty()) {
					addSaidas(no.getRecursos());
				} else {
					for (ArcoCBMG arco : no.getArcos()) {
						if (arco.getNo().getOperacao() != null) {
							addSaidas(getRecursos(no, arco.getNo()));
						}
					}
				}
			}

			for (ArcoCBMG arco : no.getArcos()) {
				if (arco.getNo().getOperacao() != null) {
					addSaidas(arco.getNo(), nosVisitados);
				}
			}
		}
	}

	private List<RecursoEnum> getRecursos(NoCBMG no, NoCBMG no2) {
		List<RecursoEnum> recursos = new ArrayList<RecursoEnum>(no.getRecursos());
		recursos.addAll(no2.getRecursos());
		return recursos;
	}

	protected void addSaidas(List<RecursoEnum> recursos) {
		RecursoEnum recursoAnterior = null;
		for (RecursoEnum recursoCorrente : recursos) {
			if (recursoAnterior != null && recursoAnterior == recurso) {
				addSaida(recursoCorrente);
			}
			
			recursoAnterior = recursoCorrente;
		}
	}
	
	protected String getSaida(EventoGenericoCBMG eventoGenerico) {
		RecursoEnum recursoSaida = eventoGenerico.getRecursoCorrente();
		String sufixo = "";
		
//		if (clusters.containsKey(recursoSaida)) {
//			Integer numeroServidorBalanceado = eventoGenerico.getNumeroServidorBalanceado(recursoSaida);
//			
//			if (numeroServidorBalanceado != null) {
//				sufixo = numeroServidorBalanceado + "";
//			} else {
//				balanceamento = (balanceamento + 1) % getNumeroServidores(recursoSaida);
//				numeroServidorBalanceado = balanceamento + 1;
//				sufixo = numeroServidorBalanceado + "";
//				eventoGenerico.addRecursoBalanceado(recursoSaida, numeroServidorBalanceado);
//			}
//		}
		
		return getNomePortaSaida(recursoSaida) + sufixo;
	}

	public RecursoEnum getRecurso() {
		return recurso;
	}

	private double getAmostraTempoServico(EventoGenericoCBMG eventoGenerico) {
		return getGeradorTempoServico(eventoGenerico.getOperacao(), eventoGenerico.getNumeroRecursoCorrente()).sample();
	}

	private double getAmostraTempoPensamento(OperacaoEnum operacao) {
		return getGeradorTempoPensamento(operacao).sample();
	}

	private ContinuousGenerator getGeradorTempoServico(OperacaoEnum operacao, int numeroRecurso) {
		return geradoresTempoServico.get(operacao).get(numeroRecurso);
	}

	private ContinuousGenerator getGeradorTempoPensamento(OperacaoEnum operacao) {
		return geradoresTempoPensamento.get(operacao);
	}

	private void addSaida(RecursoEnum recursoSaida) {
		String nomePorta = getNomePortaSaida(recursoSaida);
		
//		if (clusters.containsKey(recursoSaida)) {
//			for (int i = 1; i <= getNumeroServidores(recursoSaida); i++) {
//				addSaida(nomePorta + i);
//			}
//		} else {
			addSaida(nomePorta);
//		}
	}

//	public int getNumeroServidores() {
//	return getNumeroServidores(recurso);
//}


//	private int getNumeroServidores(RecursoEnum recursoSaida) {
//		return clusters.containsKey(recursoSaida) ? clusters.get(recursoSaida) : 1;
//	}


	protected void addSaida(String nomePorta) {
		if (get_port(nomePorta) == null) {
			System.out.println("Adicionando saída " + nomePorta);
			add_port(new Sim_port(nomePorta));
		}
	}

	protected String getNomePortaSaida(RecursoEnum recurso) {
		return ConstantesSimulador.getNomePortaSaida(recurso);
	}

	private void debug(String string) {
		if (DEBUG) {
			System.out.println(string);
		}
	}
}
