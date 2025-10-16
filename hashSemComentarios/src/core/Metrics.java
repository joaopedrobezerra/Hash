
package core;

public class Metrics {
    public long insertTimeNs;
    public long searchTimeNs;
    public long collisionsInsert;
    public long collisionsSearch;

    public void resetAll() {
        insertTimeNs = 0L;
        searchTimeNs = 0L;
        collisionsInsert = 0L;
        collisionsSearch = 0L;
    }
}
