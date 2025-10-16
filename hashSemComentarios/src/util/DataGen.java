package util;

import java.io.*;
import java.util.Random;

public class DataGen {
    public static void generateDatasetFile(String path, long seed, long count) throws IOException {
        Random rnd = new Random(seed); // Cria gerador de números aleatórios com seed fixa para reprodutibilidade.
        FileWriter fw = null; // Declara FileWriter.
        try {
            fw = new FileWriter(path); // Cria FileWriter para escrever no arquivo.
            for (long i = 0; i < count; i++) { // Gera a quantidade especificada de códigos.
                int code = rnd.nextInt(1_000_000_000); // Gera código aleatório de até 9 dígitos.
                fw.write(Integer.toString(code)); // Escreve o código no arquivo.
                fw.write("\n"); // Adiciona quebra de linha.
            }
        } finally {
            if (fw != null) { // Se FileWriter foi criado.
                fw.close(); // Fecha o arquivo.
            }
        }
    }

    public static void forEachCode(String path, CodeConsumer consumer) throws IOException {
        FileReader fr = null; // Declara FileReader.
        try {
            fr = new FileReader(path); // Cria FileReader para ler o arquivo.
            int c; // Caractere lido.
            String line = ""; // String para acumular linha.
            while ((c = fr.read()) != -1) { // Lê cada caractere do arquivo.
                if (c == '\n') { // Se encontrou quebra de linha.
                    if (!line.isEmpty()) { // Se a linha não está vazia.
                        consumer.accept(Integer.parseInt(line.trim())); // Converte a linha para inteiro e chama o consumer.
                    }
                    line = ""; // Limpa a linha.
                } else {
                    line += (char) c; // Adiciona caractere à linha.
                }
            }
            if (!line.isEmpty()) { // Se há uma última linha sem quebra.
                consumer.accept(Integer.parseInt(line.trim())); // Processa a última linha.
            }
        } finally {
            if (fr != null) { // Se FileReader foi criado.
                fr.close(); // Fecha o arquivo.
            }
        }
    }

    public static class CodeConsumer { // Classe concreta para processar cada código lido do arquivo.
        public void accept(int code) { // Método que recebe um código e processa conforme necessário.
            // Implementação vazia - será sobrescrito pelo lambda.
        }
    }
}