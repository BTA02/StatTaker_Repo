package local.stattaker.helper;

import java.util.ArrayList;
import java.util.List;

import local.stattaker.model.GameDb;
import local.stattaker.model.PlayerDb;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static DatabaseHelper instance;
	

	public static DatabaseHelper getHelper(Context context)
	{
		if (instance == null)
		{
			instance = new DatabaseHelper(context);
		}
		return instance;
	}

  // Logcat tag
  private static final String LOG = "DatabaseHelper";

  // Database Version
  private static final int DATABASE_VERSION = 32;

  // Database Name
  private static final String DATABASE_NAME = "quidditchGames";

  // Table Names
  private static final String TABLE_PLAYER = "playerTable";
  private static final String TABLE_GAME = "gameTable";

  // Common column names
  private static final String COL_PLAYERID = "playerId";
  private static final String COL_TEAMNAME = "teamName";

  // GAME Table - column names
  private static final String COL_GAMEID = "gameId";
  private static final String COL_OPPONENT = "opponent";
  private static final String COL_SHOTS = "shots";
  private static final String COL_GOALS = "goals";
  private static final String COL_ASSISTS = "assists";
  private static final String COL_STEALS = "steals";
  private static final String COL_TURNOVERS = "turnovers";
  private static final String COL_SAVES = "saves";
  private static final String COL_SNITCHES = "snitches";
  private static final String COL_PLUSSES = "plusses";
  private static final String COL_MINUSES = "minuses";
  private static final String COL_ONFIELD = "onField";
  private static final String COL_TIME = "time";

  // PLAYER Table - column names
  private static final String COL_NUMBER = "number";
  private static final String COL_FNAME = "fname";
  private static final String COL_LNAME = "lname";
  private static final String COL_ACTIVE = "active";

  // Table Create Statements
  // GAME table create statement
  // I also need to make sure I can add the proper gameId to each player
  //I think this will work
  private static final String CREATE_TABLE_GAME = "CREATE TABLE "
          + TABLE_GAME + "(" + COL_GAMEID + " INTEGER," + COL_TEAMNAME
          + " TEXT," + COL_OPPONENT + " TEXT," + COL_PLAYERID + " INTEGER," + COL_SHOTS
          + " INTEGER," + COL_GOALS + " INTEGER," 
          + COL_ASSISTS + " INTEGER," + COL_STEALS
          + " INTEGER," + COL_TURNOVERS +  " INTEGER," + COL_SAVES
          + " INTEGER," + COL_SNITCHES + " INTEGER," + COL_PLUSSES
          + " INTEGER," + COL_MINUSES + " INTEGER, "
          + COL_ONFIELD + " INTEGER, "
          + COL_TIME + " INTEGER, "
          + "PRIMARY KEY (" + COL_GAMEID + ", " + COL_PLAYERID + "), "
          + "FOREIGN KEY(" + COL_PLAYERID + ") REFERENCES "
          + TABLE_PLAYER + "(" + COL_PLAYERID + ")"
          + ")";

  // PLAYER table create statement
  //actually good
  private static final String CREATE_TABLE_PLAYER = "CREATE TABLE " + TABLE_PLAYER
          + "(" + COL_TEAMNAME + " TEXT," + COL_PLAYERID 
          + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_NUMBER + " TEXT,"
          + COL_FNAME + " TEXT," + COL_LNAME + " TEXT,"
          + COL_ACTIVE + " INTEGER" + ")";

  public DatabaseHelper(Context context) 
  {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }
  
  

  @Override
  public void onCreate(SQLiteDatabase db) 
  {
      db.execSQL(CREATE_TABLE_GAME);
      db.execSQL(CREATE_TABLE_PLAYER);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // on upgrade drop older tables
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);

      // create new tables
      onCreate(db);
  }
  
  /*
   * Insert a row in GAME Table
   * set onField in the createGame code
   */
  public long insertGameRow(GameDb g)
  {
      SQLiteDatabase db = this.getWritableDatabase();
   
      ContentValues values = new ContentValues();
      values.put(COL_GAMEID, g.getGameId());
      values.put(COL_TEAMNAME, g.getTeamName());
      values.put(COL_OPPONENT, g.getOpponent());
      values.put(COL_PLAYERID, g.getPlayerId());
      values.put(COL_SHOTS, g.getShots());
      values.put(COL_GOALS, g.getGoals());
      values.put(COL_ASSISTS, g.getAssists());
      values.put(COL_STEALS, g.getSteals());
      values.put(COL_TURNOVERS, g.getTurnovers());
      values.put(COL_SAVES, g.getSaves());
      values.put(COL_SNITCHES, g.getSnitches());
      values.put(COL_PLUSSES, g.getPlusses());
      values.put(COL_MINUSES, g.getMinuses());
      values.put(COL_ONFIELD, g.getOnField());
      values.put(COL_TIME, g.getTime());
      //these should start at 0 for everything except onField
   
      // insert row
      long game_id = db.insert(TABLE_GAME, null, values);
   
      return game_id; //I didn't know we got a return id from this
  }
  
  //this just returns a list of the old games team has played
  //only returns a list of opponents
  public List<GameDb> getAllGames(String team)
  {
  	List<GameDb> oldGames = new ArrayList<GameDb>();
  	SQLiteDatabase db = this.getReadableDatabase();
  	String selectGames = "SELECT DISTINCT " + COL_OPPONENT + ", "
  			+ COL_GAMEID + " FROM " + TABLE_GAME 
  			+" WHERE " + COL_TEAMNAME + " = \"" + team + "\"";
  	
  	Cursor c = db.rawQuery(selectGames, null);
  	
  	if (c.moveToFirst())
  	{
  		do
  		{
	      GameDb g = new GameDb();
	      g.setId((c.getInt(c.getColumnIndex(COL_GAMEID))));
	      g.setOpponent(c.getString(c.getColumnIndex(COL_OPPONENT)));
	      oldGames.add(g);
  		}
  		while (c.moveToNext());
    }
  	return oldGames;
  }
  
  //untested
  //R
  //M
  //E
  public List<GameDb> getOneRowByIdKeys(int gID, int pID)
  {
      List<GameDb> gameRow = new ArrayList<GameDb>();
      String selectQuery = "SELECT * FROM " + TABLE_GAME + " WHERE "
              + COL_GAMEID + " = " + gID +  " AND " + COL_PLAYERID
              + " = " + pID;
   
      Log.e(LOG, selectQuery);
   
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor c = db.rawQuery(selectQuery, null);
   
      // looping through all rows and adding to list
      if (c.moveToFirst()) 
      {
          do 
          {
              GameDb g = new GameDb();
              g.setId(c.getInt((c.getColumnIndex(COL_GAMEID)))); //is this right?
              g.setTeamName((c.getString(c.getColumnIndex(COL_TEAMNAME)))); //
              g.setOpponent(c.getString(c.getColumnIndex(COL_OPPONENT))); //i think this is right
              g.setPlayerId(c.getInt((c.getColumnIndex(COL_PLAYERID))));
              g.setShots(c.getInt((c.getColumnIndex(COL_SHOTS))));
              g.setGoals(c.getInt((c.getColumnIndex(COL_GOALS))));
              g.setAssists(c.getInt((c.getColumnIndex(COL_ASSISTS))));
              g.setSteals(c.getInt((c.getColumnIndex(COL_STEALS))));
              g.setTurnovers(c.getInt((c.getColumnIndex(COL_TURNOVERS))));
              g.setSaves(c.getInt((c.getColumnIndex(COL_SAVES))));
              g.setSnitches(c.getInt((c.getColumnIndex(COL_SNITCHES))));
              g.setPlusses(c.getInt((c.getColumnIndex(COL_PLUSSES))));
              g.setMinuses(c.getInt((c.getColumnIndex(COL_MINUSES))));
              g.setOnField(c.getInt(c.getColumnIndex(COL_ONFIELD)));
              g.setTime(c.getInt((c.getColumnIndex(COL_TIME))));
              // adding to todo list
              gameRow.add(g);
          } 
          while (c.moveToNext()); //I hate do-while loops
      }
   
      return gameRow;
  }
  
  public Cursor getGameStats(int gameId)
  {
  	String query = "SELECT * FROM "
				+ TABLE_PLAYER + " p, " + TABLE_GAME + " g"
				+ " WHERE " + "g." + COL_GAMEID + " = " + gameId
				+ " AND " + "p." + COL_PLAYERID + " = "
				+ "g." + COL_PLAYERID;
  	SQLiteDatabase db = this.getReadableDatabase();
  	Cursor c = db.rawQuery(query, null);
  	return c;
  }
  
  public int getHomeScore(int gameId)
  {
  	String query = "SELECT " + COL_ASSISTS + " FROM "
  			+ TABLE_GAME + " WHERE "
  			+ COL_PLAYERID + " = -1 AND "
  			+ COL_GAMEID + " = " + gameId;
  	SQLiteDatabase db = this.getReadableDatabase();
  	Cursor c = db.rawQuery(query, null);
  	c.moveToFirst();
  	return c.getInt(0);
  }
  
  public int getAwayScore(int gameId)
  {
  	String query = "SELECT " + COL_GOALS + " FROM "
  			+ TABLE_GAME + " WHERE "
  			+ COL_PLAYERID + " = -1 AND "
  			+ COL_GAMEID + " = " + gameId;
  	SQLiteDatabase db = this.getReadableDatabase();
  	Cursor c = db.rawQuery(query, null);
  	c.moveToFirst();
  	return c.getInt(0);
  }
  
  public int getGameTime(int gameId)
  {
  	String query = "SELECT " + COL_TIME + " FROM "
  			+ TABLE_GAME + " WHERE "
  			+ COL_PLAYERID + " = -1 AND "
  			+ COL_GAMEID + " = " + gameId;
  	SQLiteDatabase db = this.getReadableDatabase();
  	Cursor c = db.rawQuery(query, null);
  	c.moveToFirst();
  	return c.getInt(0);
  }
  

  public int setHomeScore(int gameId, int toAdd)
  {
		String[] arguments = {String.valueOf(gameId), String.valueOf(-1)};
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COL_ASSISTS, toAdd);
    return db.update(TABLE_GAME, values, COL_GAMEID + " = ? AND "
				+ COL_PLAYERID + " = ?", arguments);
  }

  public int setAwayScore(int gameId, int toAdd)
  {
  	String[] arguments = {String.valueOf(gameId), String.valueOf(-1)};
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COL_GOALS, toAdd);
    return db.update(TABLE_GAME, values, COL_GAMEID + " = ? AND "
				+ COL_PLAYERID + " = ?", arguments);
    
  }
  
  public int setGameTime(int gameId, int totalTime)
  {
  	String[] arguments = {String.valueOf(gameId), String.valueOf(-1)};
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COL_TIME, totalTime);
    return db.update(TABLE_GAME, values, COL_GAMEID + " = ? AND "
				+ COL_PLAYERID + " = ?", arguments);
  }
  
  //R: nothing
  //M: nothing
  //E: returns the max game id, so I can add 1 to it and use that going forward
  public int getMaxGameRow()
  {
  	int retVal;
  	String maxQuery = "SELECT MAX(" + COL_GAMEID + ") FROM " + TABLE_GAME;
  	SQLiteDatabase db = this.getReadableDatabase();
  	Cursor c = db.rawQuery(maxQuery, null);
  	if(c.moveToFirst())
  	{
  		c.moveToFirst();
  	}
  	else
  	{
  		return 0;
  	}
  	retVal = c.getInt(0);
  	return retVal;
  }
  
  public boolean checkIfTeamExists(String teamName)
  {
  	SQLiteDatabase db = this.getReadableDatabase();
  	String query = "SELECT 1 FROM "
  			+ TABLE_PLAYER + " WHERE "
  			+ COL_TEAMNAME + " = '" + teamName + "'";
  	Cursor c = db.rawQuery(query, null);
  	if (c.moveToFirst()) //if something is there
  	{
  		return true;
  	}
  	else //nothing is there
  	{
  		return false;
  	}
  	
  }
  //untested
  //R: a valid gID and pID, a stat to update and a value to add
  //M: a row in the table
  //E: changes the value of "column" to "column" + "valToAdd"
  public int updateStat(int gID, int pID, String column, int valToAdd) 
  {
  		List<GameDb> g = getOneRowByIdKeys(gID, pID);
  		GameDb gameRow = g.get(0);
  		String[] arguments = {String.valueOf(gID), String.valueOf(pID)};
  		
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      
  		if (column.equals("shots"))
  		{
  			
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, (gameRow.getShots() + valToAdd));
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  			
  		}
  		else if (column.equals("goals"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, (gameRow.getGoals() + valToAdd));
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  		}
  		else if (column.equals("assists"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, (gameRow.getAssists() + valToAdd));
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  		}
  		else if (column.equals("steals"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, (gameRow.getSteals() + valToAdd));
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  		}
  		else if (column.equals("turnovers"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, (gameRow.getTurnovers() + valToAdd));
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  		}
  		else if (column.equals("saves"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, (gameRow.getSaves() + valToAdd));
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  		}
  		else if (column.equals("snitches"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, (gameRow.getSnitches() + valToAdd));
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  		}
  		else if (column.equals("plusses"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, (gameRow.getPlusses() + valToAdd));
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  		}
  		else if (column.equals("minuses"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, (gameRow.getMinuses() + valToAdd));
  			values.put(COL_ONFIELD, gameRow.getOnField());
  		}
  		else if (column.equals("onField"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, valToAdd);
  		}
  		else if (column.equals("time"))
  		{
  			values.put(COL_GAMEID, gameRow.getGameId());
  			values.put(COL_TEAMNAME, gameRow.getTeamName());
  			values.put(COL_OPPONENT, gameRow.getOpponent());
  			values.put(COL_PLAYERID, gameRow.getPlayerId());
  			values.put(COL_SHOTS, gameRow.getShots());
  			values.put(COL_GOALS, gameRow.getGoals());
  			values.put(COL_ASSISTS, gameRow.getAssists());
  			values.put(COL_STEALS, gameRow.getSteals());
  			values.put(COL_TURNOVERS, gameRow.getTurnovers());
  			values.put(COL_SAVES, gameRow.getSaves());
  			values.put(COL_SNITCHES, gameRow.getSnitches());
  			values.put(COL_PLUSSES, gameRow.getPlusses());
  			values.put(COL_MINUSES, gameRow.getMinuses());
  			values.put(COL_ONFIELD, gameRow.getOnField());
  			values.put(COL_TIME, gameRow.getTime() + valToAdd);
  			//values.put(COL_TIME, valToAdd);
  		}
  		else
  		{
  			Log.i(LOG, "couldn't find column in update: " + column);
  		}
  		
      // updating row
      return db.update(TABLE_GAME, values, COL_GAMEID + " = ? AND "
      				+ COL_PLAYERID + " = ?", arguments);
      //should hopefully return 1
  }
  
  //untested
  //R: a valid gameID and playerID
  //M: the Game Table in the database
  //E: deletes a single row from the database,
  //	 returns how many rows are deleted (should be 1)
  public void deleteGameRow(int gID, int pID) 
  {
  		String[] arguments = {String.valueOf(gID), String.valueOf(pID)};
      SQLiteDatabase db = this.getWritableDatabase();
      db.delete(TABLE_GAME, COL_GAMEID + " = ? AND "
  				+ COL_PLAYERID + " = ?", arguments);
  }
  
  //tested
  //R: valid values for each category
  //M: the players database
  //E: adds a new player to the db with a unique pID
  public long addPlayer(PlayerDb p)
  {
  	SQLiteDatabase db = this.getWritableDatabase();
  	
  	SQLiteDatabase readDb = this.getReadableDatabase();
  	String[] selectionArgs = {COL_PLAYERID, TABLE_PLAYER};
  	
  	ContentValues values = new ContentValues();

    values.put(COL_TEAMNAME, p.getTeamName());
    values.put(COL_NUMBER, p.getNumber());
    values.put(COL_FNAME, p.getFname());
    values.put(COL_LNAME, p.getLname());
    values.put(COL_ACTIVE, p.getActive());
    
    // insert row
    Log.i("Test", "I bet it breaks here");
    long player_insert_id = db.insert(TABLE_PLAYER, null, values);

    return player_insert_id; //I didn't know we got a return id from this
  	
  }
  
  //untested
  //R
  //M
  //E
  public PlayerDb getOnePlayerRow(String teamName, int pID)
  {
      PlayerDb playerRow = new PlayerDb();
      String selectQuery = "SELECT  * FROM " + TABLE_PLAYER + " WHERE "
              + COL_TEAMNAME + " = \"" + teamName +  "\" AND " + COL_PLAYERID
              + " = " + pID;
   
      Log.e(LOG, selectQuery);
   
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor c = db.rawQuery(selectQuery, null);
   
      // looping through all rows and adding to list
      if (c.moveToFirst()) 
      {
          do 
          {
              PlayerDb p = new PlayerDb(); //just a db row
              p.setTeamName((c.getString(c.getColumnIndex(COL_TEAMNAME)))); //
              p.setPlayerId(c.getInt((c.getColumnIndex(COL_PLAYERID))));
              p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
              p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
              p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
              p.setActive(c.getInt((c.getColumnIndex(COL_ACTIVE))));
              //adding to the list
              playerRow = p;
          } 
          while (c.moveToNext()); //I hate do-while loops
      }
   
      return playerRow;
  }
  
  //untested
  //R: a valid team name, and a variable that determines if active matters
  //M: nothing
  //E: returns all the players on a team (or just the active ones, if the variable is set)
  //public List<PlayerDb> was the old return type
  public List<PlayerDb> getAllPlayers(String tN, int activeFlag)
  {
  	List<PlayerDb> playerList = new ArrayList<PlayerDb>();
  	String selectWhichPlayers;
  	if (activeFlag == 0)
  	{
  		selectWhichPlayers = "SELECT * FROM " + TABLE_PLAYER + " WHERE "
    			+ COL_TEAMNAME + " = " + "\"" + tN + "\"";
  	}
  	else
  	{
  		selectWhichPlayers = "SELECT * FROM " + TABLE_PLAYER + " WHERE "
    			+ COL_TEAMNAME + " = " + "\"" + tN + "\"" + " AND " + COL_ACTIVE + " = 1";
  	}
  	
  	Log.i(LOG, selectWhichPlayers);
  	SQLiteDatabase db = this.getReadableDatabase();
  	Cursor c = db.rawQuery(selectWhichPlayers, null);
  	//HERE
  	if (c.moveToFirst())
  	{
  		do
  		{
	      PlayerDb p = new PlayerDb(); //just a db row
	      p.setTeamName((c.getString(c.getColumnIndex(COL_TEAMNAME)))); //
	      p.setPlayerId(c.getInt((c.getColumnIndex(COL_PLAYERID))));
	      p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
	      p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
	      p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
	      p.setActive(c.getInt((c.getColumnIndex(COL_ACTIVE))));
	      playerList.add(p);
  		}
  		while (c.moveToNext());
    }
  	return playerList;
  }
  public int updatePlayerInfo(PlayerDb p)
  {
  	SQLiteDatabase db = this.getWritableDatabase();
  	
  	ContentValues values = new ContentValues();
  	
    values.put(COL_TEAMNAME, p.getTeamName());
    values.put(COL_PLAYERID, p.getPlayerId() );
    values.put(COL_NUMBER, p.getNumber());
    values.put(COL_FNAME, p.getFname());
    values.put(COL_LNAME, p.getLname());
    values.put(COL_ACTIVE, p.getActive());
    
    return db.update(TABLE_PLAYER, values, COL_PLAYERID + " = ?",
    		new String[] { String.valueOf(p.getPlayerId()) });
  }
  
  //untested
  //R: nothing
  //M: nothing
  //E: returns all of the current team names
  public List<String> getCurrentTeams()
  {
  	List<String> ret = new ArrayList<String>();
  	SQLiteDatabase db = this.getReadableDatabase();
  	String teamsQuery = "SELECT DISTINCT " + COL_TEAMNAME + " FROM " + TABLE_PLAYER;
  	Cursor c = db.rawQuery(teamsQuery, null);
  	if(c.moveToFirst())
  	{
  		do
  		{
  			if (c.getString(c.getColumnIndex(COL_TEAMNAME)) != null)
  			{
  				ret.add(c.getString(c.getColumnIndex(COL_TEAMNAME)));
  			}
  		}
  		while (c.moveToNext() );
  	}
  	
  	
  	return ret;
  }
  
	public List<PlayerDb> getOnFieldPlayers(String teamName) 
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		SQLiteDatabase db = this.getReadableDatabase();
		String onFieldPlayersQuery = "SELECT * FROM " + TABLE_PLAYER + " WHERE "
				+ COL_TEAMNAME + " = \"" + teamName + "\" AND " + COL_ONFIELD + " != 0";
		
		Cursor c = db.rawQuery(onFieldPlayersQuery, null);
  	if (c.moveToFirst())
  	{
  		do
  		{
	      PlayerDb p = new PlayerDb(); //just a db row
	      p.setTeamName((c.getString(c.getColumnIndex(COL_TEAMNAME)))); //
	      p.setPlayerId(c.getInt((c.getColumnIndex(COL_PLAYERID))));
	      p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
	      p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
	      p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
	      p.setActive(c.getInt((c.getColumnIndex(COL_ACTIVE))));
	      playerList.add(p);
  		}
  		while (c.moveToNext());
    }
  	return playerList;
	}
	
	//this doesn't work anymore, because onField comes from the game
	public List<PlayerDb> getOnFieldPlayersFromGame(String teamName, int gID) 
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String onFieldPlayersQuery = "SELECT * FROM "
				+ TABLE_PLAYER + " p, " + TABLE_GAME + " g"
				+ " WHERE " + "g." + COL_GAMEID + " = " + gID
				+ " AND " + "g." + COL_ONFIELD + " != 0"
				+ " AND " + "g." + COL_PLAYERID + " = "
				+ "p." + COL_PLAYERID 
				+ " ORDER BY " + COL_ONFIELD + " ASC";
				
		Cursor c = db.rawQuery(onFieldPlayersQuery, null);
  	if (c.moveToFirst())
  	{
  		do
  		{
	      PlayerDb p = new PlayerDb(); //just a db row
	      p.setTeamName((c.getString(c.getColumnIndex(COL_TEAMNAME)))); //
	      p.setPlayerId(c.getInt((c.getColumnIndex(COL_PLAYERID))));
	      p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
	      p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
	      p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
	      p.setActive(c.getInt((c.getColumnIndex(COL_ACTIVE))));
	      playerList.add(p);
  		}
  		while (c.moveToNext());
    }
  	return playerList;
	}
	
	public List<PlayerDb> getOffFieldPlayersFromGame(String teamName, int gID) 
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String onFieldPlayersQuery = "SELECT * FROM "
				+ TABLE_PLAYER + " p, " + TABLE_GAME + " g"
				+ " WHERE " + "g." + COL_GAMEID + " = " + gID
				+ " AND " + "g." + COL_ONFIELD + " = 0"
				+ " AND " + "p." + COL_PLAYERID + " = "
				+ "g." + COL_PLAYERID;
		
		Cursor c = db.rawQuery(onFieldPlayersQuery, null);
  	if (c.moveToFirst())
  	{
  		do
  		{
	      PlayerDb p = new PlayerDb(); //just a db row
	      p.setTeamName((c.getString(c.getColumnIndex(COL_TEAMNAME)))); //
	      p.setPlayerId(c.getInt((c.getColumnIndex(COL_PLAYERID))));
	      p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
	      p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
	      p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
	      p.setActive(c.getInt((c.getColumnIndex(COL_ACTIVE))));
	      playerList.add(p);
  		}
  		while (c.moveToNext());
    }
  	return playerList;
	}
	
	public List<PlayerDb> getAllPlayersFromGame(String teamName, int gID) 
	{
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String onFieldPlayersQuery = "SELECT * FROM "
				+ TABLE_PLAYER + " p, " + TABLE_GAME + " g"
				+ " WHERE " + "g." + COL_GAMEID + " = " + gID
				+ " AND " + "p." + COL_PLAYERID + " = "
				+ "g." + COL_PLAYERID;
		
		Cursor c = db.rawQuery(onFieldPlayersQuery, null);
  	if (c.moveToFirst())
  	{
  		do
  		{
	      PlayerDb p = new PlayerDb(); //just a db row
	      p.setTeamName((c.getString(c.getColumnIndex(COL_TEAMNAME)))); //
	      p.setPlayerId(c.getInt((c.getColumnIndex(COL_PLAYERID))));
	      p.setNumber(c.getString((c.getColumnIndex(COL_NUMBER))));
	      p.setFname(c.getString((c.getColumnIndex(COL_FNAME))));
	      p.setLname(c.getString((c.getColumnIndex(COL_LNAME))));
	      p.setActive(c.getInt((c.getColumnIndex(COL_ACTIVE))));
	      playerList.add(p);
  		}
  		while (c.moveToNext());
    }
  	return playerList;
	}
	
	
	public int onFieldPlayers(String tN)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String countQuery = "SELECT * FROM " + TABLE_PLAYER + " WHERE " + 
				COL_TEAMNAME + " = \"" + tN + "\" AND " + COL_ONFIELD + " != 0";
		Cursor c = db.rawQuery(countQuery, null);
		return c.getCount();
	}
  
  //untested
  //R: a valid teamName and playerID
  //M: the Game Table in the database
  //E: deletes a single row from the player database
  public void deletePlayerRow(String tN, int pID) 
  {
  		String[] arguments = {tN, String.valueOf(pID)};
      SQLiteDatabase db = this.getWritableDatabase();
      db.delete(TABLE_PLAYER, COL_TEAMNAME + " = ? AND "
  				+ COL_PLAYERID + " = ?", arguments);
  }
  
  //closing database
  public void closeDB() 
  {
      SQLiteDatabase db = this.getReadableDatabase();
      if (db != null && db.isOpen())
          db.close();
  }

  
}
