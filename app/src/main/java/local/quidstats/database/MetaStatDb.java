package local.quidstats.database;

import java.util.List;

public class MetaStatDb
{
	private int timeOfAction;
	private String statType;
	private String playerId;
	private String playerSubbedIn;
	
	public int getTimeOfAction()
	{
		return timeOfAction;
	}
	public void setTimeOfAction(int timeOfAction)
	{
		this.timeOfAction = timeOfAction;
	}
	public String getStatType()
	{
		return statType;
	}
	public void setStatType(String statType)
	{
		this.statType = statType;
	}
	public String getPlayerId()
	{
		return playerId;
	}
	public void setPlayerId(String playerId)
	{
		this.playerId = playerId;
	}
	public String getPlayerSubbedIn()
	{
		return playerSubbedIn;
	}
	public void setPlayerSubbedIn(String playerSubbedIn)
	{
		this.playerSubbedIn = playerSubbedIn;
	}

	
	

}
