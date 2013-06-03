package br.ufcg.edu.simulador;

import java.util.HashMap;
import java.util.Map;

import br.ufcg.edu.simulador.entidades.OperacaoEnum;


public class EstatisticasUtil {
	
	private class AmostraTempoResposta {
		private int numeroAmostras;
		private double somatorioAmostras;
	}
	
	private static final EstatisticasUtil INSTANCE = new EstatisticasUtil();
	
	private Object mutex = this;
	
	private int totalSessoes;
	
	private int numeroSessoesExistentes;
	
	private int maximoSessoesExistentes;
	
	private double somatorioAmostraSessoesExistentes;
	
	private double somatorioAmostraTempoSessao;
	
	private Map<OperacaoEnum, AmostraTempoResposta> mapaAmostrasTemposResposta = new HashMap<OperacaoEnum, AmostraTempoResposta>();
	
	private int numeroAmostras;
	
	private int numeroAmostrasTempoSessao;
	
	public static EstatisticasUtil getInstance() {
		return INSTANCE;
	}
	
	private EstatisticasUtil() {
		
	}
	
	public void adicionarAmostraTempoResposta(OperacaoEnum operacao, double tempoResposta) {
		synchronized (mutex) {
			AmostraTempoResposta amostraTempoResposta = mapaAmostrasTemposResposta.get(operacao);
			
			if (amostraTempoResposta == null) {
				mapaAmostrasTemposResposta.put(operacao, amostraTempoResposta = new AmostraTempoResposta());
			}
			
			amostraTempoResposta.numeroAmostras++;
			amostraTempoResposta.somatorioAmostras += tempoResposta;
		}
	}
	
	public void adicionarAmostraSessoesExistentes() {
		synchronized (mutex) {
			somatorioAmostraSessoesExistentes += getNumeroSessoesExistentes();
			numeroAmostras++;
		}
	}
	
	public void adicionarAmostraTempoSessao(double tempoSessao) {
		synchronized (mutex) {
			somatorioAmostraTempoSessao += tempoSessao;
			numeroAmostrasTempoSessao++;
		}
	}
	
	public int getNumeroSessoesExistentes() {
		return numeroSessoesExistentes;
	}

	public int getMaximoSessoesExistentes() {
		return maximoSessoesExistentes;
	}

	public int getTotalSessoes() {
		return totalSessoes;
	}

	public double getMediaSessoes() {
		return somatorioAmostraSessoesExistentes / numeroAmostras;
	}
	
	public double getMediaTempoSessao() {
		return somatorioAmostraTempoSessao / numeroAmostrasTempoSessao;
	}
	
	public double getMediaTempoResposta(OperacaoEnum operacao) {
		AmostraTempoResposta amostraTempoResposta = mapaAmostrasTemposResposta.get(operacao);
		
		if (amostraTempoResposta != null) {
			return amostraTempoResposta.somatorioAmostras / amostraTempoResposta.numeroAmostras;
		} else {
			return 0.0;
		}
	}
	
	public double getMediaTempoResposta() {
		double mediaTempoResposta = 0.0;
		
		for (OperacaoEnum operacao : mapaAmostrasTemposResposta.keySet()) {
			mediaTempoResposta += getMediaTempoResposta(operacao); 
		}
		
		return mediaTempoResposta / mapaAmostrasTemposResposta.size();
	}


	public void adicionarSessao() {
		synchronized (mutex) {
			numeroSessoesExistentes++;
			totalSessoes++;
			
			if (numeroSessoesExistentes > maximoSessoesExistentes) {
				maximoSessoesExistentes = numeroSessoesExistentes;
			}
		}
	}
	
	public void removerSessao() {
		synchronized (mutex) {
			numeroSessoesExistentes--;
			
			if (numeroSessoesExistentes < 0) {
				throw new RuntimeException("numeroSessoesExistentes < 0");
			}
		}
	}

}
