package local.quidstats.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private SparseArray<List<String> > timeArray;


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

	public SparseArray<List<String> > getTimeArray()
	{
		return timeArray;
	}

	public byte[] getTimeMapBytes()
	{
		return timeArrayToBytes(timeArray);
	}

	public void setTimeMap(SparseArray<List<String> > timeMap)
	{
		this.timeArray = timeMap;
	}

	public void setTimeArray(byte[] timeMap_)
	{
		if (timeMap_ == null)
		{
			return;
		}
		SparseArray<List<String>> ret = new SparseArray<List<String> >();
		try
		{
			JSONArray outerArray = new JSONArray(new String(timeMap_));
			for (int i = 0; i < outerArray.length(); i++)
			{
				List<String> list = new ArrayList<String>();
				JSONArray innerArray = (JSONArray) outerArray.get(i);
				for (int j = 0; j < innerArray.length(); j++)
				{
					JSONObject obj = (JSONObject) innerArray.get(j);
					list.add(obj.getString("_id"));
				}
				ret.put(i, list);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		timeArray = ret;
	}

	public static byte[] timeArrayToBytes(SparseArray<List<String> > timeMap_)
	{
		if (timeMap_ == null)
		{
			return null;
		}
		JSONArray outerArray = new JSONArray();
		for(int i = 0; i < timeMap_.size(); i++) 
		{
			JSONArray innerArray = new JSONArray();
			int key = timeMap_.keyAt(i);
			List<String> list = (List<String>) timeMap_.get(key);
			for (String p : list)
			{
				JSONObject json = new JSONObject();
				try
				{
					json.put("_id", p);
					innerArray.put(json);
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				outerArray.put(i, innerArray);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		return outerArray.toString().getBytes();
		
	}
}





