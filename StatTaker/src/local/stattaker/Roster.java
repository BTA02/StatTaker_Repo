package local.stattaker;

import java.util.Vector;

public class Roster
{
	String teamName;
	Vector players = new Vector();
	
	public Roster(String str) 
	{
		this.teamName = str;
	}

	void addPlayer(Player a)
	{
		this.players.add(a);
	}
	
}

