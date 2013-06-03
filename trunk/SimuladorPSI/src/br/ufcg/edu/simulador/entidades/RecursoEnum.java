package br.ufcg.edu.simulador.entidades;

public enum RecursoEnum {

	SERVIDOR_APLICACAO_CPU("aplicacaoCpu", "Servidor de aplicação - CPU"),
	SERVIDOR_APLICACAO_HD("aplicacaoHd", "Servidor de aplicação - HD"),
	SERVIDOR_BD_CPU("bdCpu", "Servidor de bancos de dados - CPU"),
	SERVIDOR_BD_HD("bdHd", "Servidor de bancos de dados - HD"),
	SERVIDOR_JOSSO_GATEWAY_CPU("jossoGatewayCpu", "Servidor JOSSO Gateway - CPU"),
	SERVIDOR_JOSSO_GATEWAY_HD("jossoGatewayHd", "Servidor JOSSO Gateway - HD"),
	SERVIDOR_JOSSO_AGENT_CPU("jossoAgentCpu", "Servidor JOSSO Agent - CPU"),
	SERVIDOR_JOSSO_AGENT_HD("jossoAgentHd", "Servidor JOSSO Agent - HD"),

	//remover
	CLIENTE("cliente", "Cliente"),
	CLIENTE_RECORRENTE("clienteRecorrente", "Cliente Recorrente"),
	BALANCEADOR_CARGA("balanceadorCarga", "Balanceador de Carga"),
	SERVIDOR_WEB("web", "Servidor web"),
	SERVIDOR_BD("bd", "Servidor de banco de dados"),
	SERVIDOR_JOSSO_GATEWAY("jossoGateway", "Servidor JOSSO Gateway"),
	SERVIDOR_JOSSO_AGENT("jossoAgent", "Servidor JOSSO Agent");
	
	private final String identificador;
	private final String nome;
	
	private RecursoEnum(String identificador, String nome) {
		this.identificador = identificador;
		this.nome = nome;
	}

	public String getIdentificador() {
		return identificador;
	}

	public String getNome() {
		return nome;
	}

}
