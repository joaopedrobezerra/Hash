
import core.*;
import impl.*;
import java.io.File;
import util.*;

public class MainRunner {

    // Tamanhos da tabela hash
    static final int[] M_LIST = { 1000, 10000, 100000 }; // Uilizamos final para impossibilitar que os valores sejam alterados durante a execução do programa.
    // Quantidade de registros em cada dataset
    static final long[] N_LIST = { 100000L, 1000000L, 10000000L }; //  Os conjuntos de dados são diferentes para termos escalas diferentes de teste, uma pequena, outra media e outra grande.
    // Seeds fixas (para reprodutibilidade)
    static final long[] SEEDS = { 42L, 43L, 44L }; // Seeds para gerar os datasets de forma consistente.

    static final String DATA_DIR = "datasets";
    static final String OUT_CSV = "resultados.csv";

    public static void main(String[] args) throws Exception { // Usamos throws Exceptions para evitar try-catch em todo lugar, assim polui menos o codigo.
        // Garante que a pasta datasets exista
        File dir = new File(DATA_DIR);
        if (!dir.exists())
            dir.mkdirs();

        // Cria o arquivo CSV de resultados
        Reporter rep = new Reporter(OUT_CSV);
        rep.writeHeader();

        // Gera os datasets se não existirem
        for (int i = 0; i < N_LIST.length; i++) {
            String path = DATA_DIR + "/dataset_" + (i + 1) + ".txt";
            if (!new File(path).exists()) {
                System.out.println("Gerando dataset: " + path + " (n=" + N_LIST[i] + ")");
                DataGen.generateDatasetFile(path, SEEDS[i], N_LIST[i]);
            }
        }

        // Três métodos de hash para testar
        String[] methods = { "Encadeamento", "Linear", "Duplo" };

        // Loop principal
        for (String metodo : methods) {
            for (int M : M_LIST) {
                for (int d = 0; d < N_LIST.length; d++) {
                    long N = N_LIST[d];
                    String path = DATA_DIR + "/dataset_" + (d + 1) + ".txt";

                    System.out.println("\nMétodo: " + metodo + " | M = " + M + " | N = " + N);

                    // Cria tabela hash conforme o método
                    HashTable table; // Usamos interface HashTable para evitar dependência de implementação.
                    if (metodo.equals("Encadeamento"))
                        table = new HashSeparateChaining(M); // Cria tabela hash com encadeamento
                    else if (metodo.equals("Linear"))
                        table = new HashOpenAddressingLinear(M); // Cria tabela hash com endereçamento linear
                    else
                        table = new HashOpenAddressingDouble(M); // Cria tabela hash com endereçamento duplo

                    Metrics metrics = new Metrics(); // Cria objeto Metrics para armazenar as métricas.

                    // Inserção
                    long t0 = System.nanoTime();
                    DataGen.forEachCode(path, new DataGen.CodeConsumer() { // Cria classe anônima para inserção.
                        public void accept(int code) {
                            table.insert(code, metrics); // Insere cada código no dataset na tabela hash.
                        }
                    });
                    long t1 = System.nanoTime();
                    metrics.insertTimeNs = t1 - t0;

                    // Busca
                    long s0 = System.nanoTime();
                    DataGen.forEachCode(path, new DataGen.CodeConsumer() { // Cria classe anônima para busca.
                        public void accept(int code) {
                            table.contains(code, metrics); // Busca cada código no dataset na tabela hash.
                        }
                    });
                    long s1 = System.nanoTime();
                    metrics.searchTimeNs = s1 - s0;

                    // Coleta estatísticas específicas
                    int top1 = 0, top2 = 0, top3 = 0; // Variáveis para armazenar os top 3 tamanhos das listas encadeadas.
                    int gapMin = 0, gapMax = 0; // Variáveis para armazenar a maior e menor distancia entre os dadosSAI.
                    String gapAvg = ""; // Distancia media

                    int[] bucketSizes = table.bucketSizesOrNull();
                    if (bucketSizes != null) {
                        // top 3 listas encadeadas
                        int a = 0, b = 0, c = 0;
                        for (int v : bucketSizes) {
                            if (v > a) { // Se o tamanho da lista for maior que a, atualiza os top 3.
                                c = b;
                                b = a;
                                a = v;
                            } else if (v > b) { // Se o tamanho da lista for maior que b, atualiza o top 3.
                                c = b;
                                b = v;
                            } else if (v > c) // Se o tamanho da lista for maior que c, atualiza o top 3.
                                c = v;
                        }
                        top1 = a; // Atribui o maior valor ao top 1.
                        top2 = b; // Atribui o segundo maior valor ao top 2.
                        top3 = c; // Atribui o terceiro maior valor ao top 3.
                    } else { // Se a tabela hash não for encadeada, coleta estatísticas de gaps.
                        int[] gaps = table.gapStatsOrNull(); // Coleta estatísticas de gaps.
                        if (gaps != null) { 
                            gapMin = gaps[0]; // Atribui o menor valor ao gap min.
                            gapMax = gaps[1]; // Atribui o maior valor ao gap max.
                            if (gaps[2] == 0) {
                                gapAvg = "0.000"; // Se não há gaps, define como 0.000.
                            } else {
                                int avgInt = gaps[2] / 1000; // Atribui o valor médio ao gap avg.
                                int frac = gaps[2] % 1000; // Atribui o valor da fração ao gap avg.
                                gapAvg = avgInt + "." + String.format("%03d", frac); // Atribui o valor da fração ao gap avg.
                            }
                        }
                    }

                    long insertMs = metrics.insertTimeNs / 1_000_000; // Atribui o tempo de inserção em milissegundos(ms).
                    long searchMs = metrics.searchTimeNs / 1_000_000; // Atribui o tempo de busca em milissegundos(ms).

                    // Monta linha CSV
                    String line = metodo + "," + M + "," + N + "," + insertMs + "," + searchMs + "," + 
                                 metrics.collisionsInsert + "," + metrics.collisionsSearch + "," + 
                                 top1 + "," + top2 + "," + top3 + "," + gapMin + "," + gapMax + "," + gapAvg; // Monta linha CSV usando concatenação de String.

                    rep.appendLine(line); // Adiciona linha ao arquivo CSV.

                    // Log no console
                    System.out.println("Inserção: " + insertMs + " ms | Busca: " + searchMs + " ms");
                    System.out.println(
                            "Colisões Inserção/Busca: " + metrics.collisionsInsert + "/" + metrics.collisionsSearch);
                    if (bucketSizes != null)
                        System.out.println("Top 3 listas: " + top1 + ", " + top2 + ", " + top3);
                    else
                        System.out.println("Gaps min/max/médio: " + gapMin + " / " + gapMax + " / " + gapAvg);
                }
            }
        }

        System.out.println("\n✅ Execução concluída. Resultados salvos em: " + OUT_CSV);
    }
}
