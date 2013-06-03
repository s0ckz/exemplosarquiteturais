package br.ufcg.edu.simulador.entidades;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;
import eduni.simjava.distributions.ContinuousGenerator;
import eduni.simjava.distributions.Sim_negexp_obj;

public class ClienteGenerico extends Sim_entity {
	
    public static boolean DEBUG = false;

	private Sim_stat stat;
	
	private NoCBMG cbmg;
	
	private ContinuousGenerator geradorClientes;

	public ClienteGenerico(String nome, NoCBMG cbmg, double taxa) {
		super(nome);
		this.cbmg = cbmg;
		this.geradorClientes = new Sim_negexp_obj("geradorClientes", 1 / taxa);
		
		addSaidas();
		
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

    private void addSaidas() {
		NoCBMG no = cbmg;
		if (no.getOperacao() == null) {
			if (no.getArcos().size() > 1) {
				throw new RuntimeException("no.getArcos().size() > 1");
			}
			
			no = no.getArcos().get(0).getNo();
		}
		
		if (no.getOperacao() == null) {
			throw new RuntimeException("no.getOperacao() == null");
		}
		
		addSaida(no.getOperacao().getRecursos().get(0));
	}

	public void body() {
    	while (Sim_system.running()) {
			EventoGenericoCBMG eventoGenerico = new EventoGenericoCBMG(cbmg);
			eventoGenerico.proximoRecurso();
			double sample = geradorClientes.sample();
			
			debug("Criando novo evento genérico CBMG. " +
					"Saída: " + getSaida(eventoGenerico) + ". " +
					"Pausa: " + sample);
			
			sim_schedule(getSaida(eventoGenerico), 0.0, 0, eventoGenerico);
			eventoGenerico.setRelogioUltimaResposta(Sim_system.clock());
			sim_pause(sample);
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
