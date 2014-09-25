package local.stattaker.util;

public class Action 
{
	private String gameId;
	private String playerId;
	private int valueAdded;
	private String playerSubbedOut;
	private int timeSwitched;
	private String databaseColumn;
	
	public String getGameId()
	{
		return gameId;
	}
	public void setGameId(String gameId)
	{
		this.gameId = gameId;
	}
	public String getPlayerId()
	{
		return playerId;
	}
	public void setPlayerId(String playerId)
	{
		this.playerId = playerId;
	}
	public int getValueAdded()
	{
		return valueAdded;
	}
	public void setValueAdded(int valueAdded)
	{
		this.valueAdded = valueAdded;
	}
	public String getPlayerSubbedOut()
	{
		return playerSubbedOut;
	}
	public void setPlayerSubbedOut(String playerSubbedOut)
	{
		this.playerSubbedOut = playerSubbedOut;
	}
	public int getTimeSwitched()
	{
		return timeSwitched;
	}
	public void setTimeSwitched(int timeSwitched)
	{
		this.timeSwitched = timeSwitched;
	}
	public String getDatabaseColumn()
	{
		return databaseColumn;
	}
	public void setDatabaseColumn(String databaseColumn)
	{
		this.databaseColumn = databaseColumn;
	}
	
}
