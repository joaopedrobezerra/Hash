package util;

import java.io.*;

public class Reporter {
    private final File file;

    public Reporter(String csvPath) {
        this.file = new File(csvPath);
    }

    public void writeHeader() throws IOException {
        if (!file.exists()) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                fw.write("metodo,M,n,insert_ms,search_ms,collisions_insert,collisions_search,top1chain,top2chain,top3chain,gap_min,gap_max,gap_avg\n");
            } finally {
                if (fw != null) {
                    fw.close();
                }
            }
        }
    }

    public void appendLine(String line) throws IOException {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
            fw.write(line);
            fw.write("\n");
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }
}
