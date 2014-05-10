package local.stattaker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.GameDb;
import local.stattaker.model.PlayerDb;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity implements OnClickListener
{
		DatabaseHelper db;
		
		List<String> teams;
		List<String> oTeams;
		
		Button create_team;
		ListView currentTeams;
		ListView onlineTeams;
		
		ArrayAdapter<String> listAdapter;
		ArrayAdapter<String> listAdapter2;
		
		Context context = this;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);

        Parse.initialize(this, "RoDlI2ENBnxSWlPvdG2VEsFPRSt06qHJ78nZop77", "fbuEyPT9Exq141IZfueUO1asOcbAFaBjJvdAFI1A");
        ParseAnalytics.trackAppOpened(getIntent());
        
        create_team = (Button) findViewById(R.id.create_button);
        create_team.setOnClickListener(this);
        
        populateTeamsList();
        populateOnlineTeamList();
        
        currentTeams = (ListView) findViewById(R.id.teams_list);
        currentTeams.setOnItemClickListener(new OnItemClickListener()
        {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) 
					{
						//now this is what happens when you click
						final String teamClicked = (String)((TextView) arg1).getText();
						LayoutInflater dialogFactory = LayoutInflater.from(context);
						final View newGameView = dialogFactory.inflate(
				        R.layout.custom_new_game_alert, null);
						AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
						
						alert.setView(newGameView);
	  				alert.setTitle("Options");
	  				alert.setMessage("Resume Old Game:");
	  				
	  				final EditText oppo = (EditText) newGameView
	  						.findViewById(R.id.new_game_opponent_name);
	  				
	  				ListView oldGames = (ListView) newGameView
	  						.findViewById(R.id.new_game_list);
	  				List<GameDb> gameList = new ArrayList<GameDb>();
	  				
	  				
	  		  	gameList = db.getAllGames(teamClicked);
	  		  	
	  		  	ListAdapter listAdapter = new ArrayAdapter(context, R.layout.custom_player_list, gameList);
	  		  	oldGames.setAdapter(listAdapter);
	  		  	
	  				oldGames.setOnItemClickListener(new OnItemClickListener()
	  				{

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) 
							{
								GameDb g = (GameDb) arg0.getItemAtPosition(arg2);
								String t = (String)((TextView) arg1).getText();
								Intent i = new Intent(getApplicationContext(), FragmentMain.class);
	  						i.putExtra("teamName", teamClicked );
	  						i.putExtra("gId", g.getGameId() );
	  						i.putExtra("old", 1);
	  	          startActivity(i);
								
							}
	  					
	  				});
	  				
	  				
	  				alert.setPositiveButton("Create New Game", new DialogInterface.OnClickListener() 
	  				{
	  					public void onClick(DialogInterface dialog, int whichButton) 
	  					{
	  						int index = 1;
	  						int toAdd = 1; //onField value
	  						int newGameId = db.getMaxGameRow() + 1;
	  						List<PlayerDb> newList = db.getAllPlayers(teamClicked, 1);
	  						//gets me a list of players that are active
	  						Iterator<PlayerDb> it = newList.iterator();
	  						while (it.hasNext())
	  						{
	  							String newOpponentString = oppo.getText().toString();
	  							if (newOpponentString.matches(""))
	  							{
	  								newOpponentString = "name left blank";
	  							}
	  							if (index < 8)
	  							{
	  								toAdd = index;
	  							}
	  							else
	  							{
	  								toAdd = 0;
	  							}
	  							if (index == 1) //just do this one time
	  							{
	  								GameDb g1 = new GameDb(newGameId, teamClicked, newOpponentString, -1, 0);
	  								db.insertGameRow(g1);
	  							}
	  							GameDb g = new GameDb(newGameId, teamClicked, newOpponentString, 
	  									it.next().getPlayerId(), toAdd);
	  							db.insertGameRow(g);
	  							index++;
	  						}
	  						
	  						Intent i = new Intent(getApplicationContext(), FragmentMain.class);
	  						i.putExtra("teamName", teamClicked);
	  						i.putExtra("gId", newGameId);
	  	          startActivity(i);
	  				  }
	  				});
	  				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
	  				{
	  				  public void onClick(DialogInterface dialog, int whichButton) 
	  				  {
	  				  	//Cancel
	  				  }
	  				});
	  				alert.setNeutralButton("Edit Team", new DialogInterface.OnClickListener()
	  				{

							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								Intent i = new Intent(getApplicationContext(), EditTeam.class);
								i.putExtra("teamName", teamClicked);
								startActivity(i);
							}
	  					
	  				});
	  				alert.show();
					}
        	
        });
        
        onlineTeams = (ListView) findViewById(R.id.online_teams_list);
        onlineTeams.setOnItemClickListener(new OnItemClickListener()
        {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) 
					{
						final String teamClicked = (String)((TextView) view).getText();
						loadInTeam(teamClicked);
					}
        	
        });
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	populateTeamsList();
    }
		@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
		
		//still works, shows local teams, which is perfect
    public void populateTeamsList() 
    {      
    	currentTeams = (ListView) findViewById(R.id.teams_list);
      teams = new ArrayList<String>();
      teams = db.getCurrentTeams();
      listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teams);
      currentTeams.setAdapter(listAdapter);
      
		}
    
		@Override
		public void onClick(View v) 
		{
  		switch(v.getId())
  		{
  			case(R.id.create_button):
  			{
  				String newTeamName;
  				AlertDialog.Builder alert = new AlertDialog.Builder(this);

  				alert.setTitle("New Team Name");
  				alert.setMessage("Enter Name of Team:");

  				// Set an EditText view to get user input 
  				final EditText input = new EditText(this);
  				alert.setView(input);

  				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
  				{
  					public void onClick(DialogInterface dialog, int whichButton) 
  					{
  						///Log.i("Test", "input: " + input.getText().toString());
  						Intent i = new Intent(getApplicationContext(), CreateTeam.class);
  						i.putExtra("teamName", input.getText().toString());
  	          startActivity(i);
  				  }
  				});

  				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
  				{
  				  public void onClick(DialogInterface dialog, int whichButton) 
  				  {
  				    // Canceled.
  				  }
  				});
  				alert.show();
  				break;
  			}

  		}
			
		}
		public void populateOnlineTeamList()
		{
			onlineTeams = (ListView) findViewById(R.id.online_teams_list);
			
			List<ParseObject> objects = new ArrayList<ParseObject>();
			oTeams = new ArrayList<String>();
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");
			try 
			{
				objects = query.find();
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				Log.e("Test", "Parse .find didn't work 1");
			}
			for (int i = 0; i < objects.size(); i++)
			{
				String teamName = objects.get(i).getString("team_name");
				if (!oTeams.contains(teamName) && !teams.contains(teamName) )
				{
					oTeams.add(teamName);
				}
			}
			listAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, oTeams);
			onlineTeams.setAdapter(listAdapter2);
			
		}
		
		public void loadInTeam(String teamName)
		{
			//build a db object here
			String num;
			String fname;
			String lname;
			int active;
			
			List<ParseObject> objects = new ArrayList<ParseObject>();
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");
			query.whereEqualTo("team_name", teamName);
			try 
			{
				objects = query.find();
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				Log.e("Test", "Parse .find didn't work 2");
			}
			for (int i = 0; i < objects.size(); i++)
			{
				num = objects.get(i).getString("number");
				fname = objects.get(i).getString("fname");
				lname = objects.get(i).getString("lname");
				if (i < 21)
				{
					active = 1;
				}
				else
				{
					active = 0;
				}
				PlayerDb p = new PlayerDb(teamName, -1, num, fname, lname, 0, active);
				db.addPlayer(p);
			}
			populateTeamsList();
			populateOnlineTeamList();
		}

}
