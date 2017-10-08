import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Main {

    private static String[][] matrizAdj;    // representação do AFD por matriz de adjacência
    private static boolean[] saidas;        // indica quais estados são de aceitação
    private static int sigma;                // tamanho do alfabeto
    private static int ini;                    // estado inicial

    private static void LeEntrada(String dir) {

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
            for (int i = 0; i < n; i++) {
                saidas[i] = (param[i].equals("1"));
            }

            // lê as demais linhas que contém informações sobre as transições
            for (int i = 0; i < n; i++) {

                linha = reader.readLine();
                param = linha.split(" ");

                for (int j = 0; j < sigma; j++) {

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

    private static String TipoEstado(int i) {

        String tipoEstado;

        //testa se é estado inicial
        if (i == ini) tipoEstado = "I";
            //se não for, testa se é estado de aceitação
        else if (saidas[i]) tipoEstado = "A";
            //se não, só pode ser um estado neutro
        else tipoEstado = "N";

        return tipoEstado;
    }

    // Implementa a busca em profundidade (DFS) para determinar os estados inacessíveis
    private static void getEstadosInacessiveis(ArrayList<Integer> estadosInacessiveis) {
        // Assume inicialmente que todos os estados são inacessíveis
        for (int i = 0; i < saidas.length; i++) {
            estadosInacessiveis.add(i);
        }

        // Busca em profundidade para preencher um vetor de estados que conseguiram ser visitados
        Stack<Integer> pilha = new Stack<>();
        ArrayList<Integer> visitado = new ArrayList<>();

        pilha.push(ini);

        // Percorre a matriz enquanto a pilha não estiver vazia
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
        estadosInacessiveis.removeAll(visitado);
    }

    private static void removeEstados(ArrayList<Integer> estadosParaRemover) {
        if (estadosParaRemover.size() == 0) return;

        String[][] novaMatrizAdj = new String[saidas.length - 1][saidas.length - 1];

        for (int i = 0; i < matrizAdj.length; i++) {
            // Pula o estado na linha i que esta inacessível
            if (!estadosParaRemover.contains(i)) {

                for (int j = 0; j < matrizAdj[i].length; j++) {

                    // Pula o estado na coluna j que esta inacessível
                    if (!estadosParaRemover.contains(j)) {
                        int auxI = i > estadosParaRemover.size() ? i - 1 : i;
                        int auxJ = j > estadosParaRemover.size() ? j - 1 : j;

                        novaMatrizAdj[auxI][auxJ] = matrizAdj[i][j];
                    }
                }
            }
        }
        matrizAdj = novaMatrizAdj;
    }

    private static void printEstadosInacessiveis(ArrayList<Integer> estadosInacessiveis) {
        if (estadosInacessiveis.size() == 0) {
            System.out.println("Não existem estados inacessíveis");
            return;
        }

        // Mostra os indices dos estados inacessíveis
        System.out.print("Estados inacessíveis: ");
        for (Integer estado : estadosInacessiveis) {
            System.out.print(estado + " ");
        }
        System.out.println();
    }

    private static void printMatrizAdj() {

        // saída para testar a construção da matriz de adjacência
        String linha = "";
        for (int i = 0; i < matrizAdj.length; i++) {
            for (int j = 0; j < matrizAdj.length; j++) {
                //se não houver nada em matriz[i,j], não há uma transição saindo do estado i e indo para o estado j
                String valor = matrizAdj[i][j];
                if (valor == null) valor = "-1";
                //(origemTransição [tipoEstado] -> destinoTransição [tipoEstado]) {valorTransição1;valorTransição2,...,valorTransiçãoN}
                linha += "(" + i + " [" + TipoEstado(i) + "] -> " + j + " [" + TipoEstado(j) + "]) {" + valor + "} ";
            }
            System.out.println(linha);
            linha = "";
        }
    }

    public static void main(String[] args) {

        ArrayList<Integer> estadosInacessiveis = new ArrayList<>();

        LeEntrada(args[0]);
        printMatrizAdj();

        getEstadosInacessiveis(estadosInacessiveis);
        printEstadosInacessiveis(estadosInacessiveis);
        removeEstados(estadosInacessiveis);

        printMatrizAdj();
    }
}