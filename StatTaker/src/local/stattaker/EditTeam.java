package local.stattaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.PlayerDb;
import local.stattaker.model.TeamDb;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EditTeam extends Activity
{
	private String	TAG	= "EditTeam";

	Context			context;
	Activity		activity;

	DatabaseHelper	db;

	ListView		currentPlayers;
	TextView		teamTitle;
	Button			addPlayerButton;

	TeamDb			team;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		context = this;
		activity = this;
		db = new DatabaseHelper(this);
		String teamId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) // something was input
		{
			teamId = extras.getString("teamId");

		}
		else
		// nothing found
		{
			Log.e(TAG, "no team selected");
			this.finish();
		}
		team = db.getTeamFromId(teamId);

		teamTitle = (TextView) findViewById(R.id.edit_team_title);
		teamTitle.setText(team.getName());
		
		populatePlayerList(team.getId());

	}

	// untested
	// R: a valid teamName
	// M: the listView
	// E: populates the list view with the players on the team
	public void populatePlayerList(String tN)
	{
		currentPlayers = (ListView) findViewById(R.id.edit_player_list);
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		playerList = db.getAllPlayers(tN, 0);
		Collections.sort(playerList, new PlayerDb.OrderByActive());
		ListAdapter listAdapter = new ArrayAdapter(this,
				R.layout.custom_player_list, playerList);
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
		final AlertDialog.Builder editRosterBuilder = new AlertDialog.Builder(
				this);

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
		if (currentPlayer.getActive() == 1)
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
						updatedPlayer.setTeamId(currentPlayer.getTeamId());
						if (activeBox.isChecked())
						{
							updatedPlayer.setActive(1);
						}
						else
						{
							updatedPlayer.setActive(0);
						}

						db.updatePlayerInfo(updatedPlayer);
						populatePlayerList(currentPlayer.getTeamId());
					}

				});

		editRosterBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// exit, so do nothing
					}

				});

		return editRosterBuilder;
	}

	Builder addDialog(final String teamName)
	{
		LayoutInflater dialogFactory = LayoutInflater.from(this);
		final View addDialogView = dialogFactory.inflate(
				R.layout.custom_edit_player_alert, null);
		final AlertDialog.Builder addRosterBuilder = new AlertDialog.Builder(
				this);

		addRosterBuilder.setTitle("Edit Current Player");
		addRosterBuilder.setView(addDialogView);

		final EditText number = (EditText) addDialogView
				.findViewById(R.id.edit_custom_number);
		final EditText firstName = (EditText) addDialogView
				.findViewById(R.id.edit_custom_fname);
		final EditText lastName = (EditText) addDialogView
				.findViewById(R.id.edit_custom_lname);
		final CheckBox activeBox = (CheckBox) addDialogView
				.findViewById(R.id.edit_custom_box);

		addRosterBuilder.setPositiveButton("Save",
				new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						PlayerDb newPlayer = new PlayerDb();
						newPlayer.setNumber(number.getText().toString());
						newPlayer.setFname(firstName.getText().toString());
						newPlayer.setLname(lastName.getText().toString());
						newPlayer.setTeamName(teamName);
						if (activeBox.isChecked())
						{
							newPlayer.setActive(1);
						}
						else
						{
							newPlayer.setActive(0);
						}

						db.addPlayer(newPlayer);
						populatePlayerList(teamName);
					}

				});

		addRosterBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// exit, so do nothing
					}

				});

		return addRosterBuilder;
	}

}
