package local.quidstats.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper
{
	// Logcat tag
	private static final String TAG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 72;

	// Database Name
	private static final String DATABASE_NAME = "quidditchGames";

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DatabaseHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	

	private SQLiteDatabase myDabatabse;

	//Shared strings
	public static final String COL_ID = "_id";

	// Table Names
	public static final String TABLE_PLAYER = "playerTable";
	public static final String TABLE_GAME = "gameTable";
	public static final String TABLE_TEAM = "teamTable";
	public static final String TABLE_META_STAT = "metaStatsTable";
	public static final String TABLE_STATS = "statsTable";
	public static final String TABLE_TIME = "timeTable";
    public static final String TABLE_VIDEO = "videoTable";

	// GAME Table - column names
	public static final String COL_GAMEID = "g_id";
	public static final String COL_HOME_TEAM = "homeTeamId";
	public static final String COL_AWAY_TEAM = "awayTeamId";
	public static final String COL_GAME_TIME = "gameTime";
	public static final String COL_HOME_SCORE = "homeScore";
	public static final String COL_AWAY_SCORE = "awayScore";
	public static final String COL_TIME_MAP = "timeMap";

	// TEAM Table = column names
	public static final String COL_TEAMID = "t_id";
	public static final String COL_TEAM_NAME = "teamName";
	public static final String COL_ACTIVE = "active";
	
	// META STAT Tables - column names
	public static final String COL_META_STAT_ID = "m_id";
	public static final String COL_META_STAT_TYPE = "type";
	public static final String COL_META_STAT_WHEN = "timeOf";
	public static final String COL_META_STAT_SUBBED = "subbed";

	// STAT Table = column names
	public static final String COL_STATID = "s_id";
	public static final String COL_SHOTS = "shots";
	public static final String COL_GOALS = "goals";
	public static final String COL_ASSISTS = "assists";
	public static final String COL_STEALS = "steals";
	public static final String COL_TURNOVERS = "turnovers";
	public static final String COL_SAVES = "saves";
	public static final String COL_SNITCHES = "snitches";
	public static final String COL_PLUSSES = "plusses";
	public static final String COL_MINUSES = "minuses";
	public static final String COL_TIMEID = "time";
	public static final String COL_TOTAL_TIME = "totalTime";
	public static final String COL_ONFIELD = "onField";

	//TIME Tables = column names
	//Has id, timein, timeout, get the rest from the stats table
	public static final String COL_TIME_IN = "time_in";
	public static final String COL_TIME_OUT = "time_out";

	// PLAYER Table - column names
	public static final String COL_PLAYERID = "p_id";
	public static final String COL_NUMBER = "number";
	public static final String COL_FNAME = "fname";
	public static final String COL_LNAME = "lname";

    // VIDEO Table - column names
    public static final String COL_VID_TIME = "video_time";
    public static final String COL_ACTION = "action";
    public static final String COL_PLAYER_IN = "player_in";
    public static final String COL_PLAYER_OUT = "playout_out";
    public static final String COL_LOCATION = "location";


	// Table Create Statements
	
	// GAME table create statement
	private static final String CREATE_TABLE_GAME = "CREATE TABLE "
			+ TABLE_GAME + "(" + COL_ID + " TEXT, " 
			+ COL_HOME_TEAM + " TEXT, " 
			+ COL_AWAY_TEAM + " TEXT, " 
			+ COL_GAME_TIME + " INT, "
			+ COL_HOME_SCORE + " INT, "
			+ COL_AWAY_SCORE + " INT, "
			+ COL_TIME_MAP + " BLOB)";

	private static final String CREATE_TABLE_PLAYER = "CREATE TABLE " + TABLE_PLAYER
			+ "(" + COL_ID + " TEXT, " 
			+ COL_NUMBER + " TEXT, " 
			+ COL_FNAME + " TEXT, " 
			+ COL_LNAME + " TEXT)";
	
	private static final String CREATE_TABLE_META_STAT = "CREATE TABLE " + TABLE_META_STAT
			+ "(" + COL_ID + " TEXT, "
			+ COL_META_STAT_TYPE + " TEXT, "
			+ COL_META_STAT_WHEN + " INTEGER, " //in seconds
			+ COL_GAMEID + " TEXT, "
			+ COL_META_STAT_SUBBED + " TEXT, "
			+ COL_PLAYERID + " TEXT)";

	private static final String CREATE_TABLE_STAT = "CREATE TABLE " + TABLE_STATS
			+ "(" + COL_ID + " TEXT, "
			+ COL_GAMEID + " TEXT, " //against who
			+ COL_PLAYERID + " TEXT, " //who this table is about
			+ COL_SHOTS + " INTEGER," 
			+ COL_GOALS + " INTEGER," 
			+ COL_ASSISTS + " INTEGER," 
			+ COL_STEALS + " INTEGER," 
			+ COL_TURNOVERS +  " INTEGER," 
			+ COL_SAVES + " INTEGER," 
			+ COL_SNITCHES + " INTEGER," 
			+ COL_PLUSSES + " INTEGER," 
			+ COL_MINUSES + " INTEGER, " 
			+ COL_TIMEID + " INTEGER, "
			+ COL_TOTAL_TIME + " INTEGER, "
			+ COL_ONFIELD + " INTEGER)";

	private static final String CREATE_TABLE_TIME = "CREATE TABLE " + TABLE_TIME
			+ "(" + COL_ID + " TEXT, "
			+ COL_TIME_IN + " INTEGER, "
			+ COL_TIME_OUT + " INTEGER)";

	private static final String CREATE_TABLE_TEAM = "CREATE TABLE " + TABLE_TEAM
			+ "(" + COL_ID + " TEXT, " //id of the team
			+ COL_TEAM_NAME + " TEXT, " // name of the team
			+ COL_PLAYERID + " TEXT, " // id of a player on the team
			+ COL_ACTIVE + ")";//each row is just an entry in a roster
	//row looks like:
	//"892-3011-df9a902", "University of Michigan, "582-2811-pa8f123", 1 (active)
	//"892-3011-df9a902", "University of Michigan, "113-9301-jk8b553", 0 (not active)
	//...

    private static final String CREATE_TABLE_VIDEO = "CREATE TABLE " + TABLE_VIDEO
            + "(" + COL_ID + " TEXT, "
            + COL_GAMEID + " TEXT, "
            + COL_VID_TIME + " INTEGER, "
            + COL_ACTION + " INTEGER, "
            + COL_PLAYER_IN + " TEXT, "
            + COL_PLAYER_OUT + " TEXT, "
            + COL_LOCATION + " INT" + ")";

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(CREATE_TABLE_GAME);
		db.execSQL(CREATE_TABLE_PLAYER);
		db.execSQL(CREATE_TABLE_META_STAT);
		db.execSQL(CREATE_TABLE_STAT);
		db.execSQL(CREATE_TABLE_TIME);
		db.execSQL(CREATE_TABLE_TEAM);
        db.execSQL(CREATE_TABLE_VIDEO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// Not quite. Good enough for testing, should be better for real
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_META_STAT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO);

		// create new tables
		onCreate(db);

	}

	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//----------------TEAM---------------------------------------
	//-----------------------------------------------------------
	public Cursor getAllTeamsCursor()
	{
		//this is a problem because I need to select DISTINCT

		String query = "SELECT * FROM " + TABLE_TEAM;
		Cursor c = getDB().rawQuery(query, null);
		if (c.moveToFirst())
		{
			
			return c;
		}
		else
		{
            c.close();
			return null;
		}
	}

	public List<TeamDb> getAllTeamsList()
	{
		String query = "SELECT * FROM " + TABLE_TEAM;
		Cursor c = getDB().rawQuery(query, null);

		List<TeamDb> teamList = new ArrayList<TeamDb>();
		Map<String, Integer> ids = new HashMap<String, Integer>();

		if (c.moveToFirst())
		{
			do
			{
				TeamDb t = new TeamDb(); //just a db row
				t.setId(c.getString((c.getColumnIndex(COL_ID))));
				t.setName(c.getString(c.getColumnIndex(COL_TEAM_NAME)));

				if (ids.size() != 0 && ids.get(t.getId()) != null && ids.get(t.getId()) == 1) //already been found
				{
					//don't do stuff
				}
				else
				{
					ids.put(t.getId(), 1);
					teamList.add(t);
				}
			}
			while (c.moveToNext());
		}

        c.close();
		return teamList;
	}

	public TeamDb getTeamFromId(String id)
	{
		String query = "SELECT " + COL_TEAM_NAME + " FROM " + TABLE_TEAM
				+ " WHERE " + COL_ID + " = ?";
		Cursor c = getDB().rawQuery(query, new String[]{ id });
		TeamDb ret = new TeamDb();
		if (c.moveToFirst())
		{
			ret.setName( c.getString(c.getColumnIndex(COL_TEAM_NAME)) );
			ret.setId(id);
		}
		else
		{
			Log.e(TAG, "team not found in getTeamNameFromId");
            c.close();
			return null;
		}
        c.close();
		return ret;
	}

	public void addTeam(String teamId, String teamName)
	{
		ContentValues values = new ContentValues();
		values.put(COL_ID, teamId);
		values.put(COL_TEAM_NAME, teamName);
		values.put(COL_PLAYERID, ""); //null player, so I can access the first row whenever

		getDB().insert(TABLE_TEAM, null, values);
	}// addTeam

	//I can update all references to this team to just have the name?
	//I can also just store team name ONLY when doing stuff
	//I'll have to think about this
	//I also need to delete stats...
	public void deleteTeam(String teamId)
	{
		String[] whereArgs = new String[] { teamId };
		getDB().delete(TABLE_TEAM, COL_ID + " = ?", whereArgs);
		getDB().delete(TABLE_GAME, COL_HOME_TEAM + " = ?", whereArgs);
		getDB().delete(TABLE_STATS, COL_GAMEID + " = ?", whereArgs);
	}

	public List<PlayerDb> getActivePlayers(String teamId)
	{
		List<PlayerDb> ret = new ArrayList<PlayerDb>();

		String query = "SELECT "
				+ TABLE_PLAYER + "." + COL_ID + ", "
				+ TABLE_PLAYER + "." + COL_NUMBER + ", "
				+ TABLE_PLAYER + "." + COL_FNAME + ", "
				+ TABLE_PLAYER + "." + COL_LNAME + ", "
				+ TABLE_TEAM + "." + COL_ACTIVE + ", "
				+ TABLE_TEAM + "." + COL_PLAYERID
				+ " FROM " + TABLE_PLAYER + ", " + TABLE_TEAM
				+ " WHERE " 
				+ TABLE_TEAM + "." + COL_ID + " = '" + teamId + "'"
				+ " AND "
				+ TABLE_TEAM + "." + COL_PLAYERID + " = " + TABLE_PLAYER + "." + COL_ID;

		Cursor c = getDB().rawQuery(query, null);
		//Cursor c = getDB().rawQuery(query, new String[] {teamId});
		if (c.moveToFirst())
		{
			do
			{
				PlayerDb p = new PlayerDb();
				p.setFname(c.getString(c.getColumnIndex(COL_FNAME)));
				p.setLname(c.getString(c.getColumnIndex(COL_LNAME)));
				p.setNumber(c.getString(c.getColumnIndex(COL_NUMBER)));
				p.setPlayerId(c.getString(c.getColumnIndex(COL_ID)));
				if (c.getInt(c.getColumnIndex(COL_ACTIVE)) == 1)
				{
					ret.add(p);
				}
			}
			while (c.moveToNext());
		}
        c.close();
		return ret;
	}

	public boolean isPlayerActiveOnTeam(String teamId, String playerId)
	{
		String query = "SELECT * FROM " + TABLE_TEAM
				+ " WHERE "
				+ COL_ID + " = '" + teamId + "'"
				+ " AND "
				+ COL_PLAYERID + " = '" + playerId + "'";
		Cursor c = getDB().rawQuery(query, null);
		if (c.moveToFirst())
		{
			if (c.getInt(c.getColumnIndex(COL_ACTIVE)) == 1)
			{
                c.close();
				return true;
			}
		}
        c.close();
		return false;
	}

	public void updateActiveInfo(String teamId, String playerId, int newVal)
	{
		ContentValues values = new ContentValues();
		values.put(COL_ACTIVE, newVal);

		@SuppressWarnings("unused")
		int rowsAffected = getDB().update(TABLE_TEAM, values, 
				COL_ID + " = ? AND " + COL_PLAYERID + " = ?", new String[] {teamId, playerId} );
		
		
		return;
	}
	
	public boolean teamExists(String teamId)
	{
		String query = "SELECT * FROM " + TABLE_TEAM + " WHERE "
				+ COL_ID + " = '" + teamId + "'";
		
		Cursor c = getDB().rawQuery(query, null);
		
		if (c.moveToFirst())
		{
            c.close();
			return true;
		}
		else
		{
            c.close();
			return false;
		}
	}
	
	// -------------------------------------------------------------------------
	
	public void removePlayerFromTeam(String playerId, String teamId)
	{
		// Should just remove a team from the roster
		String[] whereArgs = new String[] {teamId, playerId};
		getDB().delete(TABLE_TEAM, COL_ID + " =? AND " + COL_PLAYERID + " = ?", 
				whereArgs);
	}
	
	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//----------------Players------------------------------------
	//-----------------------------------------------------------

	//Works
	public void addPlayerToTeam(String playerId, String teamId)
	{
		TeamDb team = getTeamFromId(teamId);

		ContentValues values = new ContentValues();
		values.put(COL_ID, teamId);
		values.put(COL_TEAM_NAME, team.getName());
		values.put(COL_PLAYERID, playerId);
		values.put(COL_ACTIVE, 0);

		getDB().insert(TABLE_TEAM, null, values);

		

	}// addPlayerToTeam


	// Closes cursor
	public List<PlayerDb> getAllPlayersFromTeam(String teamId, int activeFlag)
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		String query;
		if (activeFlag == 0)
		{
			//this might actually be working
			query = "SELECT "
					+ TABLE_PLAYER + "." + COL_ID + ", "
					+ TABLE_PLAYER + "." + COL_NUMBER + ", "
					+ TABLE_PLAYER + "." + COL_FNAME + ", "
					+ TABLE_PLAYER + "." + COL_LNAME
					+ " FROM " + TABLE_TEAM + ", " + TABLE_PLAYER
					+ " WHERE "
					+ TABLE_TEAM + "." + COL_PLAYERID + " = "
					+ TABLE_PLAYER + "." + COL_ID
					+ " AND " + TABLE_TEAM + "." + COL_ID + " = '"
					+ teamId + "' ORDER BY " + COL_FNAME + " ASC";


		}
		else //active players only, not done
		{
			query = "SELECT "
					+ TABLE_PLAYER + "." + COL_ID + ", "
					+ TABLE_PLAYER + "." + COL_NUMBER + ", "
					+ TABLE_PLAYER + "." + COL_FNAME + ", "
					+ TABLE_PLAYER + "." + COL_LNAME + ", "
					+ TABLE_PLAYER + "." + COL_ACTIVE
					+ " FROM " + TABLE_TEAM + ", " + TABLE_PLAYER
					+ " WHERE "
					+ TABLE_TEAM + "." + COL_PLAYERID + " = "
					+ TABLE_PLAYER + "." + COL_ID
					+ " AND " + TABLE_PLAYER + "." + COL_ACTIVE + " = 1"
					+ " AND " + TABLE_TEAM + "." + COL_ID + " = '"
					+ teamId + "' ORDER BY " + COL_FNAME + " ASC";
		}
		Cursor c = getDB().rawQuery(query, null);

		if (c.moveToFirst())
		{
			do
			{
				PlayerDb p = new PlayerDb(); //just a db row
				p.setPlayerId(c.getString((c.getColumnIndex(COL_ID))));
				p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
				p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
				p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
				playerList.add(p);
			}
			while (c.moveToNext());
		}
        c.close();
		return playerList;

	}// getAllPlayersFromTeam


    // Need to handle closing  later
	public Cursor getAllPlayersFromTeamCursor(String teamId, int activeFlag)
	{
		String query;
		if (activeFlag == 0)
		{
			//this might actually be working
			query = "SELECT "
					+ TABLE_PLAYER + "." + COL_ID + ", "
					+ TABLE_PLAYER + "." + COL_NUMBER + ", "
					+ TABLE_PLAYER + "." + COL_FNAME + ", "
					+ TABLE_PLAYER + "." + COL_LNAME
					+ " FROM " + TABLE_TEAM + ", " + TABLE_PLAYER
					+ " WHERE "
					+ TABLE_TEAM + "." + COL_PLAYERID + " = "
					+ TABLE_PLAYER + "." + COL_ID
					+ " AND " + TABLE_TEAM + "." + COL_ID + " = '"
					+ teamId + "'"
					+ " ORDER BY "
					+ COL_FNAME + " ASC";

		}
		else //active players only, not done
		{
			query = "SELECT "
					+ TABLE_PLAYER + "." + COL_ID + ", "
					+ TABLE_PLAYER + "." + COL_NUMBER + ", "
					+ TABLE_PLAYER + "." + COL_FNAME + ", "
					+ TABLE_PLAYER + "." + COL_LNAME + ", "
					+ TABLE_PLAYER + "." + COL_ACTIVE
					+ " FROM " + TABLE_TEAM + ", " + TABLE_PLAYER
					+ " WHERE "
					+ TABLE_TEAM + "." + COL_PLAYERID + " = "
					+ TABLE_PLAYER + "." + COL_ID
					+ " AND " + TABLE_PLAYER + "." + COL_ACTIVE + " = 1"
					+ " AND " + TABLE_TEAM + "." + COL_ID + " = '"
					+ teamId + "'"
					+ " ORDER BY "
					+ COL_FNAME + " ASC";
		}
		Cursor c = getDB().rawQuery(query, null);
		if(c.moveToFirst())
		{
			return c;
		}
        c.close();
		return null;
	}

	public void updatePlayerInfo(PlayerDb updatedPlayer)
	{
		String playerId = updatedPlayer.getPlayerId();

		ContentValues values = new ContentValues();

		values.put(COL_FNAME, updatedPlayer.getFname());
		values.put(COL_LNAME, updatedPlayer.getLname());
		values.put(COL_NUMBER, updatedPlayer.getNumber());

		getDB().update(TABLE_PLAYER, values, COL_ID + " = ?", new String[] {playerId} );

		
	}

	public void addPlayer(String id, String number, String fname, String lname)
	{
		ContentValues values = new ContentValues();
		values.put(COL_ID, id);
		values.put(COL_NUMBER, number);
		values.put(COL_FNAME, fname);
		values.put(COL_LNAME, lname);

		getDB().insert(TABLE_PLAYER, null, values);
	}

    // Closes cursor
	public List<PlayerDb> getAllPlayersList()
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		String query;

		//this might actually be working
		query = "SELECT * FROM " + TABLE_PLAYER;


		Cursor c = getDB().rawQuery(query, null);

		if (c.moveToFirst())
		{
			do
			{
				PlayerDb p = new PlayerDb(); //just a db row
				p.setPlayerId(c.getString((c.getColumnIndex(COL_ID))));
				p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
				p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
				p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
				playerList.add(p);
			}
			while (c.moveToNext());
		}
        c.close();
		return playerList;
	}

	public Cursor getAllPlayersCursor()
	{
		String query = "SELECT * FROM " + TABLE_PLAYER + " ORDER BY " + COL_LNAME + " ASC";
		//String query = "SELECT * FROM " + TABLE_PLAYER;
		Cursor c = getDB().rawQuery(query, null);
		if (c.moveToFirst())
		{
			
			return c;
		}
		else
		{
            c.close();
			return null;
		}
	}
	
	public boolean playerExists(String playerId)
	{
		String query = "SELECT * FROM " + TABLE_PLAYER + " WHERE "
				+ COL_ID + " = '" + playerId + "'";
		Cursor c = getDB().rawQuery(query, null);
		if (c.moveToFirst())
		{
            c.close();
			return true;
		}
        c.close();
		return false;
	}

	public boolean playerExistsOnTeam(String playerId, String teamId)
	{
		String query = "SELECT 1 FROM " + TABLE_TEAM + ", " + TABLE_PLAYER
				+ " WHERE "
				+ TABLE_TEAM + "." + COL_PLAYERID + " = "
				+ TABLE_PLAYER + "." + COL_ID
				+ " AND " + TABLE_TEAM + "." + COL_ID + " = '"
				+ teamId + "' AND " + TABLE_PLAYER + "." + COL_ID
				+ " = '" + playerId + "'";
		Cursor c = getDB().rawQuery(query, null);

		if (c.moveToFirst())
		{
            c.close();
			return true;
		}
        c.close();
		return false;
	}
	
	public PlayerDb getPlayerById(String id)
	{
		String query = "SELECT * FROM " + TABLE_PLAYER + " WHERE "
				+ COL_ID + " = ?";
		Cursor c = getDB().rawQuery(query, new String[] {id});
		if (c.moveToFirst())
		{
			PlayerDb ret = new PlayerDb();
			ret.setFname(c.getString(c.getColumnIndex(COL_FNAME)));
			ret.setLname(c.getString(c.getColumnIndex(COL_LNAME)));
			ret.setNumber(c.getString(c.getColumnIndex(COL_NUMBER)));
			ret.setPlayerId(c.getString(c.getColumnIndex(COL_ID)));
            c.close();
			return ret;
		}
        c.close();
		return null;
		
	}


    public void checkIfGoal(String gameId, List<String> timeArray, MetaStatDb action) {
        List<String> players = new ArrayList<>();
        for (int i = 0; i < timeArray.size(); i++) {
            players.add(getPlayerById(timeArray.get(i)).toString());
        }
        if (action.getTimeOfAction() == 693) {
            SparseArray<List<String>> oldArr = getGameInfo(gameId).getTimeArray();
            List<String> update = oldArr.get(693);
            update.set(0, "e1ed43d5-8f79-4ae9-9603-32aad0db3425"); // Wilber
            update.set(1, "R4QcQcmIeU"); //Schepers
            oldArr.setValueAt(693, update);
            List<String> test = oldArr.get(693);
            ContentValues values = new ContentValues();
            values.put(COL_TIME_MAP, GameDb.timeArrayToBytes(oldArr));
            int i = getDB().update(TABLE_GAME, values, COL_ID + "= ?", new String[] {gameId});
            String breakfds = "break";
        }
        String str = "fds";
    }

	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//----------------Games--------------------------------------
	//-----------------------------------------------------------

	//Home teams are recorded with an id, because they are in the database
	//Away teams are recorded with an actual name, 
	//because they are NOT in the database

	public String createNewGame(String teamId, String awayTeamName)
	{
		ContentValues values = new ContentValues();
		String gId = UUID.randomUUID().toString();
		values.put(COL_ID, gId);
		values.put(COL_HOME_TEAM, teamId);
		values.put(COL_AWAY_TEAM, awayTeamName);
		values.put(COL_HOME_SCORE, 0);
		values.put(COL_AWAY_SCORE, 0);
		values.put(COL_GAME_TIME, 0);

		getDB().insert(TABLE_GAME, null, values);
		

		//now create the necessary stats tables for each "active" player


		List<PlayerDb> activePlayers = getActivePlayers(teamId);

		SQLiteDatabase db1 = this.getWritableDatabase();
		ContentValues values1 = new ContentValues();

		for (int i = 0; i < activePlayers.size(); i++)
		{
			values1.put(COL_ID, UUID.randomUUID().toString());
			values1.put(COL_GAMEID, gId);
			values1.put(COL_PLAYERID, activePlayers.get(i).getPlayerId());
			values1.put(COL_SHOTS, 0);
			values1.put(COL_GOALS, 0);
			values1.put(COL_ASSISTS, 0);
			values1.put(COL_STEALS, 0);
			values1.put(COL_TURNOVERS, 0);
			values1.put(COL_SAVES, 0);
			values1.put(COL_SNITCHES, 0);
			values1.put(COL_PLUSSES, 0);
			values1.put(COL_MINUSES, 0);
			values1.put(COL_TIMEID, UUID.randomUUID().toString());
			values1.put(COL_TOTAL_TIME, 0);
			if (i < 6)
			{
				values1.put(COL_ONFIELD, (i + 1));
			}
			else
			{
				values1.put(COL_ONFIELD, 0);
			}
			db1.insert(TABLE_STATS, null, values1);
		}
		return gId;
	}

	public List<GameDb> getAllGamesForTeam(String teamId)
	{
		List<GameDb> gamesList = new ArrayList<GameDb>();
		String query = "SELECT * FROM " + TABLE_GAME + " WHERE "
				+ COL_HOME_TEAM + " = ?";
		Cursor c = getDB().rawQuery(query, new String[]{ teamId });


		if (c.moveToFirst())
		{
			do
			{
				GameDb g = new GameDb();
				g.setId((c.getString(c.getColumnIndex(COL_ID))));
				g.setHomeTeam(c.getString(c.getColumnIndex(COL_HOME_TEAM)));
				g.setAwayTeam(c.getString(c.getColumnIndex(COL_AWAY_TEAM)));
				g.setGameTimeSeconds(c.getInt(c.getColumnIndex(COL_GAME_TIME)));
				g.setHomeScore(c.getInt(c.getColumnIndex(COL_HOME_SCORE)));
				g.setAwayScore(c.getInt(c.getColumnIndex(COL_AWAY_SCORE)));
				g.setTimeArray(c.getBlob(c.getColumnIndex(COL_TIME_MAP)));

				gamesList.add(g);
			}
			while (c.moveToNext());
		}
        c.close();
		return gamesList;
	}

	public GameDb getGameInfo(String gId)
	{
		GameDb ret = new GameDb();

		String query = "SELECT * FROM " + TABLE_GAME + " WHERE " 
				+ COL_ID + " = ?";
		Cursor c = getDB().rawQuery(query, new String[] {gId} );


		if (c.moveToFirst())
		{
			ret.setId(c.getString(c.getColumnIndex(COL_ID)));
			ret.setAwayScore(c.getInt(c.getColumnIndex(COL_AWAY_SCORE)));
			ret.setAwayTeam(c.getString(c.getColumnIndex(COL_AWAY_TEAM)));
			ret.setGameTimeSeconds(c.getInt(c.getColumnIndex(COL_GAME_TIME)));
			ret.setHomeScore(c.getInt(c.getColumnIndex(COL_HOME_SCORE)));
			ret.setHomeTeam(c.getString(c.getColumnIndex(COL_HOME_TEAM)));
			ret.setTimeArray(c.getBlob(c.getColumnIndex(COL_TIME_MAP)));
		}
        c.close();
		return ret;
	}

	public Cursor getOnFieldPlayersFromGameCursor(String gameId)
	{		
		String query = "SELECT "
				+ TABLE_PLAYER + "." + COL_ID + ", "
				+ TABLE_PLAYER + "." + COL_NUMBER + ", "
				+ TABLE_PLAYER + "." + COL_FNAME + ", "
				+ TABLE_PLAYER + "." + COL_LNAME + ", "
				+ TABLE_STATS + "." + COL_ONFIELD
				+ " FROM " + TABLE_PLAYER + ", " + TABLE_STATS
				+ " WHERE " 
				+ TABLE_STATS + "." + COL_GAMEID + " = '" + gameId + "'" 
				+ " AND "
				+ TABLE_PLAYER + "." + COL_ID + " = " + TABLE_STATS + "." + COL_PLAYERID
				+ " AND "
				+ COL_ONFIELD + " != 0"
				+ " ORDER BY " + COL_ONFIELD + " ASC";

		Cursor c = getDB().rawQuery(query, null);
		if (c.moveToFirst())
		{
			return c;
		}
        c.close();
		return null;
	}
	
	public List<PlayerDb> getOnFieldPlayersFromGame(String gameId)
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		
		String query = "SELECT "
				+ TABLE_PLAYER + "." + COL_ID + ", "
				+ TABLE_PLAYER + "." + COL_NUMBER + ", "
				+ TABLE_PLAYER + "." + COL_FNAME + ", "
				+ TABLE_PLAYER + "." + COL_LNAME + ", "
				+ TABLE_STATS + "." + COL_ONFIELD
				+ " FROM " + TABLE_PLAYER + ", " + TABLE_STATS
				+ " WHERE " 
				+ TABLE_STATS + "." + COL_GAMEID + " = '" + gameId + "'" 
				+ " AND "
				+ TABLE_PLAYER + "." + COL_ID + " = " + TABLE_STATS + "." + COL_PLAYERID
				+ " ORDER BY " + COL_ONFIELD + " ASC";

		Cursor c = getDB().rawQuery(query, null);
		if (c.moveToFirst())
		{
			do
			{
				PlayerDb p = new PlayerDb(); //just a db row
				p.setPlayerId(c.getString((c.getColumnIndex(COL_ID))));
				p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
				p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
				p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
				if (c.getInt(c.getColumnIndex(COL_ONFIELD)) != 0)
				{
					playerList.add(p);
				}
			}
			while (c.moveToNext());
		}
        c.close();
		return playerList;
	}

	public List<PlayerDb> getOffFieldPlayersFromGame(String gameId)
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		
		//a player cna't have a stat table... right?
		//what makes sense then?
		//stats have players, players dont have stats
		//that is weird, but it MUST be this way
		//stat row would be

		String query = "SELECT "
				+ TABLE_PLAYER + "." + COL_ID + ", "
				+ TABLE_PLAYER + "." + COL_NUMBER + ", "
				+ TABLE_PLAYER + "." + COL_FNAME + ", "
				+ TABLE_PLAYER + "." + COL_LNAME + ", "
				+ TABLE_STATS + "." + COL_ONFIELD
				+ " FROM " + TABLE_PLAYER + ", " + TABLE_STATS
				+ " WHERE " 
				+ TABLE_STATS + "." + COL_GAMEID + " = ?"
				+ " AND "
				+ TABLE_PLAYER + "." + COL_ID + " = " + TABLE_STATS + "." + COL_PLAYERID;



		Cursor c = getDB().rawQuery(query, new String[] {gameId} );

		if (c.moveToFirst())
		{
			do
			{

				PlayerDb p = new PlayerDb(); //just a db row
				p.setPlayerId(c.getString((c.getColumnIndex(COL_ID))));
				p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
				p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
				p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
				if (c.getInt(c.getColumnIndex(COL_ONFIELD)) == 0)
				{
					playerList.add(p);
				}
			}
			while (c.moveToNext());
		}
        c.close();
		return playerList;
	}

	public int getGameTime(String gameId)
	{
		String query = "SELECT " + COL_GAME_TIME + " FROM "
				+ TABLE_GAME + " WHERE " + COL_ID + " = ?";
		Cursor c = getDB().rawQuery(query, new String[] {gameId} );

		if (c.moveToFirst())
		{
            int ret = c.getInt(c.getColumnIndex(COL_GAME_TIME));
            c.close();
			return ret;
		}
		c.close();
		return 0;
	}

	public int getListLocation(String gId, String pId)
	{
		String query = "SELECT " + COL_ONFIELD
				+ " FROM " + TABLE_STATS
				+ " WHERE "
				+ COL_GAMEID + " = ?" + " AND "
				+ COL_PLAYERID + " = ?";

		Cursor c = getDB().rawQuery(query, new String[] {gId, pId} );



		if (c.moveToFirst())
		{
            int ret = c.getInt(c.getColumnIndex(COL_ONFIELD));
            c.close();
            return ret;
		}
        c.close();
		return 0;
	}

	public void updateTime(String gId, int valToAdd)
	{
		int curTime = getGameTime(gId);

		ContentValues values = new ContentValues();
		values.put(COL_GAME_TIME, (curTime + valToAdd) );

		getDB().update(TABLE_GAME, values, COL_ID + " = ?", new String[] {gId} );
		
	}
	
	public void updateScore(String gameId, String who, int newVal)
	{	
		
		ContentValues values = new ContentValues();
		if (who.equals("opponent"))
		{
			values.put(COL_AWAY_SCORE, newVal );
		}
		else
		{
			values.put(COL_HOME_SCORE, newVal );
		}
		
		getDB().update(TABLE_GAME, values, COL_ID + " = '" + gameId + "'" , null);
		
		
	}
	
	public void updateTimeMap(String gameId, SparseArray<List<String> > timeMap_)
	{
		ContentValues values = new ContentValues();
		byte[] timeMapBytes = GameDb.timeArrayToBytes(timeMap_);
		values.put(COL_TIME_MAP, timeMapBytes);
		int i = getDB().update(TABLE_GAME, values, COL_ID + " = ?", new String[] {gameId} );
		System.out.println(i);
	}
	
	public void deleteGame(String gameId)
	{
		getDB().delete(TABLE_GAME, COL_ID + " = ?", new String[] {gameId});
		getDB().delete(TABLE_STATS, COL_GAMEID + " = ?", new String[] {gameId});
	}
	
	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//----------------Meta Stats---------------------------------
	//-----------------------------------------------------------
	
	public void addEventToMetaStats(String gameId, String playerId, String type, int time, String playerIdSub)
	{
		ContentValues values = new ContentValues();
		values.put(COL_PLAYERID, playerId);
		values.put(COL_GAMEID, gameId);
		values.put(COL_META_STAT_TYPE, type);
		values.put(COL_META_STAT_WHEN, time);
		values.put(COL_META_STAT_SUBBED, playerIdSub);
		
		getDB().insert(TABLE_META_STAT, null, values);
	}
	
	public void undoEventInMetaStatsTable(String gameId, String playerId, String type, int time, String playerIdSub)
	{
		// Remove whatever line this data points to
		String str = COL_GAMEID + " = ? AND " + COL_PLAYERID + " = ? AND " + COL_META_STAT_TYPE + " = ? AND "
				+ COL_META_STAT_WHEN + " = ? AND " + COL_META_STAT_SUBBED + " = ?";
		String[] whereArgs = new String[]{gameId, playerId, String.valueOf(type), String.valueOf(time), playerIdSub};
		getDB().delete(TABLE_META_STAT, str, whereArgs);
		
	}
	
	public List<MetaStatDb> getAllMetaStats(String gameId)
	{
		String query = "SELECT * FROM " + TABLE_META_STAT + " WHERE "
				+ COL_GAMEID + " = ? ORDER BY " + COL_META_STAT_WHEN;
		
		Cursor c = getDB().rawQuery(query, new String[] {gameId});
		List<MetaStatDb> ret = new ArrayList<MetaStatDb>();
		if (c.moveToFirst())
		{
			do
			{
				MetaStatDb object = new MetaStatDb();
				object.setPlayerId(c.getString(c.getColumnIndex(COL_PLAYERID)));
				object.setPlayerSubbedIn(c.getString(c.getColumnIndex(COL_META_STAT_SUBBED)));
				object.setStatType(c.getString(c.getColumnIndex(COL_META_STAT_TYPE)));
				object.setTimeOfAction(c.getInt(c.getColumnIndex(COL_META_STAT_WHEN)));
				ret.add(object);
			}
			while (c.moveToNext());
		}
		c.close();
		return ret;
	}
	
	

	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//----------------Stats--------------------------------------
	//-----------------------------------------------------------

	public int getStatValue(String gameId, String playerId, String column)
	{
		String query = "SELECT * FROM " + TABLE_STATS + " WHERE "
				+ COL_GAMEID + " = ? AND " + COL_PLAYERID + " = ?";
		Cursor c = getDB().rawQuery(query, new String[] {gameId, playerId});

		if (c.moveToFirst())
		{
			int ret = c.getInt(c.getColumnIndex(column));
			c.close();
            return ret;
		}
		c.close();
		return 0;

	}

	public synchronized void updateStat(String gameId, String playerId, int valToAdd, String column)
	{
		ContentValues values = new ContentValues();
		values.put(column, (getStatValue(gameId, playerId, column) + valToAdd));

		getDB().update(TABLE_STATS, values, COL_GAMEID + " = ? AND " + COL_PLAYERID + " = ?", 
				new String[] {gameId, playerId} );

		
	}
	
	public void subOut(String gameId, String playerIdOut, String playerIdIn, int location)
	{
		ContentValues values = new ContentValues();
		values.put(COL_ONFIELD, 0);
		
		ContentValues values1 = new ContentValues();
		values1.put(COL_ONFIELD, location);
		
		getDB().update(TABLE_STATS, values, COL_GAMEID + " = ? AND " + COL_PLAYERID + " = ?",
				new String[] {gameId, playerIdOut} );
		
		getDB().update(TABLE_STATS, values1, COL_GAMEID + " = ? AND " + COL_PLAYERID + " = ?",
				new String[] {gameId, playerIdIn} );
		
		
	}

	public Cursor getGameStats(String gameId)
	{
		String query = "SELECT "
				+ TABLE_PLAYER + "." + COL_NUMBER + ", "
				+ TABLE_PLAYER + "." + COL_LNAME + ", "
				+ TABLE_STATS + "." + COL_SHOTS + ", "
				+ TABLE_STATS + "." + COL_GOALS + ", "
				+ TABLE_STATS + "." + COL_ASSISTS + ", "
				+ TABLE_STATS + "." + COL_STEALS + ", "
				+ TABLE_STATS + "." + COL_TURNOVERS + ", "
				+ TABLE_STATS + "." + COL_SAVES + ", "
				+ TABLE_STATS + "." + COL_SNITCHES + ", "
				+ TABLE_STATS + "." + COL_PLUSSES + ", "
				+ TABLE_STATS + "." + COL_MINUSES + ", "
				+ TABLE_STATS + "." + COL_TOTAL_TIME
				+ " FROM " 
				+ TABLE_PLAYER + ", " 
				+ TABLE_STATS
				+ " WHERE "
				+ TABLE_PLAYER + "." + COL_ID + " = " + TABLE_STATS + "." + COL_PLAYERID
				+ " AND "
				+ TABLE_STATS + "." + COL_GAMEID + " = '" + gameId + "'";


		Cursor c = getDB().rawQuery(query, null);

		if (c.moveToFirst())
		{
			return c;
		}
		else
		{
            c.close();
			return null;
		}
	}

    //-----------------------------------------------------------
    //-----------------------------------------------------------
    //----------------New Actions--------------------------------
    //-----------------------------------------------------------

    public void addNewAction(NewActionDb n) {
        ContentValues values = new ContentValues();
        values.put(COL_ID, n.getId());
        values.put(COL_GAMEID, n.getGameId());
        values.put(COL_ACTION, n.getActualAction().ordinal());
        values.put(COL_VID_TIME, n.getYoutubeTime());
        values.put(COL_PLAYER_OUT, n.getPlayerOut());
        values.put(COL_PLAYER_IN, n.getPlayerIn());
        values.put(COL_LOCATION, n.getLoc());

        getDB().insert(TABLE_VIDEO, null, values);
    }

    public NewActionDb getNewAction(String id) {
        NewActionDb ret = null;
        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE " + COL_ID + " = ?";
        Cursor c = getDB().rawQuery(query, new String[] {id});
        if (c.moveToFirst()) {
            ret = new NewActionDb();
            ret.setId(id);

            int aa = c.getInt(c.getColumnIndex(COL_ACTION));
            NewActionDb.NewAction dd = NewActionDb.NewAction.values()[aa];
            ret.setActualAction(dd);

            ret.setPlayerIn(c.getString(c.getColumnIndex(COL_PLAYER_IN)));
            ret.setGameId(c.getString(c.getColumnIndex(COL_GAMEID)));
            ret.setPlayerOut(c.getString(c.getColumnIndex(COL_PLAYER_OUT)));
            ret.setYoutubeTime(c.getInt(c.getColumnIndex(COL_VID_TIME)));

            ret.setLoc(c.getInt(c.getColumnIndex(COL_LOCATION)));
        }
        return ret;
    }

    public List<NewActionDb> getAllActionsFromGame(String gameId) {
        List<NewActionDb> ret = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE " + COL_GAMEID + " = ? " +
                "ORDER BY " + COL_VID_TIME + " ASC";
        Cursor c = getDB().rawQuery(query, new String[] {gameId});

        if (c.moveToFirst()) {
            do {
                NewActionDb toAdd = new NewActionDb();
                toAdd.setId(c.getString(c.getColumnIndex(COL_ID)));

                int aa = c.getInt(c.getColumnIndex(COL_ACTION));
                NewActionDb.NewAction dd = NewActionDb.NewAction.values()[aa];
                toAdd.setActualAction(dd);

                toAdd.setPlayerIn(c.getString(c.getColumnIndex(COL_PLAYER_IN)));
                toAdd.setGameId(c.getString(c.getColumnIndex(COL_GAMEID)));
                toAdd.setPlayerOut(c.getString(c.getColumnIndex(COL_PLAYER_OUT)));
                toAdd.setYoutubeTime(c.getInt(c.getColumnIndex(COL_VID_TIME)));

                toAdd.setLoc(c.getInt(c.getColumnIndex(COL_LOCATION)));

                ret.add(toAdd);
            } while (c.moveToNext());
        }

        return ret;
    }

    public void deleteNewAction(String id) {
        getDB().delete(TABLE_VIDEO, COL_ID + " = ?", new String[] {id});
    }

    public List<NewActionDb> getAllSubActionsFromGame(String gameId) {
        List<NewActionDb> ret = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE "
                + COL_ACTION + " = ? AND "
                + COL_GAMEID + " = ? ORDER BY " + COL_VID_TIME + " ASC";
        Cursor c = getDB().rawQuery(query,
                new String[] {String.valueOf(NewActionDb.NewAction.SUB.ordinal()), gameId});

        if (c.moveToFirst()) {
            do {
                NewActionDb toAdd = new NewActionDb();
                toAdd.setId(c.getString(c.getColumnIndex(COL_ID)));

                int aa = c.getInt(c.getColumnIndex(COL_ACTION));
                NewActionDb.NewAction dd = NewActionDb.NewAction.values()[aa];
                toAdd.setActualAction(dd);

                toAdd.setPlayerIn(c.getString(c.getColumnIndex(COL_PLAYER_IN)));
                toAdd.setGameId(c.getString(c.getColumnIndex(COL_GAMEID)));
                toAdd.setPlayerOut(c.getString(c.getColumnIndex(COL_PLAYER_OUT)));
                toAdd.setYoutubeTime(c.getInt(c.getColumnIndex(COL_VID_TIME)));

                toAdd.setLoc(c.getInt(c.getColumnIndex(COL_LOCATION)));

                ret.add(toAdd);
            } while (c.moveToNext());
        }

        return ret;

    }

    public List<NewActionDb> getAllHomeScoresFromGame(String gameId) {
        List<NewActionDb> ret = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE "
                + COL_ACTION + " = ? AND "
                + COL_GAMEID + " = ? ORDER BY " + COL_VID_TIME + " ASC";
        Cursor c = getDB().rawQuery(query,
                new String[] {String.valueOf(NewActionDb.NewAction.GOAL.ordinal()), gameId});

        if (c.moveToFirst()) {
            do {
                NewActionDb toAdd = new NewActionDb();
                toAdd.setId(c.getString(c.getColumnIndex(COL_ID)));

                int aa = c.getInt(c.getColumnIndex(COL_ACTION));
                NewActionDb.NewAction dd = NewActionDb.NewAction.values()[aa];
                toAdd.setActualAction(dd);

                toAdd.setPlayerIn(c.getString(c.getColumnIndex(COL_PLAYER_IN)));
                toAdd.setGameId(c.getString(c.getColumnIndex(COL_GAMEID)));
                toAdd.setPlayerOut(c.getString(c.getColumnIndex(COL_PLAYER_OUT)));
                toAdd.setYoutubeTime(c.getInt(c.getColumnIndex(COL_VID_TIME)));

                toAdd.setLoc(c.getInt(c.getColumnIndex(COL_LOCATION)));

                ret.add(toAdd);
            } while (c.moveToNext());
        }

        return ret;
    }

    public List<NewActionDb> getAllAwayScoresFromGame(String gameId) {
        List<NewActionDb> ret = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE "
                + COL_ACTION + " = ? AND "
                + COL_GAMEID + " = ? ORDER BY " + COL_VID_TIME + " ASC";
        Cursor c = getDB().rawQuery(query,
                new String[] {String.valueOf(NewActionDb.NewAction.AWAY_GOAL.ordinal()), gameId});

        if (c.moveToFirst()) {
            do {
                NewActionDb toAdd = new NewActionDb();
                toAdd.setId(c.getString(c.getColumnIndex(COL_ID)));

                int aa = c.getInt(c.getColumnIndex(COL_ACTION));
                NewActionDb.NewAction dd = NewActionDb.NewAction.values()[aa];
                toAdd.setActualAction(dd);

                toAdd.setPlayerIn(c.getString(c.getColumnIndex(COL_PLAYER_IN)));
                toAdd.setGameId(c.getString(c.getColumnIndex(COL_GAMEID)));
                toAdd.setPlayerOut(c.getString(c.getColumnIndex(COL_PLAYER_OUT)));
                toAdd.setYoutubeTime(c.getInt(c.getColumnIndex(COL_VID_TIME)));

                toAdd.setLoc(c.getInt(c.getColumnIndex(COL_LOCATION)));

                ret.add(toAdd);
            } while (c.moveToNext());
        }

        return ret;
    }

    public int getSnitchCatchFromGameAtTime(int time, String gameId) {

        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE "
                + COL_GAMEID + " = ? AND "
                + "(" + COL_ACTION + " = ? OR "
                + COL_ACTION + " = ?)";

        Cursor c = getDB().rawQuery(query,
                new String[] {
                        gameId,
                        String.valueOf(NewActionDb.NewAction.SNITCH_CATCH.ordinal()),
                        String.valueOf(NewActionDb.NewAction.AWAY_SNITCH_CATCH.ordinal()) }
        );

        if (c.moveToFirst()) {
            int timeOfCatch = (c.getInt(c.getColumnIndex(COL_VID_TIME)));
            if (timeOfCatch > time) {
                return -1;
            }
            int aa = c.getInt(c.getColumnIndex(COL_ACTION));
            NewActionDb.NewAction dd = NewActionDb.NewAction.values()[aa];
            if (dd == NewActionDb.NewAction.SNITCH_CATCH) {
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

    public void clearAllActionsFromGame(String id) {
        int i = getDB().delete(TABLE_VIDEO, COL_GAMEID + " = ?", new String[] {id});
        return;
    }

    public void addAllActions(List<NewActionDb> actions) {
        for (NewActionDb action : actions) {
            addNewAction(action);
        }
    }

    public List<NewActionDb> getActionsAtTime(String id, int time) {
        List<NewActionDb> ret = new ArrayList<>();
        int lowerBoundMillis = time * 1000;
        int upperBoundMillis = (time+1) * 1000;

        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE "
                + COL_GAMEID + " = ?" + " AND "
                + COL_VID_TIME + " >= " + lowerBoundMillis + " AND "
                + COL_VID_TIME + " <= " + upperBoundMillis
                + " ORDER BY " + COL_VID_TIME + " ASC";
        Cursor c = getDB().rawQuery(query, new String[] {id});
        if (c.moveToFirst()) {
            do {
                NewActionDb toAdd = new NewActionDb();
                toAdd.setPlayerIn(c.getString(c.getColumnIndex(COL_PLAYER_IN)));
                toAdd.setPlayerOut(c.getString(c.getColumnIndex(COL_PLAYER_OUT)));
                toAdd.setGameId(c.getString(c.getColumnIndex(COL_GAMEID)));
                toAdd.setYoutubeTime(c.getInt(c.getColumnIndex(COL_VID_TIME)));

                int aa = c.getInt(c.getColumnIndex(COL_ACTION));
                NewActionDb.NewAction dd = NewActionDb.NewAction.values()[aa];
                toAdd.setActualAction(dd);

                toAdd.setId(c.getString(c.getColumnIndex(COL_ID)));
                toAdd.setLoc(c.getInt(c.getColumnIndex(COL_LOCATION)));
                ret.add(toAdd);
            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    //Return -1 on a pause_clock
    //Return 0 on nothing
    //Return 1 on a resume or start clock
    public int pauseAction(String gameId, int time) {
        List<NewActionDb> actions = getActionsAtTime(gameId, time);
        for (NewActionDb action : actions) {
            switch (action.getActualAction()) {
                case GAME_START:
                    return 1;
                case START_CLOCK:
                    return 1;
                case PAUSE_CLOCK:
                    return -1;
                case GAME_END:
                    return -1;
                default:
                    break;
            }
        }
        return 0;
    }

    public void updateNewActionTime(NewActionDb a) {
        ContentValues values = new ContentValues();
        values.put(COL_VID_TIME, a.getYoutubeTime());
        getDB().update(TABLE_VIDEO, values, COL_ID + " = ?", new String[]{a.getId()});
    }

    public boolean getSeekerOnPitch(String gameId, int time) {
        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE "
                + COL_ACTION + " = ? AND "
                + COL_GAMEID + " = ?";
        Cursor c = getDB().rawQuery(query,
                new String[] {String.valueOf(NewActionDb.NewAction.SNITCH_ON_PITCH.ordinal()), gameId});
        if (c.moveToFirst()) {
            int timeOfSnitch = c.getInt(c.getColumnIndex(COL_VID_TIME));
            if (timeOfSnitch <= time) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public List<NewActionDb> getAllActionsFromGameAfterSnitch(String gameId) {
        List<NewActionDb> ret = new ArrayList<>();
        int timeOfSnitchOnPitch;
        String query1 = "SELECT * FROM " + TABLE_VIDEO + " WHERE "
                + COL_GAMEID + " = ? AND "
				+ COL_ACTION + " = ?";
        Cursor c1 = getDB().rawQuery(query1,
                new String[] {
                        gameId,
                        String.valueOf(NewActionDb.NewAction.SNITCH_ON_PITCH.ordinal()) }
        );

        if (c1.moveToFirst()) {
            timeOfSnitchOnPitch = c1.getInt(c1.getColumnIndex(COL_VID_TIME));
        } else {
            return ret;
        }



        String query = "SELECT * FROM " + TABLE_VIDEO + " WHERE "
                + COL_GAMEID + " = ? AND "
                + COL_VID_TIME + " > ? " +
                "ORDER BY " + COL_VID_TIME + " ASC";
        Cursor c = getDB().rawQuery(query,
				new String[] {gameId,
				String.valueOf(timeOfSnitchOnPitch)});

        if (c.moveToFirst()) {
            do {
                NewActionDb toAdd = new NewActionDb();
                toAdd.setId(c.getString(c.getColumnIndex(COL_ID)));

                int aa = c.getInt(c.getColumnIndex(COL_ACTION));
                NewActionDb.NewAction dd = NewActionDb.NewAction.values()[aa];
                toAdd.setActualAction(dd);

                toAdd.setPlayerIn(c.getString(c.getColumnIndex(COL_PLAYER_IN)));
                toAdd.setGameId(c.getString(c.getColumnIndex(COL_GAMEID)));
                toAdd.setPlayerOut(c.getString(c.getColumnIndex(COL_PLAYER_OUT)));
                toAdd.setYoutubeTime(c.getInt(c.getColumnIndex(COL_VID_TIME)));

                toAdd.setLoc(c.getInt(c.getColumnIndex(COL_LOCATION)));

                ret.add(toAdd);
            } while (c.moveToNext());
        }

        return ret;
    }

	// -------------------------------------------------------------------------

	public SQLiteDatabase getDB()
	{
		if( myDabatabse == null )
		{
			myDabatabse = getWritableDatabase();
		}
		return myDabatabse;
	}
	
	// -------------------------------------------------------------------------


    public void update1(String vidId, String teamId) {
        ContentValues values = new ContentValues();
        values.put(COL_GAMEID, vidId + teamId);
        int updated = getDB().update(TABLE_VIDEO, values, COL_GAMEID + " = ?", new String[]{vidId});
        String str = "";
        str = "Hello";

    }

    // Change values of playerId
    public void update2() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, "vtK6JXIKC9");
        String[] oldId = new String[]{"433a9fdd-2966-416e-bb56-8819c22b39d8"};
        getDB().update(TABLE_PLAYER, values, COL_ID + " = ?", oldId);
    }

    public void update3() {
        ContentValues values = new ContentValues();
        values.put(COL_PLAYER_IN, "vtK6JXIKC9");
        String[] oldId = new String[]{"433a9fdd-2966-416e-bb56-8819c22b39d8"};
        getDB().update(TABLE_VIDEO, values, COL_PLAYER_IN + " = ?", oldId);
    }

    public void update4() {
        ContentValues values = new ContentValues();
        values.put(COL_PLAYER_OUT, "vtK6JXIKC9");
        String[] oldId = new String[]{"433a9fdd-2966-416e-bb56-8819c22b39d8"};
        getDB().update(TABLE_VIDEO, values, COL_PLAYER_OUT + " = ?", oldId);
    }



}
