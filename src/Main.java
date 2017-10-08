import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;

public class Main {

    private static String[][] matrizAdj;    // representa��o do AFD por matriz de adjac�ncia
    private static boolean[] saidas;        // indica quais estados s�o de aceita��o
    private static int sigma;                // tamanho do alfabeto
    private static int ini;                    // estado inicial

    private static void LeEntrada(String dir) {

        //leitor de arquivos
        File file = new File(dir);
        BufferedReader reader = null;

        try {

            // l� a primeira linha da entrada
            reader = new BufferedReader(new FileReader(file));
            String linha = reader.readLine();

            // quebra a linha lida em par�metros separando pelo caractere espa�o
            String[] param = linha.split(" ");

            // inicializa a matriz de adjac�ncia e de aceita��o com tamanho n
            int n = Integer.parseInt(param[0]);
            matrizAdj = new String[n][n];
            saidas = new boolean[n];

            // armazena o sigma (tamanho do alfabeto)
            sigma = Integer.parseInt(param[1]);

            // grava estado de entrada
            ini = Integer.parseInt(param[2]);

            // l� segunda linha
            linha = reader.readLine();
            param = linha.split(" ");

            // grava estados de aceita��o
            for (int i = 0; i < n; i++) {
                saidas[i] = (param[i].equals("1"));
            }

            // l� as demais linhas que cont�m informa��es sobre as transi��es
            for (int i = 0; i < n; i++) {

                linha = reader.readLine();
                param = linha.split(" ");

                for (int j = 0; j < sigma; j++) {

                    //se o valor for -1, n�o h� transi��o a ser adicionada
                    if (!param[j].equals("-1")) {

                        //qualquer outro valor significa que h� uma transi��o partindo do estado i e chegando no estado param[j] ao receber o valor j
                        int y = Integer.parseInt(param[j]);
                        if (matrizAdj[i][y] == null) matrizAdj[i][y] = "";
                        matrizAdj[i][y] += (matrizAdj[i][y].isEmpty() ? j : ";" + j);
                    }
                }
            }

            //tratamento das exce��es
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

    private static String TipoEstado(int i) {

        String tipoEstado;

        //testa se � estado inicial
        if (i == ini) tipoEstado = "I";
            //se n�o for, testa se � estado de aceita��o
        else if (saidas[i]) tipoEstado = "A";
            //se n�o, s� pode ser um estado neutro
        else tipoEstado = "N";

        return tipoEstado;
    }

    // Implementa a busca em profundidade (DFS) para determinar os estados inuteis
    private static void EstadosInuteis(ArrayList<Integer> estadosInuteis) {
        // Assume inicialmente que todos os estados sao inuteis
        for (int i = 0; i < saidas.length; i++) {
            estadosInuteis.add(i);
        }

        // Busca em profundidade para preencher um vetor de estados que conseguiram ser visitados
        Stack<Integer> pilha = new Stack<>();
        ArrayList<Integer> visitado = new ArrayList<>();

        pilha.push(ini);

        // Percorre a matriz enquanto a pilha n�o estiver vazia
        while (!pilha.isEmpty()) {
            int estado = pilha.pop();
            visitado.add(estado);

            for (int i = 0; i < matrizAdj.length; i++) {
                // Se o estado nao estiver na pilha ou nos visitados, adicione na pilha
                if (matrizAdj[estado][i] != null) {
                    if (!pilha.contains(i) && !visitado.contains(i)) {
                        pilha.push(i);
                    }
                }
            }
        }

        // Retira os estados que conseguiram ser visitados com a busca em profundidade
        for (Integer estado : visitado) {
            estadosInuteis.remove(estado);
        }
    }

    private static void TestaEstadosInuteis(ArrayList<Integer> estadosInuteis) {
        if (estadosInuteis.size() == 0) {
            System.out.println("Nao existe estados inuteis");
            return;
        }

        // Mostra os indices dos estados inuteis
        System.out.print("Estados inuteis: ");
        for (Integer estado : estadosInuteis) {
            System.out.print(estado + " ");
        }
        System.out.println();
    }
    private static void TestaConstrucao() {

        //sa�da para testar a constru��o da matriz de adjac�ncia
        String linha = "";
        for (int i = 0; i < matrizAdj.length; i++) {
            for (int j = 0; j < matrizAdj.length; j++) {
                //se n�o houver nada em matriz[i,j], n�o h� uma transi��o saindo do estado i e indo para o estado j
                if (matrizAdj[i][j] == null) matrizAdj[i][j] = "-1";
                //(origemTransi��o [tipoEstado] -> destinoTransi��o [tipoEstado]) {valorTransi��o1;valorTransi��o2,...,valorTransi��oN}
                linha += "(" + i + " [" + TipoEstado(i) + "] -> " + j + " [" + TipoEstado(j) + "]) {" + matrizAdj[i][j] + "} ";
            }
            System.out.println(linha);
            linha = "";
        }
    }

    public static void main(String[] args) {

        ArrayList<Integer> estadosInuteis = new ArrayList<>();

        LeEntrada(args[0]);
        EstadosInuteis(estadosInuteis);

        TestaEstadosInuteis(estadosInuteis);
        TestaConstrucao();
    }
}