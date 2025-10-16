package util;

import java.io.*;

public class Reporter {
    private final File file; // Arquivo CSV onde os resultados serão salvos.

    public Reporter(String csvPath) {
        this.file = new File(csvPath); // Cria objeto File com o caminho do arquivo CSV.
    }

    public void writeHeader() throws IOException {
        if (!file.exists()) { // Verifica se o arquivo não existe para evitar sobrescrever dados existentes.
            FileWriter fw = null; // Declara FileWriter.
            try {
                fw = new FileWriter(file); // Cria FileWriter para escrever no arquivo.
                fw.write("metodo,M,n,insert_ms,search_ms,collisions_insert,collisions_search,top1chain,top2chain,top3chain,gap_min,gap_max,gap_avg\n"); // Escreve o cabeçalho do CSV com todas as colunas.
            } finally {
                if (fw != null) { // Se FileWriter foi criado.
                    fw.close(); // Fecha o arquivo.
                }
            }
        }
    }

    public void appendLine(String line) throws IOException {
        FileWriter fw = null; // Declara FileWriter.
        try {
            fw = new FileWriter(file, true); // Cria FileWriter em modo append (adiciona ao final do arquivo).
            fw.write(line); // Escreve a linha de dados.
            fw.write("\n"); // Adiciona quebra de linha.
        } finally {
            if (fw != null) { // Se FileWriter foi criado.
                fw.close(); // Fecha o arquivo.
            }
        }
    }
}