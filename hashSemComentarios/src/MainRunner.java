
import core.*;
import impl.*;
import java.io.File;
import util.*;

public class MainRunner {

    static final int[] M_LIST = { 1000, 10000, 100000 };
    static final long[] N_LIST = { 100000L, 1000000L, 10000000L };
    static final long[] SEEDS = { 42L, 43L, 44L };

    static final String DATA_DIR = "datasets";
    static final String OUT_CSV = "resultados.csv";

    public static void main(String[] args) throws Exception {
        File dir = new File(DATA_DIR);
        if (!dir.exists())
            dir.mkdirs();

        Reporter rep = new Reporter(OUT_CSV);
        rep.writeHeader();

        for (int i = 0; i < N_LIST.length; i++) {
            String path = DATA_DIR + "/dataset_" + (i + 1) + ".txt";
            if (!new File(path).exists()) {
                System.out.println("Gerando dataset: " + path + " (n=" + N_LIST[i] + ")");
                DataGen.generateDatasetFile(path, SEEDS[i], N_LIST[i]);
            }
        }

        String[] methods = { "Encadeamento", "Linear", "Duplo" };

        for (String metodo : methods) {
            for (int M : M_LIST) {
                for (int d = 0; d < N_LIST.length; d++) {
                    long N = N_LIST[d];
                    String path = DATA_DIR + "/dataset_" + (d + 1) + ".txt";

                    System.out.println("\nMétodo: " + metodo + " | M = " + M + " | N = " + N);

                    HashTable table;
                    if (metodo.equals("Encadeamento"))
                        table = new HashSeparateChaining(M);
                    else if (metodo.equals("Linear"))
                        table = new HashOpenAddressingLinear(M);
                    else
                        table = new HashOpenAddressingDouble(M);

                    Metrics metrics = new Metrics();

                    long t0 = System.nanoTime();
                    DataGen.forEachCode(path, new DataGen.CodeConsumer() {
                        public void accept(int code) {
                            table.insert(code, metrics);
                        }
                    });
                    long t1 = System.nanoTime();
                    metrics.insertTimeNs = t1 - t0;

                    long s0 = System.nanoTime();
                    DataGen.forEachCode(path, new DataGen.CodeConsumer() {
                        public void accept(int code) {
                            table.contains(code, metrics);
                        }
                    });
                    long s1 = System.nanoTime();
                    metrics.searchTimeNs = s1 - s0;

                    int top1 = 0, top2 = 0, top3 = 0;
                    int gapMin = 0, gapMax = 0;
                    String gapAvg = "";

                    int[] bucketSizes = table.bucketSizesOrNull();
                    if (bucketSizes != null) {
                        int a = 0, b = 0, c = 0;
                        for (int v : bucketSizes) {
                            if (v > a) {
                                c = b;
                                b = a;
                                a = v;
                            } else if (v > b) {
                                c = b;
                                b = v;
                            } else if (v > c)
                                c = v;
                        }
                        top1 = a;
                        top2 = b;
                        top3 = c;
                    } else {
                        int[] gaps = table.gapStatsOrNull();
                        if (gaps != null) { 
                            gapMin = gaps[0];
                            gapMax = gaps[1];
                            if (gaps[2] == 0) {
                                gapAvg = "0.000";
                            } else {
                                int avgInt = gaps[2] / 1000;
                                int frac = gaps[2] % 1000;
                                gapAvg = avgInt + "." + String.format("%03d", frac);
                            }
                        }
                    }

                    long insertMs = metrics.insertTimeNs / 1_000_000;
                    long searchMs = metrics.searchTimeNs / 1_000_000;

                    String line = metodo + "," + M + "," + N + "," + insertMs + "," + searchMs + "," + 
                                 metrics.collisionsInsert + "," + metrics.collisionsSearch + "," + 
                                 top1 + "," + top2 + "," + top3 + "," + gapMin + "," + gapMax + "," + gapAvg;

                    rep.appendLine(line);

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

        System.out.println("\n Execução concluída. Resultados salvos em: " + OUT_CSV);
    }
}
