package impl;

import core.*;

public class HashOpenAddressingLinear implements HashTable {
    private int[] table;
    private boolean[] used;
    private int M;
    private int size;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    public HashOpenAddressingLinear(int capacity) {
        this.M = capacity;
        this.table = new int[M];
        this.used = new boolean[M];
        this.size = 0;
    }

    private int h0(int x) {
        int v = x % M;
        return (v < 0) ? v + M : v;
    }

    private void rehash() {
        int[] oldTable = table;
        boolean[] oldUsed = used;
        int oldM = M;

        M = M * 2;
        table = new int[M];
        used = new boolean[M];
        size = 0;

        for (int i = 0; i < oldM; i++) {
            if (oldUsed[i]) {
                insertWithoutRehash(oldTable[i]);
            }
        }
    }

    private void insertWithoutRehash(int key) {
        int idx = h0(key);
        int i = 0;
        while (i < M) {
            int pos = (idx + i) % M;
            if (!used[pos]) {
                used[pos] = true;
                table[pos] = key;
                size++;
                return;
            }
            if (table[pos] == key) return;
            i++;
        }
    }

    @Override
    public void insert(int key, Metrics m) {
        if ((double) size / M >= LOAD_FACTOR_THRESHOLD) {
            rehash();
        }

        int idx = h0(key);
        int i = 0, col = 0;
        while (i < M) {
            int pos = (idx + i) % M;
            if (!used[pos]) {
                used[pos] = true;
                table[pos] = key;
                size++;
                m.collisionsInsert += col;
                return;
            }
            if (table[pos] == key) return;
            i++;
            col++;
        }
    }

    @Override
    public boolean contains(int key, Metrics m) {
        int idx = h0(key);
        int i = 0, col = 0;
        while (i < M) {
            int pos = (idx + i) % M;
            if (!used[pos]) {
                m.collisionsSearch += col;
                return false;
            }
            if (table[pos] == key) {
                m.collisionsSearch += col;
                return true;
            }
            i++;
            col++;
        }
        m.collisionsSearch += col;
        return false;
    }

    @Override
    public int capacity() {
        return M;
    }

    @Override
    public int[] bucketSizesOrNull() {
        return null;
    }

    @Override
    public int[] gapStatsOrNull() {
        int occ = 0;
        for (boolean b : used) if (b) occ++;
        if (occ <= 1) return new int[]{M - occ, M - occ, (M - occ) * 1000};
        
        if (occ >= M * 0.9) {
            int avgGap = (M - occ) * 1000 / Math.max(1, occ);
            return new int[]{0, M - occ, avgGap};
        }
        
        int gapMin = Integer.MAX_VALUE, gapMax = 0;
        long sum = 0;
        int count = 0;
        int start = -1;
        for (int i = 0; i < M; i++) if (used[i]) {
            start = i;
            break;
        }
        int prev = start;
        int i = (start + 1) % M;
        while (i != start) {
            if (used[i]) {
                int gap = (i > prev) ? i - prev - 1 : (M - prev - 1) + i;
                if (gap < gapMin) gapMin = gap;
                if (gap > gapMax) gapMax = gap;
                sum += gap;
                count++;
                prev = i;
            }
            i = (i + 1) % M;
        }
        int tail = (start > prev) ? start - prev - 1 : (M - prev - 1) + start;
        gapMin = Math.min(gapMin, tail);
        gapMax = Math.max(gapMax, tail);
        sum += tail;
        count++;
        
        if (count == 0) return new int[]{0, 0, 0};
        
        int avgX1000 = (int)((sum * 1000L) / count);
        return new int[]{gapMin, gapMax, avgX1000};
    }
}
