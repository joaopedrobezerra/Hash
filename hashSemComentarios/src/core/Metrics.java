
package core;
// Esse codigo é uma classe que armazena as métricas de uma tabela hash.
public class Metrics {
    public long insertTimeNs; // Tempo de inserção em nanossegundos.
    public long searchTimeNs; // Tempo de busca em nanossegundos.
    public long collisionsInsert; // Número de colisões na inserção.
    public long collisionsSearch; // Número de colisões na busca.

    public void resetAll() { // Reinicia todas as métricas.
        insertTimeNs = 0L;
        searchTimeNs = 0L; // Tempo de busca em nanossegundos.
        collisionsInsert = 0L; // Número de colisões na inserção.
        collisionsSearch = 0L; // Número de colisões na busca.
    }
}