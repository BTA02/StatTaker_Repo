package local.stattaker.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import local.stattaker.model.GameDb;
import local.stattaker.model.PlayerDb;
import local.stattaker.model.TeamDb;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	// Logcat tag
	private static final String TAG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 47;

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

	//Shared strings
	public static final String COL_ID = "_id";

	// Table Names
	public static final String TABLE_PLAYER = "playerTable";
	public static final String TABLE_GAME = "gameTable";
	public static final String TABLE_TEAM = "teamTable";
	public static final String TABLE_STATS = "statsTable";
	public static final String TABLE_TIME = "timeTable";

	// GAME Table - column names
	public static final String COL_GAMEID = "g_id";
	public static final String COL_HOME_TEAM = "homeTeamId";
	public static final String COL_AWAY_TEAM = "awayTeamId";
	public static final String COL_GAME_TIME = "gameTime";
	public static final String COL_HOME_SCORE = "homeScore";
	public static final String COL_AWAY_SCORE = "awayScore";

	// TEAM Table = column names
	public static final String COL_TEAMID = "t_id";
	public static final String COL_TEAM_NAME = "teamName";
	public static final String COL_ACTIVE = "active";

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
	

	// Table Create Statements
	// GAME table create statement
	// I also need to make sure I can add the proper gameId to each player
	//I think this will work
	private static final String CREATE_TABLE_GAME = "CREATE TABLE "
			+ TABLE_GAME + "(" + COL_ID + " TEXT, " 
			+ COL_HOME_TEAM + " TEXT, " 
			+ COL_AWAY_TEAM + " TEXT, " 
			+ COL_GAME_TIME + " INT, "
			+ COL_HOME_SCORE + " INT, "
			+ COL_AWAY_SCORE + " INT)";

	private static final String CREATE_TABLE_PLAYER = "CREATE TABLE " + TABLE_PLAYER
			+ "(" + COL_ID + " TEXT, " 
			+ COL_NUMBER + " TEXT, " 
			+ COL_FNAME + " TEXT, " 
			+ COL_LNAME + " TEXT)";

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
			+ "(" + COL_ID + " TEXT, "
			+ COL_TEAM_NAME + " TEXT, "
			+ COL_PLAYERID + " TEXT, "
			+ COL_ACTIVE + ")";//each row is just an entry in a roster
	//row looks like:
	//"892-3011-df9a902", "University of Michigan, "582-2811-pa8f123", 1 (active)
	//"892-3011-df9a902", "University of Michigan, "113-9301-jk8b553", 0 (not active)
	//...
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(CREATE_TABLE_GAME);
		db.execSQL(CREATE_TABLE_PLAYER);
		db.execSQL(CREATE_TABLE_STAT);
		db.execSQL(CREATE_TABLE_TIME);
		db.execSQL(CREATE_TABLE_TEAM);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAM);

		// create new tables
		onCreate(db);
	}

	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//----------------TEAM---------------------------------------
	//-----------------------------------------------------------
	public Cursor getAllTeamsCursor()
	{
		SQLiteDatabase db = this.getReadableDatabase();

		//this is a problem because I need to select DISTINCT

		String query = "SELECT * FROM " + TABLE_TEAM;
		Cursor c = db.rawQuery(query, null);
		if (c.moveToFirst())
		{
			db.close();
			return c;
		}
		else
		{
			db.close();
			return null;
		}
	}

	public List<TeamDb> getAllTeamsList()
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT * FROM " + TABLE_TEAM;
		Cursor c = db.rawQuery(query, null);

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
		db.close();

		return teamList;
	}

	public TeamDb getTeamFromId(String id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT " + COL_TEAM_NAME + " FROM " + TABLE_TEAM
				+ " WHERE " + COL_ID + " = ?";
		Cursor c = db.rawQuery(query, new String[]{ id });
		TeamDb ret = new TeamDb();
		if (c.moveToFirst())
		{
			ret.setName( c.getString(c.getColumnIndex(COL_TEAM_NAME)) );
			ret.setId(id);
		}
		else
		{
			Log.e(TAG, "team not found in getTeamNameFromId");
			return null;
		}
		db.close();
		return ret;
	}

	public void addTeam(String teamId, String teamName)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_ID, teamId);
		values.put(COL_TEAM_NAME, teamName);
		values.put(COL_PLAYERID, ""); //null player, so I can access the first row whenever

		long num = db.insert(TABLE_TEAM, null, values);

		db.close();
	}// addTeam

	//I can update all references to this team to just have the name?
	//I can also just store team name ONLY when doing stuff
	//I'll have to think about this
	public void deleteTeam(String teamId)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TEAM, COL_ID + " = ?", new String[]{ teamId } );
		db.close();
	}
	
	public List<PlayerDb> getActivePlayers(String teamId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		List<PlayerDb> ret = new ArrayList<PlayerDb>();

		String query = "SELECT "
				+ TABLE_PLAYER + "." + COL_ID + ", "
				+ TABLE_PLAYER + "." + COL_NUMBER + ", "
				+ TABLE_PLAYER + "." + COL_FNAME + ", "
				+ TABLE_PLAYER + "." + COL_LNAME + ", "
				+ TABLE_PLAYER + "." + COL_ACTIVE + ", "
				+ TABLE_TEAM + "." + COL_PLAYERID
				+ " FROM " + TABLE_PLAYER + ", " + TABLE_TEAM
				+ " WHERE " 
				+ TABLE_TEAM + "." + COL_ID + " = '" + teamId + "'"
				+ " AND "
				+ TABLE_TEAM + "." + COL_PLAYERID + " = " + TABLE_PLAYER + "." + COL_ID;

		Cursor c = db.rawQuery(query, null);
		//Cursor c = db.rawQuery(query, new String[] {teamId});
		if (c.moveToFirst())
		{
			do
			{
				PlayerDb p = new PlayerDb();
				p.setFname(c.getString(c.getColumnIndex(COL_FNAME)));
				p.setLname(c.getString(c.getColumnIndex(COL_LNAME)));
				p.setNumber(c.getString(c.getColumnIndex(COL_NUMBER)));
				p.setPlayerId(c.getString(c.getColumnIndex(COL_ID)));
				
			}
			while (c.moveToNext());
		}


		return ret;
	}
	
	public boolean isPlayerActiveOnTeam(String teamId, String playerId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT * FROM " + TABLE_TEAM
				+ " WHERE "
				+ COL_ID + " = '" + teamId + "'"
				+ " AND "
				+ COL_PLAYERID + " = '" + playerId + "'";
		Cursor c = db.rawQuery(query, null);
		if (c.moveToFirst())
		{
			if (c.getInt(c.getColumnIndex(COL_ACTIVE)) == 1)
			{
				db.close();
				return true;
			}
		}
		db.close();
		return false;
	}
	
	public void updateActiveInfo(String teamId, String playerId, int newVal)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(COL_ACTIVE, newVal);
		
		db.update(TABLE_TEAM, values, COL_ID + " = ? AND " + COL_PLAYERID + " = ?", new String[] {teamId, playerId} );
	}

	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//----------------Players------------------------------------
	//-----------------------------------------------------------

	//Works
	public void addPlayerToTeam(String playerId, String teamId)
	{
		TeamDb team = getTeamFromId(teamId);

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_ID, teamId);
		values.put(COL_TEAM_NAME, team.getName());
		values.put(COL_PLAYERID, playerId);

		db.insert(TABLE_TEAM, null, values);

		db.close();

	}// addPlayerToTeam

	//works
	public List<PlayerDb> getAllPlayersFromTeam(String teamId, int activeFlag)
	{
		SQLiteDatabase db = this.getReadableDatabase();

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
					+ teamId + "'";

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
					+ teamId + "'";
		}
		Cursor c = db.rawQuery(query, null);

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
		db.close();
		return playerList;

	}// getAllPlayersFromTeam
	

	public Cursor getAllPlayersFromTeamCursor(String teamId, int activeFlag)
	{
		SQLiteDatabase db = this.getReadableDatabase();
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
					+ teamId + "'";

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
					+ teamId + "'";
		}
		Cursor c = db.rawQuery(query, null);
		if(c.moveToFirst())
		{
			return c;
		}
		return null;
	}

	public void updatePlayerInfo(PlayerDb updatedPlayer)
	{
		SQLiteDatabase db =  this.getWritableDatabase();

		String playerId = updatedPlayer.getPlayerId();

		ContentValues values = new ContentValues();

		values.put(COL_FNAME, updatedPlayer.getFname());
		values.put(COL_LNAME, updatedPlayer.getLname());
		values.put(COL_NUMBER, updatedPlayer.getNumber());

		db.update(TABLE_PLAYER, values, COL_ID + " = ?", new String[] {playerId} );

		db.close();
	}

	public void addPlayer(String id, String number, String fname, String lname)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_ID, id);
		values.put(COL_NUMBER, number);
		values.put(COL_FNAME, fname);
		values.put(COL_LNAME, lname);

		db.insert(TABLE_PLAYER, null, values);

		db.close();
	}

	public List<PlayerDb> getAllPlayersList()
	{
		SQLiteDatabase db = this.getReadableDatabase();

		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		String query;

		//this might actually be working
		query = "SELECT * FROM " + TABLE_PLAYER;


		Cursor c = db.rawQuery(query, null);

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
		db.close();
		return playerList;
	}

	public Cursor getAllPlayersCursor()
	{
		SQLiteDatabase db = this.getReadableDatabase();

		//this is a problem because I need to select DISTINCT

		String query = "SELECT * FROM " + TABLE_PLAYER + " ORDER BY " + COL_LNAME + " ASC";
		//String query = "SELECT * FROM " + TABLE_PLAYER;
		Cursor c = db.rawQuery(query, null);
		if (c.moveToFirst())
		{
			db.close();
			return c;
		}
		else
		{
			db.close();
			return null;
		}
	}

	public boolean playerExistsOnTeam(String playerId, String teamId)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT 1 FROM " + TABLE_TEAM + ", " + TABLE_PLAYER
				+ " WHERE "
				+ TABLE_TEAM + "." + COL_PLAYERID + " = "
				+ TABLE_PLAYER + "." + COL_ID
				+ " AND " + TABLE_TEAM + "." + COL_ID + " = '"
				+ teamId + "' AND " + TABLE_PLAYER + "." + COL_ID
				+ " = '" + playerId + "'";
		Cursor c = db.rawQuery(query, null);
		
		if (c.moveToFirst())
		{
			db.close();
			return true;
		}
		db.close();
		return false;
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
		SQLiteDatabase db = this.getReadableDatabase();

		ContentValues values = new ContentValues();
		String gId = UUID.randomUUID().toString();
		values.put(COL_ID, gId);
		values.put(COL_HOME_TEAM, teamId);
		values.put(COL_AWAY_TEAM, awayTeamName);
		values.put(COL_HOME_SCORE, 0);
		values.put(COL_AWAY_SCORE, 0);
		values.put(COL_GAME_TIME, 0);

		db.insert(TABLE_GAME, null, values);
		db.close();

		//now create the necessary stats tables for each "active" player
		

		List<PlayerDb> activePlayers = getActivePlayers(teamId);
		
		SQLiteDatabase db1 = this.getWritableDatabase();
		ContentValues values1 = new ContentValues();
		long qq;
		
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
			if (i < 7)
			{
				values1.put(COL_ONFIELD, (i + 1));
			}
			else
			{
				values1.put(COL_ONFIELD, 0);
			}
			qq = db1.insert(TABLE_STATS, null, values1);
		}


		db1.close();
		return gId;
	}

	public List<GameDb> getAllGamesForTeam(String teamId)
	{
		List<GameDb> gamesList = new ArrayList<GameDb>();

		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT * FROM " + TABLE_GAME + " WHERE "
				+ COL_HOME_TEAM + " = ?";
		Cursor c = db.rawQuery(query, new String[]{ teamId });


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

				gamesList.add(g);
			}
			while (c.moveToNext());
		}
		db.close();
		return gamesList;
	}

	public GameDb getGameInfo(String gId)
	{
		GameDb ret = new GameDb();

		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT * FROM " + TABLE_GAME + " WHERE " 
				+ COL_ID + " = ?";
		Cursor c = db.rawQuery(query, new String[] {gId} );


		if (c.moveToFirst())
		{
			ret.setId(c.getString(c.getColumnIndex(COL_ID)));
			ret.setAwayScore(c.getInt(c.getColumnIndex(COL_AWAY_SCORE)));
			ret.setAwayTeam(c.getString(c.getColumnIndex(COL_AWAY_TEAM)));
			ret.setGameTimeSeconds(c.getInt(c.getColumnIndex(COL_GAME_TIME)));
			ret.setHomeScore(c.getInt(c.getColumnIndex(COL_HOME_SCORE)));
			ret.setHomeTeam(c.getString(c.getColumnIndex(COL_HOME_TEAM)));
		}
		db.close();
		return ret;
	}

	//untested
	public List<PlayerDb> getOnFieldPlayersFromGame(String gameId)
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		SQLiteDatabase db = this.getReadableDatabase();

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
				+ TABLE_PLAYER + "." + COL_ID + " = " + TABLE_STATS + "." + COL_PLAYERID;

		Cursor c = db.rawQuery(query, null);
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
		db.close();
		return playerList;
	}

	public List<PlayerDb> getOffFieldPlayersFromGame(String gameId)
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		SQLiteDatabase db = this.getReadableDatabase();

		//a player cna't have a stat table... right?
		//what makes sense then?
		//stats have players, players dont have stats
		//that is weird, but it MUST be this way
		//stat row would be

		String query = "SELECT "
				+ TABLE_PLAYER + "." + COL_ID
				+ TABLE_PLAYER + "." + COL_NUMBER
				+ TABLE_PLAYER + "." + COL_FNAME
				+ TABLE_PLAYER + "." + COL_LNAME
				+ TABLE_STATS + "." + COL_ONFIELD
				+ " FROM " + TABLE_PLAYER + ", " + TABLE_STATS
				+ " WHERE " 
				+ TABLE_STATS + "." + COL_GAMEID + " = ?"
				+ " AND "
				+ TABLE_PLAYER + "." + COL_ID + " = " + TABLE_STATS + "." + COL_PLAYERID;



		Cursor c = db.rawQuery(query, new String[] {gameId} );

		if (c.moveToFirst())
		{
			do
			{

				PlayerDb p = new PlayerDb(); //just a db row
				p.setPlayerId(c.getString((c.getColumnIndex(COL_PLAYERID))));
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
		db.close();
		return playerList;
	}

	public int getGameTime(String gameId)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT " + COL_GAME_TIME + " FROM "
				+ TABLE_GAME + " WHERE " + COL_GAMEID + " = ?";
		Cursor c = db.rawQuery(query, new String[] {gameId} );

		if (c.moveToFirst())
		{
			db.close();
			return c.getInt(c.getColumnIndex(COL_GAME_TIME));
		}
		db.close();
		return 0;
	}

	public int getListLocation(String gId, String pId)
	{
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT " + COL_ONFIELD
				+ " FROM " + TABLE_STATS
				+ " WHERE "
				+ COL_GAMEID + " = ?" + " AND "
				+ COL_PLAYERID + " = ?";

		Cursor c = db.rawQuery(query, new String[] {gId, pId} );



		if (c.moveToFirst())
		{
			db.close();
			return c.getInt(c.getColumnIndex(COL_ONFIELD));
		}
		db.close();
		return 1;
	}

	public void updateTime(String gId, int valToAdd)
	{
		int curTime = getGameTime(gId);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_GAME_TIME, (curTime + valToAdd) );

		db.update(TABLE_GAME, values, COL_ID + " = ?", new String[] {gId} );
		db.close();
	}

	//-----------------------------------------------------------
	//-----------------------------------------------------------
	//----------------Stats--------------------------------------
	//-----------------------------------------------------------

	public int getStatValue(String gameId, String playerId, String column)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_STATS + " WHERE "
				+ COL_GAMEID + " = ? AND " + COL_PLAYERID + " = ?";
		Cursor c = db.rawQuery(query, new String[] {gameId, playerId});

		if (c.moveToFirst())
		{
			db.close();
			return c.getInt(c.getColumnIndex(column));
		}
		db.close();
		return 0;

	}

	public void updateStat(String gameId, String playerId, int valToAdd, String column)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(column, (getStatValue(gameId, playerId, column) + valToAdd));


		db.update(TABLE_STATS, values, COL_GAMEID + " = ? AND " + COL_PLAYERID + " = ?", 
				new String[] {gameId, playerId} );
		db.close();
	}
	
	public Cursor getGameStats(String gameId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT "
				+ TABLE_PLAYER + "." + COL_LNAME
				+ TABLE_PLAYER + "." + COL_LNAME
				+ TABLE_STATS + "." + COL_SHOTS
				+ TABLE_STATS + "." + COL_GOALS
				+ TABLE_STATS + "." + COL_ASSISTS
				+ TABLE_STATS + "." + COL_TURNOVERS
				+ TABLE_STATS + "." + COL_SAVES
				+ TABLE_STATS + "." + COL_SNITCHES
				+ TABLE_STATS + "." + COL_PLUSSES
				+ TABLE_STATS + "." + COL_MINUSES
				+ TABLE_STATS + "." + COL_TOTAL_TIME
				+ TABLE_TIME + "." + COL_TIME_IN
				+ TABLE_TIME + "." + COL_TIME_OUT
				+ " FROM " + TABLE_STATS
				+ " WHERE "
				+ TABLE_STATS + "." + COL_GAMEID + " = '" + gameId + "'";
		
		Cursor c = db.rawQuery(query, null);

		db.close();
		return c;
	}


	

	//closing database
	public void closeDB() 
	{
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}



}
