package br.ufcg.edu.simulador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GeradorEstatisticas {

	private static String CMD = "java -Xms256M -Xmx1024M -cp \"lib/easyaccept.jar:"
			+ "lib/simjava.jar:"
			+ "lib/SimuladorInternetBanking.jar:"
			+ "lib/SJGV.jar\" br.ufcg.edu.simulador.SimuladorPSIUsoMedio ";

	private static String getArgumentos(Object... argumentos) {
		if (argumentos.length == 0) {
			return "";
		} else if (argumentos.length == 1) {
			return argumentos[0] + "";
		}
		
		StringBuilder sb = new StringBuilder(20);
		sb.append(argumentos[0]);
		for (int i = 1; i < argumentos.length; i++) {
			sb.append(" " + argumentos[i]);
		}
		return sb.toString();
	}

	private static void ler(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String linha = null;
		
		while ((linha = reader.readLine()) != null) {
			System.out.println(linha);
		}
	}

	public static void main(String[] args) throws IOException {
		boolean adicionar = true;
		for (int i = 10; i <= 300; i += 5) {
			String argumentos = getArgumentos(adicionar, 3600, i / 10.0);
			Process process = Runtime.getRuntime().exec(CMD + argumentos);

			ler(process.getInputStream());
			ler(process.getErrorStream());
			
			process.destroy();
			adicionar = false;
		}
	}

}