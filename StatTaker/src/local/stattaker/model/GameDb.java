package local.stattaker.model;

 
public class GameDb //row data for a game table
{
     
  //private variables
  int gameId; 
  String teamName; 
  String opponent;
  int playerId;
  int shots;
  int goals;
  int assists;
  int steals;
  int turnovers;
  int saves;
  int snitches;
  int plusses;
  int minuses;
  
   
  // Empty constructor
  public GameDb()
  {
       
  }
  
  // constructor
  public GameDb(int gid, String t, String o, int pID)
  {
    this.gameId = gid;
    this.teamName = t;
    this.opponent = o;
    this.playerId = pID;
    this.shots = 0;
    this.goals = 0;
    this.assists = 0;
    this.steals = 0;
    this.turnovers = 0;
    this.saves = 0;
    this.snitches = 0;
    this.plusses = 0;
    this.minuses = 0;
  }
  
  // gets
  public int getGameId()
  {
    return this.gameId;
  }
  
  public String getTeamName()
  {
  	return this.teamName;
  }
  
  public String getOpponent()
  {
  	return this.opponent;
  }
  
  public int getPlayerId()
  {
  	return this.playerId;
  }
  
  public int getShots()
  {
  	return this.shots;
  }
  
  public int getGoals()
  {
  	return this.goals;
  }
  
  public int getAssists()
  {
  	return this.assists;
  }
  
  public int getSteals()
  {
  	return this.steals;
  }
  
  public int getTurnovers()
  {
  	return this.turnovers;
  }
  
  public int getSaves()
  {
  	return this.saves;
  }
  
  public int getSnitches()
  {
  	return this.snitches;
  }
  
  public int getPlusses()
  {
  	return this.plusses;
  }
  
  public int getMinuses()
  {
  	return this.minuses;
  }
  
  //sets
  public void setId(int i)
  {
    this.gameId = i;
  }
  
  public void setTeamName(String str)
  {
  	this.teamName = str;
  }
  
  public void setOpponent(String str)
  {
  	this.opponent = str;
  }
  
  public void setPlayerId(int i)
  {
  	this.playerId = i;
  }
  
  public void setShots(int i)
  {
  	this.shots = i;
  }
  
  public void setGoals(int i)
  {
  	this.goals = i;
  }
  
  public void setAssists(int i)
  {
  	this.assists = i;
  }
  
  public void setSteals(int i)
  {
  	this.steals = i;
  }
  
  public void setTurnovers(int i)
  {
  	this.turnovers = i;
  }
  
  public void setSaves(int i)
  {
  	this.saves = i;
  }
  
  public void setSnitches(int i)
  {
  	this.snitches = i;
  }
  
  public void setPlusses(int i)
  {
  	this.plusses = i;
  }
  
  public void setMinuses(int i)
  {
  	this.minuses = i;
  }
   
}