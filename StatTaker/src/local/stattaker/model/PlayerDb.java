package local.stattaker.model;

 
public class PlayerDb //row in the player table
{
     
  String teamName;
  int playerId;
  String number;
  String fname;
  String lname;
  int active; //0 is not active, obviously
  
   
  // Empty constructor
  public PlayerDb()
  {
       
  }
  
  // constructor
  public PlayerDb(String t, int p, String n, String f, String l, int a)
  {
    this.teamName = t;
    this.playerId = p;
    this.number = n;
    this.fname = f;
    this.lname = l;
    this.active = a;
    
  }
  
  // gets
  public String getTeamName()
  {
    return this.teamName;
  }
  
  public int getPlayerId()
  {
  	return this.playerId;
  }
  
  public String getNumber()
  {
  	return this.number;
  }
  
  public String getFname()
  {
  	return this.fname;
  }
  
  public String getLname()
  {
  	return this.lname;
  }
  
  public int getActive()
  {
  	return this.active;
  }
  
  //sets
  public void setTeamName(String str)
  {
    this.teamName = str;
  }
  
  public void setPlayerId(int i)
  {
  	this.playerId = i;
  }
  
  public void setNumber(String str)
  {
  	this.number = str;
  }
  
  public void setFname(String str)
  {
  	this.fname = str;
  }
  
  public void setLname(String str)
  {
  	this.lname = str;
  }
  
  public void setActive(int i)
  {
  	this.active = i;
  }
  
}