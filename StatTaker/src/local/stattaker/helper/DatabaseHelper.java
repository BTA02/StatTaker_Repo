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
  // Logcat tag
  private static final String LOG = "DatabaseHelper";

  // Database Version
  private static final int DATABASE_VERSION = 1;

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

  // PLAYER Table - column names
  private static final String COL_NUMBER = "number";
  private static final String COL_FNAME = "fname";
  private static final String COL_LNAME = "lname";
  private static final String COL_ACTIVE = "active";

  // Table Create Statements
  // GAME table create statement
  private static final String CREATE_TABLE_GAME = "CREATE TABLE "
          + TABLE_GAME + "(" + COL_GAMEID + " INTEGER PRIMARY KEY," + COL_TEAMNAME
          + " TEXT," + COL_OPPONENT + " TEXT," + COL_PLAYERID + " INTEGER," + COL_SHOTS
          + " INTEGER," + COL_GOALS + " INTEGER," + COL_GOALS
          + " INTEGER," + COL_ASSISTS + " INTEGER," + COL_STEALS
          + " INTEGER," + COL_TURNOVERS +  " INTEGER," + COL_SAVES
          + " INTEGER," + COL_SNITCHES + " INTEGER," + COL_PLUSSES
          + " INTEGER," + COL_MINUSES + " INTEGER" + ")";

  // PLAYER table create statement
  private static final String CREATE_TABLE_PLAYER = "CREATE TABLE " + TABLE_PLAYER
          + "(" + COL_TEAMNAME + " TEXT," + COL_PLAYERID 
          + " INTEGER PRIMARY KEY," + COL_NUMBER + " TEXT,"
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
      //these should start at 0 for everything
   
      // insert row
      long game_id = db.insert(TABLE_GAME, null, values);
   
      return game_id; //I didn't know we got a return id from this
  }
  
  //untested
  //R
  //M
  //E
  public List<GameDb> getOneRowByIdKeys(int gID, int pID)
  {
      List<GameDb> gameRow = new ArrayList<GameDb>();
      String selectQuery = "SELECT  * FROM " + TABLE_GAME + " WHERE "
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
              // adding to todo list
              gameRow.add(g);
          } 
          while (c.moveToNext()); //I hate do-while loops
      }
   
      return gameRow;
  }
  
  //untested
  //R: a valid gID and pID
  //M: a row in the table
  //E: changes the value of "colum" to "column" + "valToAdd"
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
  
  //untested
  //R: valid values for each category
  //M: the players database
  //E: adds a new player to the db with a unique pID
  public long addPlayer(PlayerDb p)
  {
  	SQLiteDatabase db = this.getWritableDatabase();
  	
  	SQLiteDatabase readDb = this.getReadableDatabase();
  	
  	Cursor c = readDb.query(TABLE_PLAYER, null, null, null, null, null, COL_PLAYERID, "LIMIT 1");
  	int maxPID = c.getInt(0);
  	
  	ContentValues values = new ContentValues();

    values.put(COL_TEAMNAME, p.getTeamName());
    values.put(COL_PLAYERID, (maxPID + 1) );
    values.put(COL_NUMBER, p.getNumber());
    values.put(COL_FNAME, p.getFname());
    values.put(COL_LNAME, p.getLname());
    values.put(COL_ACTIVE, p.getActive());
    
    // insert row
    long player_insert_id = db.insert(TABLE_PLAYER, null, values);
 
    return player_insert_id; //I didn't know we got a return id from this
  	
  }
  
  //untested
  //R
  //M
  //E
  public List<PlayerDb> getOnePlayerRow(String teamName, int pID)
  {
      List<PlayerDb> playerRow = new ArrayList<PlayerDb>();
      String selectQuery = "SELECT  * FROM " + TABLE_PLAYER + " WHERE "
              + COL_TEAMNAME + " = " + teamName +  " AND " + COL_PLAYERID
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
              playerRow.add(p);
          } 
          while (c.moveToNext()); //I hate do-while loops
      }
   
      return playerRow;
  }
  
  public int updatePlayerInfo(PlayerDb p, String column, String strVal, int intVal ) 
  {
  		List<PlayerDb> pDb = getOnePlayerRow(p.getTeamName(), p.getPlayerId() );
  		PlayerDb playerRow = pDb.get(0);
  		String[] arguments = {p.getTeamName(), String.valueOf(p.getPlayerId())};
  		
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      
  		if (column.equals("teamName"))
  		{
  			values.put(COL_TEAMNAME, strVal);
  			values.put(COL_PLAYERID, playerRow.getPlayerId());
  			values.put(COL_NUMBER, playerRow.getNumber() );
  			values.put(COL_FNAME, playerRow.getFname());
  			values.put(COL_LNAME, playerRow.getLname());
  			values.put(COL_ACTIVE, playerRow.getActive());
  		}
  		else if (column.equals("number"))
  		{
  			values.put(COL_TEAMNAME, playerRow.getTeamName());
  			values.put(COL_PLAYERID, playerRow.getPlayerId());
  			values.put(COL_NUMBER, strVal );
  			values.put(COL_FNAME, playerRow.getFname());
  			values.put(COL_LNAME, playerRow.getLname());
  			values.put(COL_ACTIVE, playerRow.getActive());
  		}
  		else if (column.equals("fname"))
  		{
  			values.put(COL_TEAMNAME, playerRow.getTeamName());
  			values.put(COL_PLAYERID, playerRow.getPlayerId());
  			values.put(COL_NUMBER, playerRow.getNumber() );
  			values.put(COL_FNAME, strVal);
  			values.put(COL_LNAME, playerRow.getLname());
  			values.put(COL_ACTIVE, playerRow.getActive());
  		}
  		else if (column.equals("lname"))
  		{
  			values.put(COL_TEAMNAME, playerRow.getTeamName());
  			values.put(COL_PLAYERID, playerRow.getPlayerId());
  			values.put(COL_NUMBER, playerRow.getNumber() );
  			values.put(COL_FNAME, playerRow.getFname());
  			values.put(COL_LNAME, strVal);
  			values.put(COL_ACTIVE, playerRow.getActive());
  		}
  		else if (column.equals("active"))
  		{
  			values.put(COL_TEAMNAME, playerRow.getTeamName());
  			values.put(COL_PLAYERID, playerRow.getPlayerId());
  			values.put(COL_NUMBER, playerRow.getNumber() );
  			values.put(COL_FNAME, playerRow.getFname());
  			values.put(COL_LNAME, playerRow.getLname());
  			values.put(COL_ACTIVE, intVal);
  		}
  		else
  		{
  			Log.i(LOG, "couldn't find column in update 2: " + column);
  		}
  		
      // updating row
      return db.update(TABLE_PLAYER, values, COL_TEAMNAME + " = ? AND "
      				+ COL_PLAYERID + " = ?", arguments);
      //should hopefully return 1
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