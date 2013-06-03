package br.ufcg.edu.simulador.entidades;

public class ArcoCBMG {
	
	private NoCBMG no;
	
	private int probabilidade;
	
	public ArcoCBMG(NoCBMG no, int probabilidade) {
		this.no = no;
		this.probabilidade = probabilidade;
	}

	public NoCBMG getNo() {
		return no;
	}

	public void setNo(NoCBMG no) {
		this.no = no;
	}

	public int getProbabilidade() {
		return probabilidade;
	}

	public void setProbabilidade(int probabilidade) {
		this.probabilidade = probabilidade;
	}

}
