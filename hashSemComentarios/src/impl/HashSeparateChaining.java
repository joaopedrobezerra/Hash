package impl;

import core.*;

public class HashSeparateChaining implements HashTable {
    private static class Node {
        int key;
        Node next;

        Node(int k) {
            key = k;
        }
    }

    private final Node[] table;
    private final int M;

    public HashSeparateChaining(int capacity) {
        this.M = capacity;
        this.table = new Node[M];
    }

    private int h(int x) {
        int v = x % M;
        return (v < 0) ? v + M : v;
    }

    @Override
    public void insert(int key, Metrics m) {
        int idx = h(key);
        Node head = table[idx];
        
        Node cur = head;
        while (cur != null) {
            if (cur.key == key) {
                return;
            }
            cur = cur.next;
        }
        
        if (head != null) {
            m.collisionsInsert++;
        }
        
        Node n = new Node(key);
        n.next = head;
        table[idx] = n;
    }

    @Override
    public boolean contains(int key, Metrics m) {
        int idx = h(key);
        Node cur = table[idx];
        
        int comparisons = 0;
        while (cur != null) {
            comparisons++;
            if (cur.key == key) {
                m.collisionsSearch += comparisons;
                return true;
            }
            cur = cur.next;
        }
        m.collisionsSearch += comparisons;
        return false;
    }

    @Override
    public int capacity() {
        return M;
    }

    @Override
    public int[] bucketSizesOrNull() {
        int[] sizes = new int[M];
        for (int i = 0; i < M; i++) {
            Node cur = table[i];
            int count = 0;
            while (cur != null) {
                count++;
                cur = cur.next;
            }
            sizes[i] = count;
        }
        return sizes;
    }

    @Override
    public int[] gapStatsOrNull() {
        return null;
    }
}
