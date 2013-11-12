package local.stattaker;

public class Game 
{
	Team homeTeam;
	Team awayTeam;
	int homeTeamScore;
	int awayTeamScore;
	int gameLengthSeconds;
	
	
	public Game(Team hT, Team aT)
	{
		this.homeTeam = hT;
		this.awayTeam = aT;
	}
	
}
