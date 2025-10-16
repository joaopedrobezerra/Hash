package util;

import java.io.*;
import java.util.Random;

public class DataGen {
    public static void generateDatasetFile(String path, long seed, long count) throws IOException {
        Random rnd = new Random(seed);
        FileWriter fw = null;
        try {
            fw = new FileWriter(path);
            for (long i = 0; i < count; i++) {
                int code = rnd.nextInt(1_000_000_000);
                fw.write(Integer.toString(code));
                fw.write("\n");
            }
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }

    public static void forEachCode(String path, CodeConsumer consumer) throws IOException {
        FileReader fr = null;
        try {
            fr = new FileReader(path);
            int c;
            String line = "";
            while ((c = fr.read()) != -1) {
                if (c == '\n') {
                    if (!line.isEmpty()) {
                        consumer.accept(Integer.parseInt(line.trim()));
                    }
                    line = "";
                } else {
                    line += (char) c;
                }
            }
            if (!line.isEmpty()) {
                consumer.accept(Integer.parseInt(line.trim()));
            }
        } finally {
            if (fr != null) {
                fr.close();
            }
        }
    }

    public static class CodeConsumer {
        public void accept(int code) {
        }
    }
}
