package local.quidstats.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import android.util.SparseArray;


public class GameDb //row data for a game table
{

	//private variables
	private String gameId; 
	private String homeTeam; 
	private String awayTeam;
	private int gameTimeSeconds;
	private int homeScore;
	private int awayScore;
	private SparseArray<List<PlayerDb> > timeArray;


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

	public SparseArray<List<PlayerDb> > getTimeMap()
	{
		return timeArray;
	}

	public byte[] getTimeMapBytes()
	{
		return timeArrayToBytes(timeArray);
	}

	public void setTimeMap(SparseArray<List<PlayerDb> > timeMap)
	{
		this.timeArray = timeMap;
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
			timeArray = (SparseArray<List<PlayerDb>>) in.readObject();
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public static byte[] timeArrayToBytes(SparseArray<List<PlayerDb> > timeMap_)
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





