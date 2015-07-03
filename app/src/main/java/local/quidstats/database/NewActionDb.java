package local.quidstats.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public static String convertActionsToJSON(List<NewActionDb> list) {
        JSONArray arr = new JSONArray();
        for (NewActionDb action : list) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", action.getId());
                obj.put("gameId", action.getGameId());
                obj.put("youtubeTime", action.getYoutubeTime());
                obj.put("actualAction",action.getActualAction());
                obj.put("playerOut",action.getPlayerOut());
                obj.put("playerIn",action.getPlayerIn());
                obj.put("loc",action.getLoc());
                arr.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }

        }
        return arr.toString();

    }

    public static List<NewActionDb> convertJSONToActions(String str) {
        try {
            JSONArray arr = new JSONArray(str);
            List<NewActionDb> ret = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                NewActionDb action = new NewActionDb();

                JSONObject obj = arr.getJSONObject(i);
                action.setId(obj.get("id").toString());
                action.setLoc(obj.getInt("loc"));
                action.setActualAction(NewAction.valueOf(obj.getString("actualAction")));
                action.setYoutubeTime(obj.getInt("youtubeTime"));
                action.setGameId(obj.getString("gameId"));
                action.setPlayerIn(obj.getString("playerIn"));
                action.setPlayerOut(obj.getString("playerOut"));
                ret.add(action);
            }
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }
}
