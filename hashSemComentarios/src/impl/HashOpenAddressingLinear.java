package impl;

import core.*;

public class HashOpenAddressingLinear implements HashTable {
    private int[] table; // Array para armazenar as chaves.
    private boolean[] used; // Array para marcar posições ocupadas.
    private int M; // Capacidade da tabela hash.
    private int size; // Número de elementos na tabela.
    private static final double LOAD_FACTOR_THRESHOLD = 0.75; // Fator de carga para trigger do rehashing.

    public HashOpenAddressingLinear(int capacity) {
        this.M = capacity; // Define a capacidade da tabela.
        this.table = new int[M]; // Inicializa array de chaves.
        this.used = new boolean[M]; // Inicializa array de posições ocupadas.
        this.size = 0; // Inicializa contador de elementos.
    }

    private int h0(int x) {
        int v = x % M; // Calcula o hash usando módulo.
        return (v < 0) ? v + M : v; // Garante que o resultado seja positivo (trata números negativos).
    }

    private void rehash() {
        int[] oldTable = table; // Salva referência da tabela antiga.
        boolean[] oldUsed = used; // Salva referência do array de posições ocupadas.
        int oldM = M; // Salva capacidade antiga.

        M = M * 2; // Dobra a capacidade da tabela.
        table = new int[M]; // Cria nova tabela com capacidade dobrada.
        used = new boolean[M]; // Cria novo array de posições ocupadas.
        size = 0; // Zera contador de elementos.

        // Reinsere todos os elementos da tabela antiga na nova tabela.
        for (int i = 0; i < oldM; i++) {
            if (oldUsed[i]) { // Se a posição estava ocupada na tabela antiga.
                insertWithoutRehash(oldTable[i]); // Reinsere o elemento na nova tabela sem verificar rehashing.
            }
        }
    }

    private void insertWithoutRehash(int key) {
        int idx = h0(key); // Calcula o índice inicial usando a função hash.
        int i = 0; // i = offset linear.
        while (i < M) { // Tenta encontrar posição vaga.
            int pos = (idx + i) % M; // Calcula posição com sondagem linear.
            if (!used[pos]) { // Se a posição está vaga.
                used[pos] = true; // Marca como ocupada.
                table[pos] = key; // Insere a chave.
                size++; // Incrementa contador de elementos.
                return;
            }
            if (table[pos] == key) return; // Se a chave já existe, não insere novamente.
            i++; // Incrementa offset linear.
        }
    }

    @Override
    public void insert(int key, Metrics m) {
        // Verifica se precisa fazer rehashing antes de inserir.
        if ((double) size / M >= LOAD_FACTOR_THRESHOLD) {
            rehash(); // Executa rehashing se fator de carga exceder o limite.
        }

        int idx = h0(key); // Calcula o índice inicial usando a função hash.
        int i = 0, col = 0; // i = offset linear, col = contador de colisões.
        while (i < M) { // Tenta encontrar posição vaga.
            int pos = (idx + i) % M; // Calcula posição com sondagem linear.
            if (!used[pos]) { // Se a posição está vaga.
                used[pos] = true; // Marca como ocupada.
                table[pos] = key; // Insere a chave.
                size++; // Incrementa contador de elementos.
                m.collisionsInsert += col; // Adiciona colisões às métricas.
                return;
            }
            if (table[pos] == key) return; // Se a chave já existe, não insere novamente.
            i++; // Incrementa offset linear.
            col++; // Incrementa contador de colisões.
        }
    }

    @Override
    public boolean contains(int key, Metrics m) {
        int idx = h0(key); // Calcula o índice inicial usando a função hash.
        int i = 0, col = 0; // i = offset linear, col = contador de colisões.
        while (i < M) { // Procura a chave.
            int pos = (idx + i) % M; // Calcula posição com sondagem linear.
            if (!used[pos]) { // Se a posição está vaga, a chave não existe.
                m.collisionsSearch += col; // Adiciona colisões às métricas.
                return false; // Retorna false (chave não encontrada).
            }
            if (table[pos] == key) { // Se encontrou a chave.
                m.collisionsSearch += col; // Adiciona colisões às métricas.
                return true; // Retorna true (chave encontrada).
            }
            i++; // Incrementa offset linear.
            col++; // Incrementa contador de colisões.
        }
        m.collisionsSearch += col; // Adiciona colisões às métricas.
        return false; // Retorna false (chave não encontrada).
    }

    @Override
    public int capacity() {
        return M; // Retorna a capacidade da tabela hash.
    }

    @Override
    public int[] bucketSizesOrNull() {
        return null; // Retorna null pois bucket sizes não se aplica ao endereçamento aberto.
    }

    @Override
    public int[] gapStatsOrNull() {
        int occ = 0; // Contador de posições ocupadas.
        for (boolean b : used) if (b) occ++; // Conta quantas posições estão ocupadas.
        if (occ <= 1) return new int[]{M - occ, M - occ, (M - occ) * 1000}; // Caso especial: poucas posições ocupadas.
        
        // Se a tabela está muito cheia (mais de 90%), retorna gaps pequenos mas não zero
        if (occ >= M * 0.9) {
            int avgGap = (M - occ) * 1000 / Math.max(1, occ); // Calcula gap médio aproximado
            return new int[]{0, M - occ, avgGap}; // Retorna gaps pequenos mas não zero
        }
        
        int gapMin = Integer.MAX_VALUE, gapMax = 0; // Inicializa min e max dos gaps.
        long sum = 0; // Soma total dos gaps.
        int count = 0; // Contador de gaps.
        int start = -1; // Primeira posição ocupada.
        for (int i = 0; i < M; i++) if (used[i]) { // Encontra a primeira posição ocupada.
            start = i;
            break;
        }
        int prev = start; // Posição anterior ocupada.
        int i = (start + 1) % M; // Próxima posição a verificar.
        while (i != start) { // Percorre circularmente até voltar ao início.
            if (used[i]) { // Se a posição está ocupada.
                int gap = (i > prev) ? i - prev - 1 : (M - prev - 1) + i; // Calcula gap entre posições ocupadas.
                if (gap < gapMin) gapMin = gap; // Atualiza gap mínimo.
                if (gap > gapMax) gapMax = gap; // Atualiza gap máximo.
                sum += gap; // Adiciona gap à soma.
                count++; // Incrementa contador de gaps.
                prev = i; // Atualiza posição anterior.
            }
            i = (i + 1) % M; // Avança circularmente.
        }
        int tail = (start > prev) ? start - prev - 1 : (M - prev - 1) + start; // Calcula gap final (do último ao primeiro).
        gapMin = Math.min(gapMin, tail); // Atualiza gap mínimo com o gap final.
        gapMax = Math.max(gapMax, tail); // Atualiza gap máximo com o gap final.
        sum += tail; // Adiciona gap final à soma.
        count++; // Incrementa contador.
        
        if (count == 0) return new int[]{0, 0, 0}; // Se não há gaps, retorna zero
        
        int avgX1000 = (int)((sum * 1000L) / count); // Calcula média multiplicada por 1000 para preservar decimais.
        return new int[]{gapMin, gapMax, avgX1000}; // Retorna [gap_min, gap_max, gap_avg_x1000].
    }
}