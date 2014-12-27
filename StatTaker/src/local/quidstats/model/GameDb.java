package local.quidstats.model;


public class GameDb //row data for a game table
{

	//private variables
	private String gameId; 
	private String homeTeam; 
	private String awayTeam;
	private int gameTimeSeconds;
	private int homeScore;
	private int awayScore;
	

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




}