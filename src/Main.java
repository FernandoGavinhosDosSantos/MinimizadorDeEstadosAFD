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

    private static String[][] getMatrizTransposta(String[][] matriz, int ordem) {
        String[][] novaMatrizAdj = new String[ordem][ordem];

        for (int source = 0; source < ordem; source++) {
            for (int destination = 0; destination < ordem; destination++) {
                novaMatrizAdj[source][destination] = matriz[destination][source];
            }
        }
        return novaMatrizAdj;
    }

    private static ArrayList<Integer> DFS(int inicial, String[][] matriz, ArrayList<Integer> visitado) {

        Stack<Integer> pilha = new Stack<>();

        pilha.push(inicial);

        // Percorre a matriz enquanto a pilha não estiver vazia
        while (!pilha.isEmpty()) {
            int estado = pilha.pop();
            visitado.add(estado);

            for (int i = 0; i < matriz.length; i++) {
                // Se o estado nao estiver na pilha ou nos visitados, adicione na pilha
                if (matriz[estado][i] != null) {
                    if (!pilha.contains(i) && !visitado.contains(i)) {
                        pilha.push(i);
                    }
                }
            }
        }

        return visitado;
    }

    private static void getEstadosInacessiveis(ArrayList<Integer> estadosInacessiveis) {
        // Assume inicialmente que todos os estados são inacessíveis
        for (int i = 0; i < saidas.length; i++) {
            estadosInacessiveis.add(i);
        }

        ArrayList<Integer> visitado = DFS(ini, matrizAdj, new ArrayList<>());

        // Retira os estados que conseguiram ser visitados com a busca em profundidade
        estadosInacessiveis.removeAll(visitado);
    }

    private static void getEstadosInuteis(ArrayList<Integer> estadosInuteis) {
        // Assume inicialmente que todos os estados são inúteis
        for (int i = 0; i < saidas.length; i++) {
            estadosInuteis.add(i);
        }

        ArrayList<Integer> visitado = new ArrayList<>();
        String[][] matrizTransposta = getMatrizTransposta(matrizAdj, saidas.length);

        // Itera o DFS colocando cada estado de aceitacao como estado inicial
        for (int i = 0; i < saidas.length; i++) {
            if (saidas[i]) {
                DFS(i, matrizTransposta, visitado);
            }
        }

        // Retira os estados que conseguiram ser visitados com a busca em profundidade
        estadosInuteis.removeAll(visitado);
    }

    private static void removeEstados(ArrayList<Integer> estadosParaRemover) {
        int tamanhoLista = estadosParaRemover.size();

        if (tamanhoLista == 0) return;

        String[][] novaMatrizAdj = new String[saidas.length - tamanhoLista][saidas.length - tamanhoLista];
        boolean[] novaSaida = new boolean[saidas.length - tamanhoLista];

        for (int i = 0; i < matrizAdj.length; i++) {
            // Pula o estado na linha i que esta inacessível
            if (!estadosParaRemover.contains(i)) {

                for (int j = 0; j < matrizAdj[i].length; j++) {

                    // Pula o estado na coluna j que esta inacessível
                    if (!estadosParaRemover.contains(j)) {
                        int auxI = i > tamanhoLista ? i - tamanhoLista : i;
                        int auxJ = j > tamanhoLista ? j - tamanhoLista : j;

                        novaMatrizAdj[auxI][auxJ] = matrizAdj[i][j];
                    }
                }
            }
        }
        matrizAdj = novaMatrizAdj;

        for (int i = 0; i < saidas.length; i++) {
            if (!estadosParaRemover.contains(i)) {
                int auxI = i > tamanhoLista ? i - tamanhoLista : i;
                novaSaida[auxI] = saidas[i];
            }
        }
        saidas = novaSaida;
    }

    private static void printEstados(ArrayList<Integer> estados, String label) {
        if (estados.size() == 0) {
            System.out.println("Não existem estados " + label);
            return;
        }

        System.out.print("Estados " + label + ": ");
        for (Integer estado : estados) {
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
        ArrayList<Integer> estadosInuteis = new ArrayList<>();

        LeEntrada(args[0]);
        printMatrizAdj();

        getEstadosInacessiveis(estadosInacessiveis);
        printEstados(estadosInacessiveis, "inacessíveis");
        removeEstados(estadosInacessiveis);

        getEstadosInuteis(estadosInuteis);
        printEstados(estadosInuteis, "inúteis");
        removeEstados(estadosInuteis);

        printMatrizAdj();
    }
}