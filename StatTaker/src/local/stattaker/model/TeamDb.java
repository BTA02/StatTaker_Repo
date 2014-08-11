package local.stattaker.model;

public class TeamDb
{
	private String id;
	private String name;
	
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
	
}
