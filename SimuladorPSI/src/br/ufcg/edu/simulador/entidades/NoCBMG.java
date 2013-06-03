package br.ufcg.edu.simulador.entidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class NoCBMG {
	
	private Random random = new Random();
	
	private String sigla;
	
	private String descricao;
	
	private OperacaoEnum operacao;
	
	private List<ArcoCBMG> arcos = new ArrayList<ArcoCBMG>();

	public NoCBMG(String sigla, String descricao, OperacaoEnum operacao) {
		this.sigla = sigla;
		this.descricao = descricao;
		this.operacao = operacao;
	}
	
	public void addArco(NoCBMG no, int probabilidade) {
		if (probabilidade <= 0) {
			throw new RuntimeException("probabilidade <= 0");
		} else if (getSomaProbabilidade() + probabilidade > 100) {
			throw new RuntimeException("sum(probabilidade) > 100");
		}
		getArcos().add(new ArcoCBMG(no, probabilidade));
		ordenar();
	}
		
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<ArcoCBMG> getArcos() {
		return arcos;
	}

	public void setArcos(List<ArcoCBMG> arcos) {
		this.arcos = arcos;
		ordenar();
	}
	
	public OperacaoEnum getOperacao() {
		return operacao;
	}

	public void setOperacao(OperacaoEnum operacao) {
		this.operacao = operacao;
	}

	public boolean isNoValido() {
		return getSomaProbabilidade() == 100;
	}
	
	private int getSomaProbabilidade() {
		int soma = 0;
		
		for (ArcoCBMG arco : arcos) {
			soma += arco.getProbabilidade();
		}
		
		return soma;
	}

	public RecursoEnum getRecurso(int i) {
		return getOperacao().getRecurso(i);
	}

	public int getNumeroRecursos() {
		return getOperacao().getNumeroRecursos();
	}

	public List<RecursoEnum> getRecursos() {
		return getOperacao().getRecursos();
	}

	public boolean isFolha() {
		return getArcos().size() == 0;
	}

	public NoCBMG getTransicaoAleatoria() {
		if (isFolha()) {
			return null;
		}
		
		if (!isNoValido()) {
			throw new RuntimeException("!isNoValido()");
		}
		
		int valor = random.nextInt(100) + 1;
		
		return getNo(valor);
	}
	
	public String toString() {
		return getSigla() + ";" + getDescricao();
	}

	private NoCBMG getNo(int valor) {
		int acumulado = 0;
		
		for (ArcoCBMG arco : getArcos()) {
			acumulado += arco.getProbabilidade();
			if (valor <= acumulado) {
				return arco.getNo();
			}
		}
		
		return null;
	}

	private void ordenar() {
		Collections.sort(arcos, new Comparator<ArcoCBMG>() {
			public int compare(ArcoCBMG arco1, ArcoCBMG arco2) {
				return arco1.getProbabilidade() - arco2.getProbabilidade();
			}
		});
	}
	
	public static void main(String[] args) {
		NoCBMG entrada = new NoCBMG("e", "Entrada", null);
		NoCBMG paginaInicial = new NoCBMG("p", "Página inicial", OperacaoEnum.ACESSAR_PAGINA_INICIAL);
		NoCBMG login = new NoCBMG("l", "Efetuar login", OperacaoEnum.EFETUAR_LOGIN);
		NoCBMG saida = new NoCBMG("s", "Saída", null);
		
		entrada.addArco(paginaInicial, 100);
		paginaInicial.addArco(login, 55);
		paginaInicial.addArco(saida, 45);

		int quantidadeLogin = 0;
		int quantidadeSaida = 0;
		for (int i = 0; i < 100; i++) {
			if (paginaInicial.getTransicaoAleatoria().getSigla().equals("l")) {
				quantidadeLogin++;
			} else {
				quantidadeSaida++;
			}
		}
		System.out.println((double) quantidadeLogin / (quantidadeLogin+quantidadeSaida));
		System.out.println((double) quantidadeSaida / (quantidadeLogin+quantidadeSaida));
	}

}
