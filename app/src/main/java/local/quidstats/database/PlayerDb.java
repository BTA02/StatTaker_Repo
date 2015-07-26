package local.quidstats.database;

import java.io.Serializable;
import java.util.Comparator;


public class PlayerDb
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
	public PlayerDb(String teamName_, String playerId_, String number_, 
			String fname_, String lname_, int onField_, int active_)
	{
		this.playerId = playerId_;
		this.number = number_;
		this.fname = fname_;
		this.lname = lname_;
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
		return this.number + " " + this.fname + " " + this.lname;
	}

    @Override
    public boolean equals(Object o) {
        return (playerId.equals(((PlayerDb)o).getPlayerId()));
    }
}