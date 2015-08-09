package local.quidstats.database;

import java.util.HashSet;
import java.util.Set;

public class SeekerStats {

    public String seekerId;

    // Times are all in milliseconds

    public int timeSeeking;
    public Set<String> gamesSeeked;

    public int isrTime;
    public int osrUpTime;
    public int osrDownTime;

    public int isrCatchesFor;
    public int osrUpCatchesFor;
    public int osrDownCatchesFor;

    public int isrCatchesAgainst;
    public int osrUpCatchesAgainst;
    public int osrDownCatchesAgainst;

    public Set<String> isrGames;
    public Set<String> osrUpGamesTotally;
    public Set<String> osrDownGamesTotally;

    public SeekerStats(String id) {
        seekerId = id;
        isrGames = new HashSet<>();
        osrUpGamesTotally = new HashSet<>();
        osrDownGamesTotally = new HashSet<>();
        gamesSeeked = new HashSet<>();
    }
}
