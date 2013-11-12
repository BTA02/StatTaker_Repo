package local.stattaker;

import java.util.HashMap;

public class Player 
{
	String fname;
	String lname;
	String number;
	
	HashMap stats; //key = name, value = number
	
	public Player(String f, String l, String n)
	{
		this.fname = f;
		this.lname = l;
		this.number = n;
		/*
		this.stats.put("goals", 0);
		this.stats.put("assists", 0);
		this.stats.put("shots", 0);
		this.stats.put("turnovers", 0);
		this.stats.put("steal", 0);
		this.stats.put("snitches", 0);
		this.stats.put("saves", 0);
		this.stats.put("pluses", 0);
		this.stats.put("minuses", 0);
		*/
	}
}
