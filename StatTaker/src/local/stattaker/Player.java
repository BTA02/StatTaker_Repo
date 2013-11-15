package local.stattaker;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Player implements Serializable
{
	String fname;
	String lname;
	String number;
	
	Map<String, Integer> stats = new HashMap<String, Integer>(); //key = name, value = number
	
	public Player(String f, String l, String n)
	{
		this.fname = f;
		this.lname = l;
		this.number = n;
		
		this.stats.put("goals", Integer.valueOf(0) );
		this.stats.put("assists", Integer.valueOf(0));
		this.stats.put("shots", Integer.valueOf(0));
		this.stats.put("turnovers", Integer.valueOf(0));
		this.stats.put("steals", Integer.valueOf(0));
		this.stats.put("snitches", Integer.valueOf(0));
		this.stats.put("saves", Integer.valueOf(0));
		this.stats.put("pluses", Integer.valueOf(0));
		this.stats.put("minuses", Integer.valueOf(0));
		
	}
}
