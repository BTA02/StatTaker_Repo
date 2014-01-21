package local.stattaker;

import java.util.ArrayList;
import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.PlayerDb;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EditTeam extends Activity 
{
	DatabaseHelper db;
	
	ListView currentPlayers;
	TextView teamTitle;
	
	String teamName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		db = db.getHelper(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) //something was input
		{
			teamName = extras.getString("teamName");
			
		}
		else //nothing found
		{
			Log.e("Error", "no team selected");
			this.finish();
		}
		teamTitle = (TextView) findViewById(R.id.edit_team_title);
		teamTitle.setText(teamName);
		populatePlayerList(teamName);
		//When you click on a player, their info pops up. Retype everything, update player info function, golden
		
	}
	
	//untested
	//R: a valid teamName
	//M: the listView
	//E: populates the list view with the players on the team
	public void populatePlayerList(String tN) 
	{
  	currentPlayers = (ListView) findViewById(R.id.edit_player_list);
  	List<PlayerDb> playerList = new ArrayList<PlayerDb>();
  	playerList = db.getAllPlayers(tN, 0);
  	ListAdapter listAdapter = new ArrayAdapter(this, R.layout.custom_player_list, playerList);
  	currentPlayers.setAdapter(listAdapter);

    currentPlayers.setOnItemClickListener(new OnItemClickListener()
    {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long rowId) 
			{
				PlayerDb clickedPlayer = (PlayerDb) currentPlayers.getItemAtPosition(position);
				
			}
    	
    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

}
