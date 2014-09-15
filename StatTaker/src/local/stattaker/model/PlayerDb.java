package local.stattaker.model;

import java.util.Comparator;


public class PlayerDb //row in the player table
{
	String playerId;
	String number;
	String fname;
	String lname;

	// Empty constructor
	public PlayerDb()
	{

	}

	// constructor
	public PlayerDb(String t, String p, String n, String f, String l, int oF, int a)
	{
		this.playerId = p;
		this.number = n;
		this.fname = f;
		this.lname = l;

	}

	public static class OrderByFirstName implements Comparator<PlayerDb>
	{

		@Override
		public int compare(PlayerDb lhs, PlayerDb rhs) 
		{
			int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.fname, rhs.fname);
			if (res == 0) 
			{
				res = lhs.fname.compareTo(rhs.fname);
			}
			return res;
		}

	}
	
	public static class OrderByLastName implements Comparator<PlayerDb>
	{

		@Override
		public int compare(PlayerDb lhs, PlayerDb rhs)
		{
			int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.lname, rhs.lname);
			if (res == 0)
			{
				res = lhs.lname.compareTo(rhs.lname);
			}
			return res;
		}
		
	}
	
	// gets
	public String getPlayerId()
	{
		return this.playerId;
	}

	public String getNumber()
	{
		return this.number;
	}

	public String getFname()
	{
		return this.fname;
	}

	public String getLname()
	{
		return this.lname;
	}

	//sets
	public void setPlayerId(String i)
	{
		this.playerId = i;
	}

	public void setNumber(String str)
	{
		this.number = str;
	}

	public void setFname(String str)
	{
		this.fname = str;
	}

	public void setLname(String str)
	{
		this.lname = str;
	}

	@Override
	public String toString()
	{
		return this.number + "\t\t" + this.fname + "\t\t" + this.lname + "\t\t\t";
	}

}