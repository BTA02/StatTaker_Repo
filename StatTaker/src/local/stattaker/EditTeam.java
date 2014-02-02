package local.stattaker;

import java.util.ArrayList;
import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.PlayerDb;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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
		final Context context = this;
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
		
		currentPlayers = (ListView) findViewById(R.id.edit_player_list);
		currentPlayers.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) 
			{
				PlayerDb clickedPlayer = (PlayerDb) currentPlayers.getItemAtPosition(position);
				AlertDialog.Builder editRosterBuilder = editDialog(clickedPlayer);
        editRosterBuilder.show();
				
				
				
			}
			
		});
		
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


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}
	
	
	Builder editDialog(final PlayerDb currentPlayer)
	{
		LayoutInflater dialogFactory = LayoutInflater.from(this);
		final View editDialogView = dialogFactory.inflate(
        R.layout.custom_edit_player_alert, null);
		final AlertDialog.Builder editRosterBuilder = new AlertDialog.Builder(this);
		
		editRosterBuilder.setTitle("Edit Current Player");
		editRosterBuilder.setView(editDialogView);
		
		final EditText number = (EditText) editDialogView
				.findViewById(R.id.edit_custom_number);
		final EditText firstName = (EditText) editDialogView
				.findViewById(R.id.edit_custom_fname);
		final EditText lastName = (EditText) editDialogView
				.findViewById(R.id.edit_custom_lname);
		final CheckBox activeBox = (CheckBox) editDialogView
				.findViewById(R.id.edit_custom_box);
		
		number.setText(currentPlayer.getNumber());
		firstName.setText(currentPlayer.getFname());
		lastName.setText(currentPlayer.getLname());
		if(currentPlayer.getActive() == 1)
		{
			activeBox.setChecked(true);
		}
		else
		{
			activeBox.setChecked(false);
		}
		
		editRosterBuilder.setPositiveButton("Save", 
				new DialogInterface.OnClickListener() 
		{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						PlayerDb updatedPlayer = new PlayerDb();
						updatedPlayer.setNumber(number.getText().toString());
						updatedPlayer.setFname(firstName.getText().toString());
						updatedPlayer.setLname(lastName.getText().toString());
						updatedPlayer.setPlayerId(currentPlayer.getPlayerId());
						updatedPlayer.setTeamName(currentPlayer.getTeamName());
						if (activeBox.isChecked())
						{
							updatedPlayer.setActive(1);
						}
						else
						{
							updatedPlayer.setActive(0);
						}
						db.updatePlayerInfo(updatedPlayer);
						populatePlayerList(currentPlayer.getTeamName());
					}
					
		});
		
		editRosterBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				//exit, so do nothing
			}
			
		});
		
		return editRosterBuilder;
	}
	
}
