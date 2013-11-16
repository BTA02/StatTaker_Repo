package local.stattaker;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Team implements Serializable
{

	String tName;
	
	Map<String, Player> players = new HashMap<String, Player>();
	
	
	public Team(Context myContext, String teamName) 
	{
		tName = teamName;
		//load the entire roster here
		char c = 'a'; //init, won't matter later
		String str = "";
		String fname = "";
		String lname = "";
		String number = "";
		int looper = 0;
		String fileName = teamName + "_Roster";
		AssetManager am = myContext.getAssets();
		try 
		{
			InputStream is = am.open(fileName);
			c = (char) is.read();
			while (c != '~') //while file is good, get it one character at a time
			{
				while (c != ' ' && c != '\n') //word by word
				{
					str = str + c;
					c = (char) is.read();
				}
				if (looper % 3 == 0)
				{
					fname = str;
				}
				else if (looper % 3 == 1)
				{
					lname = str;
				}
				else
				{
					number = str;
					Player a = new Player(fname, lname, number);
					this.players.put(number, a); //add player to the team
				}
				str = "";
				looper++;
				c = (char) is.read();
			}
		} 
		catch (IOException e) 
		{
			Log.i("Failure", "Reading file failure");
			e.printStackTrace();
		}
	}
}
