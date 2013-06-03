package br.ufcg.edu.simulador.entidades;



public class EventoGenericoCBMG {
	
	private NoCBMG cbmg;
	private NoCBMG noCorrente;
	private int recursoCorrente;
	private boolean sessaoIniciada;
	private double tempoResposta;
	private double relogioUltimaResposta;
	private double ultimoTempoPensamento;
	private double relogioInicioSessao;
	private double relogioFimSessao;

	public EventoGenericoCBMG(NoCBMG cbmg) {
		this.cbmg = cbmg;
		this.noCorrente = cbmg;
		this.recursoCorrente = -1;
	}
	
	public NoCBMG getNoCorrente() {
		return noCorrente;
	}

	public int getNumeroRecursoCorrente() {
		return recursoCorrente;
	}
	
	public boolean proximoRecurso() {
		if (recursoCorrente == -1 || 
				 (noCorrente.getNumeroRecursos() != 0 && 
						 (recursoCorrente + 1) == noCorrente.getNumeroRecursos())) {
			if (recursoCorrente != -1) {
				noCorrente = noCorrente.getTransicaoAleatoria();
			}
			
			while (noCorrente != null && noCorrente.getOperacao() == null) {
				noCorrente = noCorrente.getTransicaoAleatoria();
			}
			
			recursoCorrente = noCorrente == null ? -1 : 0;
			return noCorrente != null;
		} else {
			recursoCorrente++;
			return true;
		}
	}

	public OperacaoEnum getOperacao() {
		return getNoCorrente().getOperacao();
	}
	
	public RecursoEnum getRecursoCorrente() {
		if (recursoCorrente == -1) {
			throw new RuntimeException("recursoCorrente == -1");
		}
		
		return noCorrente.getRecurso(recursoCorrente);
	}
	
	public boolean isSessaoIniciada() {
		return sessaoIniciada;
	}

	public void setSessaoIniciada(boolean sessaoIniciada) {
		this.sessaoIniciada = sessaoIniciada;
	}

	public void setTempoResposta(double tempoResposta) {
		this.tempoResposta = tempoResposta;
	}

	public double getTempoResposta() {
		return tempoResposta;
	}

	public void setRelogioUltimaResposta(double relogioUltimaResposta) {
		this.relogioUltimaResposta = relogioUltimaResposta;
	}

	public double getRelogioUltimaResposta() {
		return relogioUltimaResposta;
	}

	public void setUltimoTempoPensamento(double ultimoTempoPensamento) {
		this.ultimoTempoPensamento = ultimoTempoPensamento;
	}

	public double getUltimoTempoPensamento() {
		return ultimoTempoPensamento;
	}

	public double getRelogioInicioSessao() {
		return relogioInicioSessao;
	}

	public void setRelogioInicioSessao(double relogioInicioSessao) {
		this.relogioInicioSessao = relogioInicioSessao;
	}

	public double getRelogioFimSessao() {
		return relogioFimSessao;
	}

	public void setRelogioFimSessao(double relogioFimSessao) {
		this.relogioFimSessao = relogioFimSessao;
	}
	
	public double getTempoSessao() {
		return relogioFimSessao - relogioInicioSessao;
	}

	public static void main(String[] args) {
		NoCBMG entrada = new NoCBMG("e", "Entrada", null);
		NoCBMG paginaInicial = new NoCBMG("p", "Página inicial", OperacaoEnum.ACESSAR_PAGINA_INICIAL);
		NoCBMG login = new NoCBMG("l", "Efetuar login", OperacaoEnum.EFETUAR_LOGIN);
		NoCBMG recuperarSenha = new NoCBMG("r", "Recuperar senha", OperacaoEnum.RECUPERAR_SENHA);
		NoCBMG saida = new NoCBMG("s", "Saída", null);
		
		entrada.addArco(paginaInicial, 100);
		paginaInicial.addArco(login, 70);
		paginaInicial.addArco(recuperarSenha, 20);
		paginaInicial.addArco(saida, 10);
		
		recuperarSenha.addArco(paginaInicial, 90);
		recuperarSenha.addArco(saida, 10);
		
		for (int i = 0; i < 100; i++) {
			EventoGenericoCBMG evento = new EventoGenericoCBMG(entrada);
			while (evento.proximoRecurso()) {
				System.out.println(evento.getNoCorrente() + ";" + evento.getRecursoCorrente());
			}
			System.out.println();
		}
	}


}
