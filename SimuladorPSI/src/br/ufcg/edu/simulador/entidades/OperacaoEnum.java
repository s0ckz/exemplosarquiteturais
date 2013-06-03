package br.ufcg.edu.simulador.entidades;

import java.util.Arrays;
import java.util.List;

public enum OperacaoEnum {
	
	ACESSAR_PAGINA_INICIAL("acessarPaginaInicial", 
			Arrays.asList(
			RecursoEnum.SERVIDOR_APLICACAO_CPU, 
			RecursoEnum.SERVIDOR_APLICACAO_HD)), 
	RECUPERAR_SENHA("recuperarSenha", 
			Arrays.asList(
			RecursoEnum.SERVIDOR_APLICACAO_CPU, 
			RecursoEnum.SERVIDOR_APLICACAO_HD,
			RecursoEnum.SERVIDOR_BD_CPU,
			RecursoEnum.SERVIDOR_BD_HD)), 
	EFETUAR_LOGIN("efetuarLogin", 
			Arrays.asList(
			RecursoEnum.SERVIDOR_APLICACAO_CPU, 
			RecursoEnum.SERVIDOR_APLICACAO_HD,
			RecursoEnum.SERVIDOR_JOSSO_AGENT_CPU,
			RecursoEnum.SERVIDOR_JOSSO_AGENT_HD,
			RecursoEnum.SERVIDOR_JOSSO_GATEWAY_CPU,
			RecursoEnum.SERVIDOR_JOSSO_GATEWAY_HD,
			RecursoEnum.SERVIDOR_BD_CPU,
			RecursoEnum.SERVIDOR_BD_HD)), 
	ACESSAR_PAGINA_INICIAL_RESTRITA("acessarPaginaInicialRestrita", 
			Arrays.asList(
			RecursoEnum.SERVIDOR_APLICACAO_CPU, 
			RecursoEnum.SERVIDOR_APLICACAO_HD)), 
	ALTERAR_SENHA("alterarSenha",
			Arrays.asList(
			RecursoEnum.SERVIDOR_APLICACAO_CPU, 
			RecursoEnum.SERVIDOR_APLICACAO_HD,
			RecursoEnum.SERVIDOR_BD_CPU,
			RecursoEnum.SERVIDOR_BD_HD)), 
	ACESSAR_UM_SISTEMA("acessarUmSistema",
			Arrays.asList(
			RecursoEnum.SERVIDOR_APLICACAO_CPU, 
			RecursoEnum.SERVIDOR_APLICACAO_HD)), 
	EFETUAR_LOGOUT("efetuarLogout",
			Arrays.asList(
			RecursoEnum.SERVIDOR_APLICACAO_CPU, 
			RecursoEnum.SERVIDOR_APLICACAO_HD,
			RecursoEnum.SERVIDOR_JOSSO_AGENT_CPU,
			RecursoEnum.SERVIDOR_JOSSO_AGENT_HD,
			RecursoEnum.SERVIDOR_JOSSO_GATEWAY_CPU,
			RecursoEnum.SERVIDOR_JOSSO_GATEWAY_HD));
	
	private final List<RecursoEnum> servidores;
	private String sigla;
	
	private OperacaoEnum(String sigla, List<RecursoEnum> tiposServidores) {
		this.sigla = sigla;
		this.servidores = tiposServidores;
	}

	public List<RecursoEnum> getRecursos() {
		return servidores;
	}

	public RecursoEnum getRecurso(int i) {
		return getRecursos().get(i);
	}

	public int getNumeroRecursos() {
		return getRecursos().size();
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

}
