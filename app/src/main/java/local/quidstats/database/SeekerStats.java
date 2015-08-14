package local.quidstats.database;

import java.util.HashSet;
import java.util.Set;

public class SeekerStats implements Comparable {

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
    public Set<String> osrUpGames;
    public Set<String> osrDownGames;

    public SeekerStats(String id) {
        seekerId = id;
        isrGames = new HashSet<>();
        osrUpGames = new HashSet<>();
        osrDownGames = new HashSet<>();
        gamesSeeked = new HashSet<>();
    }

    /*
    @Override
    public int compare(Object lhs_, Object rhs_) {
        if (lhs_ instanceof SeekerStats && rhs_ instanceof SeekerStats) {
            SeekerStats lhs = (SeekerStats) lhs_;
            SeekerStats rhs = (SeekerStats) rhs_;
            double lhsVal = (double) lhs.isrCatchesFor / (double) lhs.isrGames.size();
            double rhsVal = (double) rhs.isrCatchesFor / (double) this.isrGames.size();
            if (lhsVal < rhsVal) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }
    */

    @Override
    public int compareTo(Object another) {

        if (another instanceof SeekerStats) {
            SeekerStats rhs = (SeekerStats) another;
            double lhsVal;
            double rhsVal;
            if (this.isrGames.size() == 0 ) {
                lhsVal = 0;
            } else {
                lhsVal = (double) this.isrCatchesFor / (double) this.isrGames.size();
            }
            if (rhs.isrGames.size() == 0) {
                rhsVal = 0;
            } else {
                rhsVal = (double) rhs.isrCatchesFor / (double) rhs.isrGames.size();
            }
            if (lhsVal < rhsVal) {
                return 1;
            } else if (lhsVal == rhsVal){
                return 0;
            } else {
                return -1;

            }
        } else {
            return 0;
        }

        /*
        SeekerStats rhs = (SeekerStats) another;
        SeekerStats lhs = (SeekerStats) this;
        if (rhs.isrCatchesFor > lhs.isrCatchesFor) {
            return -1;
        } else {
            return 0;
        }
        */
    }
}
