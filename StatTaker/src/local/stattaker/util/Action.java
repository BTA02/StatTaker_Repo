package local.stattaker.util;

public class Action 
{
	private int gId;
	private int pId;
	private int pIdOut;
	private String name;
	private String category;
	private int valAdded;

	
	
	public Action(int g, int p, int pOut, String c, String n, int v)
	{
		this.setgId(g);
		this.setpId(p);
		this.setpIdOut(pOut);
		this.setCategory(c);
		this.setName(n);
		this.valAdded = v;
	}

	public int getpId() 
	{
		return pId;
	}

	public void setpId(int pIdOut) 
	{
		this.pId = pIdOut;
	}
	
	public int getpIdOut() 
	{
		return pIdOut;
	}

	public void setpIdOut(int pIdOut) 
	{
		this.pIdOut = pIdOut;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getgId() {
		return gId;
	}

	public void setgId(int gId) {
		this.gId = gId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
