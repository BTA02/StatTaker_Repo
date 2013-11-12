package local.stattaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Parse.initialize(this, "RoDlI2ENBnxSWlPvdG2VEsFPRSt06qHJ78nZop77", "fbuEyPT9Exq141IZfueUO1asOcbAFaBjJvdAFI1A");
        ParseAnalytics.trackAppOpened(getIntent());
        
        /*
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        */
        
        Button recordGame = (Button) findViewById(R.id.existing_team_1);
        recordGame.setOnClickListener(new View.OnClickListener() 
        {
					@Override
					public void onClick(View v) 
					{

						//pass over team names to next page
						
						Intent i = new Intent (getApplicationContext(), RecordGame.class);
						//these get set with the actual teams
						String homeTeam = "University_of_Michigan";
						String awayTeam = "Dummy";
						i.putExtra("home_team", homeTeam);
						i.putExtra("away_team", awayTeam);
						startActivity(i);
					}
				});
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void fillOutMichiganRoster()
    {
    	/*
      Roster michiganRoster = new Roster("Michigan");
    	Player a = new Player("Andrew", "Axtell", 2);
    	Player b = new Player("Malek", "Atassi", 404);
    	Player c = new Player("Andy", "Barton", 34);
    	Player d = new Player("Phuc", "Do", 12);
    	Player e = new Player("Matt", "Oppenlander", 22);
    	Player f = new Player("Evan", "Breen", 50);
    	Player g = new Player("Evan", "Batzer", 19);
    	Player h = new Player("Matt", "Oates", 93);
    	Player i = new Player("Michelle", "Busch", 10);
    	Player j = new Player("Margaret", "Burns", 142);
    	Player k = new Player("Danielle", "Dubois", 8);
    	Player l = new Player("Maddy", "Novack", 5);
    	Player m = new Player("Dylan", "Schepers", 18);
    	Player n = new Player("Zach", "Schepers", 25);
    	Player o = new Player("Lucas", "Mitchell", 3.14);
    	Player p = new Player("Lisa", "Lavalenet", 16);
    	Player q = new Player("Sarah", "Halperin", 27);
    	Player r = new Player("Meredith", "Witt", 58);
    	Player s = new Player("Robert", "Morgan", 7);
    	Player t = new Player("Sam", "Morley", 999);
    	Player u = new Player("Ben", "Griesmann", 998);
      michiganRoster.addPlayer(a);
      michiganRoster.addPlayer(b);
      michiganRoster.addPlayer(c);
      michiganRoster.addPlayer(d);
      michiganRoster.addPlayer(e);
      michiganRoster.addPlayer(f);
      michiganRoster.addPlayer(g);
      michiganRoster.addPlayer(h);
      michiganRoster.addPlayer(i);
      michiganRoster.addPlayer(j);
      michiganRoster.addPlayer(k);
      michiganRoster.addPlayer(l);
      michiganRoster.addPlayer(m);
      michiganRoster.addPlayer(n);
      michiganRoster.addPlayer(o);
      michiganRoster.addPlayer(p);
      michiganRoster.addPlayer(q);
      michiganRoster.addPlayer(r);
      michiganRoster.addPlayer(s);
      michiganRoster.addPlayer(t);
      michiganRoster.addPlayer(u);
      */
      
    }
    
}
