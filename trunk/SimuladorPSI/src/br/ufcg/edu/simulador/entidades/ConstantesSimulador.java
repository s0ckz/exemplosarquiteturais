package br.ufcg.edu.simulador.entidades;

public class ConstantesSimulador {

	public static final String PORTA_ENTRADA = "entrada";
	
	public static final String PORTA_SAIDA = "saida";

	public static final String GERADOR_CLIENTES = "geradorClientes";

	public static final String CLIENTE = "cliente";

	public static String getNomePortaSaida(RecursoEnum recurso) {
		return ConstantesSimulador.PORTA_SAIDA + "_" + recurso.getIdentificador();
	}

}
