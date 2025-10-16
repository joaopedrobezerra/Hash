package core;

// Esse codigo é uma interface que define as operações basicas de uma tabela hash.

public interface HashTable {   
    void insert(int key, Metrics m); // Insere uma chave na tabela hash.
    boolean contains(int key, Metrics m); // Verifica se uma chave está na tabela hash.
    int capacity(); // Retorna a capacidade da tabela hash.

    int[] bucketSizesOrNull(); // Retorna o tamanho de cada bucket da tabela hash.
    int[] gapStatsOrNull(); // Retorna o tamanho do gap entre as chaves na tabela hash.
}