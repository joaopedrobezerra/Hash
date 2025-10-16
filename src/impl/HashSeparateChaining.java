package impl;

import core.*;

public class HashSeparateChaining implements HashTable {
    private static class Node { // Classe interna para representar um nó da lista encadeada.
        int key; // Chave armazenada no nó.
        Node next; // Referência para o próximo nó na lista.

        Node(int k) {
            key = k; // Inicializa a chave do nó.
        }
    }

    private final Node[] table; // Array de nós para implementar a tabela hash com encadeamento.
    private final int M; // Capacidade da tabela hash.

    public HashSeparateChaining(int capacity) {
        this.M = capacity; // Define a capacidade da tabela.
        this.table = new Node[M]; // Inicializa o array de nós com a capacidade especificada.
    }

    private int h(int x) {
        int v = x % M; // Calcula o hash usando módulo.
        return (v < 0) ? v + M : v; // Garante que o resultado seja positivo (trata números negativos).
    }

    @Override
    public void insert(int key, Metrics m) {
        int idx = h(key); // Calcula o índice na tabela usando a função hash.
        Node head = table[idx]; // Obtém a cabeça da lista encadeada no índice.
        
        // Verifica se a chave já existe
        Node cur = head;
        while (cur != null) { // Percorre a lista encadeada.
            if (cur.key == key) { // Se a chave já existe, não insere novamente.
                return;
            }
            cur = cur.next; // Avança para o próximo nó.
        }
        
        // Se chegou aqui, a chave não existe
        // Se já há elementos no bucket, isso é uma colisão
        if (head != null) {
            m.collisionsInsert++; // Conta uma colisão
        }
        
        Node n = new Node(key); // Cria novo nó com a chave.
        n.next = head; // Insere no início da lista (mais eficiente).
        table[idx] = n; // Atualiza a cabeça da lista.
    }

    @Override
    public boolean contains(int key, Metrics m) {
        int idx = h(key); // Calcula o índice na tabela usando a função hash.
        Node cur = table[idx]; // Obtém a cabeça da lista encadeada no índice.
        
        // Conta quantos elementos precisam ser verificados antes de encontrar (ou não) a chave
        int comparisons = 0;
        while (cur != null) { // Percorre a lista encadeada.
            comparisons++; // Incrementa contador de comparações.
            if (cur.key == key) { // Se encontrou a chave.
                m.collisionsSearch += comparisons; // Adiciona comparações às métricas.
                return true; // Retorna true (chave encontrada).
            }
            cur = cur.next; // Avança para o próximo nó.
        }
        m.collisionsSearch += comparisons; // Adiciona comparações às métricas.
        return false; // Retorna false (chave não encontrada).
    }

    @Override
    public int capacity() {
        return M; // Retorna a capacidade da tabela hash.
    }

    @Override
    public int[] bucketSizesOrNull() {
        int[] sizes = new int[M]; // Array para armazenar os tamanhos de cada bucket.
        for (int i = 0; i < M; i++) { // Percorre todos os buckets.
            Node cur = table[i]; // Obtém a cabeça da lista no bucket i.
            int count = 0; // Contador de elementos no bucket.
            while (cur != null) { // Percorre a lista encadeada.
                count++; // Incrementa contador.
                cur = cur.next; // Avança para o próximo nó.
            }
            sizes[i] = count; // Armazena o tamanho do bucket.
        }
        return sizes; // Retorna array com tamanhos dos buckets.
    }

    @Override
    public int[] gapStatsOrNull() {
        return null; // Retorna null pois gap stats não se aplica ao encadeamento.
    }
}