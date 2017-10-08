import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	private static String[][] matrizAdj;	// representação do AFD por matriz de adjacência
	private static boolean[] saidas;		// indica quais estados são de aceitação
	private static int sigma;				// tamanho do alfabeto
	private static int ini;					// estado inicial

	public static void LerEntrada(String dir){

		//leitor de arquivos
		File file = new File(dir);
		BufferedReader reader = null;

		try {

			// lê a primeira linha da entrada
			reader = new BufferedReader(new FileReader(file));
			String linha = reader.readLine();

			// quebra a linha lida em parâmetros separando pelo caractere espaço
			String[] param = linha.split(" ");

			// inicializa a matriz de adjacência e de aceitação com tamanho n
			int n = Integer.parseInt(param[0]);
			matrizAdj = new String[n][n];
			saidas = new boolean[n];

			// armazena o sigma (tamanho do alfabeto)
			sigma = Integer.parseInt(param[1]);

			// grava estado de entrada
			ini = Integer.parseInt(param[2]);

			// lê segunda linha
			linha = reader.readLine();
			param = linha.split(" ");

			// grava estados de aceitação
			for (int i = 0; i < n; i++){
				saidas[i] = (param[i].equals("1"));
			}

			// lê as demais linhas que contém informações sobre as transições
			for (int i = 0; i < n; i++){

				linha = reader.readLine();
				param = linha.split(" ");
				
				for (int j = 0; j < sigma; j++){
					
					//se o valor for -1, não há transição a ser adicionada
					if (!param[j].equals("-1")) {
						
						//qualquer outro valor significa que há uma transição partindo do estado i e chegando no estado param[j] ao receber o valor j
						int y = Integer.parseInt(param[j]);
						if (matrizAdj[i][y] == null) matrizAdj[i][y] = "";
						matrizAdj[i][y] += (matrizAdj[i][y].isEmpty() ? j : ";" + j);
					}
				}
			}

		//tratamento das exceções
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {

				if (reader != null) reader.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		LerEntrada(args[0]);

		//saída de teste
		String linha = "";
		for (int i = 0; i < matrizAdj.length; i++){
			for (int j = 0; j < matrizAdj.length; j++){
				if (matrizAdj[i][j] == null) matrizAdj[i][j] = "-1";
				linha += matrizAdj[i][j] + " ";
			}
			System.out.println(linha);
			linha = "";
		}
	}
}