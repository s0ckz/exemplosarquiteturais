package br.ufcg.edu.simulador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.ufcg.edu.simulador.entidades.ConstantesSimulador;
import br.ufcg.edu.simulador.entidades.NoCBMG;
import br.ufcg.edu.simulador.entidades.OperacaoEnum;
import br.ufcg.edu.simulador.entidades.RecursoEnum;
import eduni.simjava.Sim_stat;
import eduni.simjava.Sim_system;
import eduni.simjava.distributions.ContinuousGenerator;
import eduni.simjava.distributions.Sim_negexp_obj;
import eduni.sjgv.SJGV;


public class SimuladorPSIUsoMedio {
	
	private double tempoSimulacao = 600;
	
	private double transitorioInicial = 0.0;
	
	private long semente = initSemente();
	
	private double taxaEntrada;
	
	private boolean geracaoGraficos = true;
	private boolean visualizarGraficos = true;

	public void simular() {
//		ClienteGenerico.DEBUG = true;
//		RecursoGenerico.DEBUG = true;
		Sim_system.initialise();
		Sim_system.set_seed(semente);
		
		Map<OperacaoEnum, List<ContinuousGenerator>> geradoresTempoServico = getGeradoresTempoServico();
		Map<OperacaoEnum, ContinuousGenerator> geradoresTempoPensamento = getGeradoresTempoPensamento();
		NoCBMG entrada = getCbmgUsoMedio();
		
		EntidadesFactory.criarCliente(RecursoEnum.CLIENTE, "", entrada, taxaEntrada);
		
		EntidadesFactory.criarServidor(RecursoEnum.SERVIDOR_APLICACAO_CPU, RecursoEnum.SERVIDOR_APLICACAO_HD, "", entrada, 
				geradoresTempoServico, geradoresTempoPensamento);
		ligar(RecursoEnum.CLIENTE, "", RecursoEnum.SERVIDOR_APLICACAO_CPU, "");
		ligar(RecursoEnum.SERVIDOR_APLICACAO_CPU, "", RecursoEnum.SERVIDOR_APLICACAO_HD, "");
		
		EntidadesFactory.criarServidor(RecursoEnum.SERVIDOR_JOSSO_AGENT_CPU, RecursoEnum.SERVIDOR_JOSSO_AGENT_HD, "", entrada, 
				geradoresTempoServico, geradoresTempoPensamento);
		ligar(RecursoEnum.SERVIDOR_APLICACAO_HD, "", RecursoEnum.SERVIDOR_APLICACAO_CPU, "");
		ligar(RecursoEnum.SERVIDOR_APLICACAO_HD, "", RecursoEnum.SERVIDOR_JOSSO_AGENT_CPU, "");
		ligar(RecursoEnum.SERVIDOR_JOSSO_AGENT_CPU, "", RecursoEnum.SERVIDOR_JOSSO_AGENT_HD, "");
		
		EntidadesFactory.criarServidor(RecursoEnum.SERVIDOR_JOSSO_GATEWAY_CPU, RecursoEnum.SERVIDOR_JOSSO_GATEWAY_HD, "", entrada, 
				geradoresTempoServico, geradoresTempoPensamento);
		ligar(RecursoEnum.SERVIDOR_JOSSO_AGENT_HD, "", RecursoEnum.SERVIDOR_JOSSO_GATEWAY_CPU, "");
		ligar(RecursoEnum.SERVIDOR_JOSSO_GATEWAY_CPU, "", RecursoEnum.SERVIDOR_JOSSO_GATEWAY_HD, "");
		
		EntidadesFactory.criarServidor(RecursoEnum.SERVIDOR_BD_CPU, RecursoEnum.SERVIDOR_BD_HD, "", entrada, 
				geradoresTempoServico, geradoresTempoPensamento);
		ligar(RecursoEnum.SERVIDOR_JOSSO_GATEWAY_HD, "", RecursoEnum.SERVIDOR_BD_CPU, "");
		ligar(RecursoEnum.SERVIDOR_APLICACAO_HD, "", RecursoEnum.SERVIDOR_BD_CPU, "");
		ligar(RecursoEnum.SERVIDOR_BD_CPU, "", RecursoEnum.SERVIDOR_BD_HD, "");
		ligar(RecursoEnum.SERVIDOR_BD_HD, "", RecursoEnum.SERVIDOR_APLICACAO_CPU, "");
		
		Sim_system.set_termination_condition(Sim_system.TIME_ELAPSED, tempoSimulacao, true);

		if (transitorioInicial != 0.0) {
        	Sim_system.set_transient_condition(Sim_system.TIME_ELAPSED, transitorioInicial);
		}

//      Sim_system.set_output_analysis(Sim_system.IND_REPLICATIONS, 5, 0.95);
        
		Sim_system.generate_graphs(isGeracaoGraficos());
		Sim_system.run();

		if (isGeracaoGraficos() && isVisualizarGraficos()) {
			if (new File("sim_graphs.sjg").exists()) {
				SJGV.main(new String[] { "sim_graphs.sjg" });
			} else {
				SJGV.main(new String[] {});
			}
		}
	}

	protected void ligar(RecursoEnum recurso1, String sufixoRecurso1, RecursoEnum recurso2, String sufixoRecurso2) {
		String nomeRecurso1 = EntidadesFactory.getNomeRecurso(recurso1, sufixoRecurso1);
		String saida1 = ConstantesSimulador.getNomePortaSaida(recurso2);
		String nomeRecurso2 = EntidadesFactory.getNomeRecurso(recurso2, sufixoRecurso2);
		String entrada2 = ConstantesSimulador.PORTA_ENTRADA;
		
		System.out.println("Ligando " + nomeRecurso1 + "." + saida1 +
				" a " + nomeRecurso2 + "." + entrada2);
		Sim_system.link_ports(nomeRecurso1, saida1, nomeRecurso2, entrada2);
	}

	protected NoCBMG getCbmgUsoMedio() {
		NoCBMG entrada = new NoCBMG("e", "Entrada", null);
		NoCBMG acessarPaginaInicial = new NoCBMG("p", "Acessar página inicial", OperacaoEnum.ACESSAR_PAGINA_INICIAL);
		NoCBMG efetuarLogin = new NoCBMG("l", "Efetuar login", OperacaoEnum.EFETUAR_LOGIN);
		NoCBMG recuperarSenha = new NoCBMG("r", "Recuperar senha", OperacaoEnum.RECUPERAR_SENHA);
		NoCBMG acessarPaginaInicialRestrita = new NoCBMG("g", "Acessar página inicial restrita", OperacaoEnum.ACESSAR_PAGINA_INICIAL_RESTRITA);
		NoCBMG acessarUmSistema = new NoCBMG("a", "Acessar um sistema", OperacaoEnum.ACESSAR_UM_SISTEMA);
		NoCBMG alterarSenha = new NoCBMG("t", "Alterar senha", OperacaoEnum.ALTERAR_SENHA);
		NoCBMG efetuarLogout = new NoCBMG("o", "Efetuar logout", OperacaoEnum.EFETUAR_LOGOUT);
		NoCBMG saida = new NoCBMG("s", "Saída", null);
		
		entrada.addArco(acessarPaginaInicial, 100);

		acessarPaginaInicial.addArco(recuperarSenha, 20);
		acessarPaginaInicial.addArco(efetuarLogin, 70);
		acessarPaginaInicial.addArco(saida, 10);
		
		recuperarSenha.addArco(acessarPaginaInicial, 85);
		recuperarSenha.addArco(saida, 15);

		efetuarLogin.addArco(acessarPaginaInicialRestrita, 90);
		efetuarLogin.addArco(saida, 10);

		acessarPaginaInicialRestrita.addArco(alterarSenha, 10);
		acessarPaginaInicialRestrita.addArco(acessarUmSistema, 80);
		acessarPaginaInicialRestrita.addArco(efetuarLogout, 5);
		acessarPaginaInicialRestrita.addArco(saida, 5);

		alterarSenha.addArco(acessarPaginaInicialRestrita, 80);
		alterarSenha.addArco(saida, 20);

		acessarUmSistema.addArco(acessarPaginaInicialRestrita, 60);
		acessarUmSistema.addArco(saida, 40);

		efetuarLogout.addArco(saida, 100);

		return entrada;
	}

	protected Map<OperacaoEnum, ContinuousGenerator> getGeradoresTempoPensamento() {
		Map<OperacaoEnum, ContinuousGenerator> geradoresTempoPensamento = new HashMap<OperacaoEnum, ContinuousGenerator>();

		geradoresTempoPensamento.put(OperacaoEnum.ACESSAR_PAGINA_INICIAL, new Sim_negexp_obj(UUID.randomUUID().toString(), 10.0));
		geradoresTempoPensamento.put(OperacaoEnum.RECUPERAR_SENHA, new Sim_negexp_obj(UUID.randomUUID().toString(), 25.0));
		geradoresTempoPensamento.put(OperacaoEnum.EFETUAR_LOGIN, new Sim_negexp_obj(UUID.randomUUID().toString(), 40.0));
		geradoresTempoPensamento.put(OperacaoEnum.ACESSAR_PAGINA_INICIAL_RESTRITA, new Sim_negexp_obj(UUID.randomUUID().toString(), 10.0));
		geradoresTempoPensamento.put(OperacaoEnum.ALTERAR_SENHA, new Sim_negexp_obj(UUID.randomUUID().toString(), 30.0));
		geradoresTempoPensamento.put(OperacaoEnum.ACESSAR_UM_SISTEMA, new Sim_negexp_obj(UUID.randomUUID().toString(), 15.0));
		geradoresTempoPensamento.put(OperacaoEnum.EFETUAR_LOGOUT, new Sim_negexp_obj(UUID.randomUUID().toString(), 0.000000000001));
		
		return geradoresTempoPensamento;
	}

	protected Map<OperacaoEnum, List<ContinuousGenerator>> getGeradoresTempoServico() {
		Map<OperacaoEnum, List<ContinuousGenerator>> geradoresTempoServico = new HashMap<OperacaoEnum, List<ContinuousGenerator>>();
		
		geradoresTempoServico.put(OperacaoEnum.ACESSAR_PAGINA_INICIAL, 
				Arrays.<ContinuousGenerator>asList(
//						RecursoEnum.SERVIDOR_APLICACAO_CPU, 
//						RecursoEnum.SERVIDOR_APLICACAO_HD,
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 200), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 175)
				));

		geradoresTempoServico.put(OperacaoEnum.RECUPERAR_SENHA, 
				Arrays.<ContinuousGenerator>asList(
//						RecursoEnum.SERVIDOR_APLICACAO_CPU, 
//						RecursoEnum.SERVIDOR_APLICACAO_HD,
//						RecursoEnum.SERVIDOR_BD_CPU,
//						RecursoEnum.SERVIDOR_BD_HD
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 75), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 60), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 50), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 30)
				));

		geradoresTempoServico.put(OperacaoEnum.EFETUAR_LOGIN, 
				Arrays.<ContinuousGenerator>asList(
//						RecursoEnum.SERVIDOR_APLICACAO_CPU, 
//						RecursoEnum.SERVIDOR_APLICACAO_HD,
//						RecursoEnum.SERVIDOR_JOSSO_AGENT_CPU,
//						RecursoEnum.SERVIDOR_JOSSO_AGENT_HD,
//						RecursoEnum.SERVIDOR_JOSSO_GATEWAY_CPU,
//						RecursoEnum.SERVIDOR_JOSSO_GATEWAY_HD,
//						RecursoEnum.SERVIDOR_BD_CPU,
//						RecursoEnum.SERVIDOR_BD_HD
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 50), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 45), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 150), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 120), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 125), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 100), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 60), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 40)
				));
		
		geradoresTempoServico.put(OperacaoEnum.ACESSAR_PAGINA_INICIAL_RESTRITA, 
				Arrays.<ContinuousGenerator>asList(
//						RecursoEnum.SERVIDOR_APLICACAO_CPU, 
//						RecursoEnum.SERVIDOR_APLICACAO_HD,
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 150), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 125)
				));

		geradoresTempoServico.put(OperacaoEnum.ALTERAR_SENHA, 
				Arrays.<ContinuousGenerator>asList(
//						RecursoEnum.SERVIDOR_APLICACAO_CPU, 
//						RecursoEnum.SERVIDOR_APLICACAO_HD,
//						RecursoEnum.SERVIDOR_BD_CPU,
//						RecursoEnum.SERVIDOR_BD_HD
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 100), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 75), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 50), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 30)
				));

		geradoresTempoServico.put(OperacaoEnum.ACESSAR_UM_SISTEMA, 
				Arrays.<ContinuousGenerator>asList(
//						RecursoEnum.SERVIDOR_APLICACAO_CPU, 
//						RecursoEnum.SERVIDOR_APLICACAO_HD,
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 200), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 175)
				));

		geradoresTempoServico.put(OperacaoEnum.EFETUAR_LOGOUT, 
				Arrays.<ContinuousGenerator>asList(
//						RecursoEnum.SERVIDOR_APLICACAO_CPU, 
//						RecursoEnum.SERVIDOR_APLICACAO_HD,
//						RecursoEnum.SERVIDOR_JOSSO_AGENT_CPU,
//						RecursoEnum.SERVIDOR_JOSSO_AGENT_HD,
//						RecursoEnum.SERVIDOR_JOSSO_GATEWAY_CPU,
//						RecursoEnum.SERVIDOR_JOSSO_GATEWAY_HD,
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 125), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 100), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 180), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 150), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 150), 
						new Sim_negexp_obj(UUID.randomUUID().toString(), 1.0 / 120)
				));
		
		return geradoresTempoServico;
	}
	
	public void setTempoSimulacao(double tempoSimulacao) {
		this.tempoSimulacao = tempoSimulacao;
	}

	public double getTempoSimulacao() {
		return tempoSimulacao;
	}

	public void setSemente(long semente) {
		this.semente = semente;
	}
	
	public double getUtilizacaoMedia(String name) {
		return getSimStat(name).average(Sim_stat.UTILISATION);
	}
	
	public double getVazaoMedia(String name) {
		return getSimStat(name).average(Sim_stat.THROUGHPUT);
	}
	
	public double getTaxaMediaChegada(String name) {
		return getSimStat(name).average(Sim_stat.ARRIVAL_RATE);
	}
	
	public double getTamanhoMedioFila(String name) {
		return getSimStat(name).average(Sim_stat.QUEUE_LENGTH);
	}
	
	public double getTempoMedioResposta(String name) {
		return getSimStat(name).average(Sim_stat.RESIDENCE_TIME);
	}
	
	public double getTempoMedioServico(String name) {
		return getSimStat(name).average(Sim_stat.SERVICE_TIME);
	}
	
	public double getTempoMedioEspera(String name) {
		return getSimStat(name).average(Sim_stat.WAITING_TIME);
	}

	public int getNumeroClientesChegaram(String name) {
		return getSimStat(name).count(Sim_stat.ARRIVAL_RATE);
	}

	public int getNumeroClientesProcessados(String name) {
		return getSimStat(name).count(Sim_stat.THROUGHPUT);
	}

	public boolean isGeracaoGraficos() {
		return geracaoGraficos;
	}

	public void setGeracaoGraficos(boolean generatingGraphs) {
		this.geracaoGraficos = generatingGraphs;
	}	
	
	private Sim_stat getSimStat(String name) {
		return Sim_system.get_entity(name).get_stat();
	}
	

	private long initSemente() {
		long candidatoAPrimo = (long) (Math.random()*10000000);
		
		if (candidatoAPrimo % 2 == 0)
			candidatoAPrimo++;
		
		while (!isPrimo(candidatoAPrimo)) {
			candidatoAPrimo += 2;
		}
		return candidatoAPrimo;
	}

	private boolean isPrimo(long candidatoAPrimo) {
		double raiz = Math.sqrt(candidatoAPrimo);
		for (int i = 3; i <= raiz; i++) {
			if (candidatoAPrimo % i == 0)
				return false;
		}
		return true;
	}

	public void setVisualizarGraficos(boolean visualizarGraficos) {
		this.visualizarGraficos  = visualizarGraficos;
	}

	public boolean isVisualizarGraficos() {
		return visualizarGraficos;
	}

	public double getTransitorioInicial() {
		return transitorioInicial;
	}

	public void setTransitorioInicial(double transitorioInicial) {
		this.transitorioInicial = transitorioInicial;
	}
	
	public double getTaxaEntrada() {
		return taxaEntrada;
	}

	public void setTaxaEntrada(double taxaEntrada) {
		this.taxaEntrada = taxaEntrada;
	}

	public static void main(String[] args) throws Throwable {
		// valores padrao
		boolean criarArquivo = true;
		double tempoSimulacao = 600;
		double taxaEntrada = 10;
		
		if (args.length == 3) {
			criarArquivo = Boolean.parseBoolean(args[0]);
			tempoSimulacao = Double.parseDouble(args[1]);
			taxaEntrada = Double.parseDouble(args[2]);
		}
		
		SimuladorPSIUsoMedio simuladorPSIUsoMedio = new SimuladorPSIUsoMedio();
		simuladorPSIUsoMedio.setTaxaEntrada(taxaEntrada);
		simuladorPSIUsoMedio.setVisualizarGraficos(false);
		simuladorPSIUsoMedio.setTempoSimulacao(tempoSimulacao);
		simuladorPSIUsoMedio.simular();
		
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("saida.dat", !criarArquivo));
		String lineSeparator = System.getProperty("line.separator");
		if (criarArquivo) {
			writer.write("taxaEntrada\tmediaSessoes\tmaximoSessoes\tmediaTempoSessao\tmediaTempoResposta");
			
			for (int i = 0; i < Sim_system.get_num_entities(); i++) {
				String nome = Sim_system.get_entity(i).get_name();
				nome = Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
				String coluna = "utilizacao" + nome;
				writer.write("\t" + coluna);
			}
			
			for (OperacaoEnum operacao : OperacaoEnum.values()) {
				String nome = operacao.getSigla();
				nome = Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
				String coluna = "tempoResposta" + nome;
				writer.write("\t" + coluna);
			}
			
			writer.write(lineSeparator);
		}
		
		writer.write(taxaEntrada + "\t" + 
				EstatisticasUtil.getInstance().getMediaSessoes() + "\t" + 
				EstatisticasUtil.getInstance().getMaximoSessoesExistentes() + "\t" + 
				EstatisticasUtil.getInstance().getMediaTempoSessao() + "\t" + 
				EstatisticasUtil.getInstance().getMediaTempoResposta());
		
		for (int i = 0; i < Sim_system.get_num_entities(); i++) {
			String nome = Sim_system.get_entity(i).get_name();
			writer.write("\t" + Sim_system.get_entity(nome).get_stat().average(Sim_stat.UTILISATION));
		}
		
		for (OperacaoEnum operacao : OperacaoEnum.values()) {
			writer.write("\t" + EstatisticasUtil.getInstance().getMediaTempoResposta(operacao));
		}
		
		writer.write(lineSeparator);
		writer.close();
	}
	
}
