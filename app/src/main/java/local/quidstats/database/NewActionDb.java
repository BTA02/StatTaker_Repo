package local.quidstats.database;

public class NewActionDb {

    private String id;
    private String gameId;
    private int youtubeTime;
    private NewAction actualAction;
    private String playerOut;
    private String playerIn;
    private int loc;

    public static enum NewAction { SHOT, GOAL, ASSIST, TURNOVER, SNITCH_CATCH, START_CLOCK, PAUSE_CLOCK,
                            GAME_START, GAME_END, GAIN_CONTROL, LOSE_CONTROL, RED_CARD, YELLOW_CARD,
                            SNITCH_ON_PITCH, SUB, AWAY_GOAL }

    public String getPlayerIn() {
        return playerIn;
    }

    public void setPlayerIn(String playerIn) {
        this.playerIn = playerIn;
    }

    public int getYoutubeTime() {
        return youtubeTime;
    }

    public void setYoutubeTime(int youtubeTime) {
        this.youtubeTime = youtubeTime;
    }

    public NewAction getActualAction() {
        return actualAction;
    }

    public void setActualAction(NewAction actualAction) {
        this.actualAction = actualAction;
    }

    public String getPlayerOut() {
        return playerOut;
    }

    public void setPlayerOut(String playerOut) {
        this.playerOut = playerOut;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    @Override
    public boolean equals(Object o) {
        return id.equals(((NewActionDb) o).getId());
    }
}
