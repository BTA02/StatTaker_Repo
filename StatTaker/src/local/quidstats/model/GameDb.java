package local.quidstats.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.Map;


public class GameDb //row data for a game table
{

	//private variables
	private String gameId; 
	private String homeTeam; 
	private String awayTeam;
	private int gameTimeSeconds;
	private int homeScore;
	private int awayScore;
	private Map<Integer, List<PlayerDb> > timeMap;


	public String getId()
	{
		return this.gameId;
	}

	public void setId(String string)
	{
		this.gameId = string;
	}

	public String getHomeTeam()
	{
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam)
	{
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam()
	{
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam)
	{
		this.awayTeam = awayTeam;
	}

	public int getGameTimeSeconds()
	{
		return gameTimeSeconds;
	}

	public void setGameTimeSeconds(int gameTimeSeconds)
	{
		this.gameTimeSeconds = gameTimeSeconds;
	}

	public int getHomeScore()
	{
		return homeScore;
	}

	public void setHomeScore(int homeScore)
	{
		this.homeScore = homeScore;
	}

	public int getAwayScore()
	{
		return awayScore;
	}

	public void setAwayScore(int awayScore)
	{
		this.awayScore = awayScore;
	}

	@Override
	public String toString()
	{
		return awayTeam;
	}

	public Map<Integer, List<PlayerDb> > getTimeMap()
	{
		return timeMap;
	}

	public byte[] getTimeMapBytes()
	{
		return timeMapToBytes(timeMap);
	}

	public void setTimeMap(Map<Integer, List<PlayerDb> > timeMap)
	{
		this.timeMap = timeMap;
	}

	public void setTimeMap(byte[] timeMap_)
	{
		if (timeMap_ == null)
		{
			return;
		}
		ByteArrayInputStream byteIn = new ByteArrayInputStream(timeMap_);
		ObjectInputStream in;
		try
		{
			in = new ObjectInputStream(byteIn);
			timeMap = (Map<Integer, List<PlayerDb>>) in.readObject();
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public static byte[] timeMapToBytes(Map<Integer, List<PlayerDb> > timeMap_)
	{
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try
		{
			out = new ObjectOutputStream(byteOut);
			out.writeObject(timeMap_);
			return byteOut.toByteArray();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}





