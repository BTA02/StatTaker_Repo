package local.quidstats.database;

public class StatDb
{
	String playerId;
	int shots;
	int goals;
	int assists;
	int steals;
	int turnovers;
	int saves;

    int takeaways;
    int yellows;
    int reds;

	int snitches;
	int plusses;
	int minuses;
	int onField;
	int time;

    public StatDb() {
    }

    public StatDb(String pId) {
        shots = 0;
        goals = 0;
        assists = 0;
        steals = 0;
        turnovers = 0;
        saves = 0;
        snitches = 0;
        plusses = 0;
        minuses = 0;
        onField = -1;
        time = 0;
        playerId = pId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getSteals() {
        return steals;
    }

    public void setSteals(int steals) {
        this.steals = steals;
    }

    public int getTurnovers() {
        return turnovers;
    }

    public void setTurnovers(int turnovers) {
        this.turnovers = turnovers;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public int getSnitches() {
        return snitches;
    }

    public void setSnitches(int snitches) {
        this.snitches = snitches;
    }

    public int getPlusses() {
        return plusses;
    }

    public void setPlusses(int plusses) {
        this.plusses = plusses;
    }

    public int getMinuses() {
        return minuses;
    }

    public void setMinuses(int minuses) {
        this.minuses = minuses;
    }

    public int getOnField() {
        return onField;
    }

    public void setOnField(int onField) {
        this.onField = onField;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTakeaways() {
        return takeaways;
    }

    public void setTakeaways(int t) {
        takeaways = t;
    }
    public int getYellows() {
        return yellows;
    }

    public void setYellows(int t) {
        yellows = t;
    }
    public int getReds() {
        return reds;
    }

    public void setReds(int t) {
        reds = t;
    }

}
