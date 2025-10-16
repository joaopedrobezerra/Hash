package core;

public interface HashTable {   
    void insert(int key, Metrics m);
    boolean contains(int key, Metrics m);
    int capacity();

    int[] bucketSizesOrNull();
    int[] gapStatsOrNull();
}
