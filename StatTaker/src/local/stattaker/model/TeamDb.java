package local.stattaker.model;

import java.util.Comparator;

public class TeamDb
{
	private String id;
	private String name;
	
	public TeamDb()
	{
		this.id = "";
		this.name = "";
	}
	
	public TeamDb(String id_, String name_)
	{
		this.id = id_;
		this.name = name_;
	}
	
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	public static class OrderByTeamName implements Comparator<TeamDb>
	{

		@Override
		public int compare(TeamDb lhs, TeamDb rhs) 
		{
			int res = String.CASE_INSENSITIVE_ORDER.compare(lhs.name, rhs.name);
			if (res == 0) 
			{
				res = lhs.name.compareTo(rhs.name);
			}
			return res;
		}

	}
	
}
