package local.quidstats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import local.quidstats.helper.DatabaseHelper;
import local.quidstats.model.PlayerDb;
import local.quidstats.model.TeamDb;
import local.quidstats.util.CursorAdapterEditPlayerList;
import local.quidstats.util.CursorAdapterPlayerList;
import local.stattaker.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditTeam extends Activity
{
	private String TAG = "EditTeam";

	Context context;
	Activity activity;

	public DatabaseHelper db;

	ListView currentPlayers;
	TextView teamTitle;
	TextView activeCount;
	Button addPlayerButton;

	TeamDb team;
	public String teamId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_team);

		context = this;
		activity = this;
		db = new DatabaseHelper(this);
		teamId = null;
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

		updateActiveCount();

		populatePlayerList();

		// -------add new player button----------------
		Button addPlayerButton = (Button) findViewById(R.id.edit_add_player_button);
		addPlayerButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder newPlayerDialog = new AlertDialog.Builder(
						context);
				newPlayerDialog.setTitle("Add Player");
				newPlayerDialog.setPositiveButton("Add Existing Player",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								// Launch a list of existing players
								AlertDialog.Builder existingPlayerDialog = new AlertDialog.Builder(
										context);
								existingPlayerDialog
										.setTitle("Exisiting Players");

								Cursor c = db.getAllPlayersCursor();
								// Collections.sort(playerList, new
								// PlayerDb.OrderByLastName());
								final CursorAdapterPlayerList listAdapter = new CursorAdapterPlayerList(
										context, c, 0);

								existingPlayerDialog.setAdapter(listAdapter,
										new DialogInterface.OnClickListener()
										{

											@Override
											public void onClick(
													DialogInterface dialog,
													int which)
											{
												// TODO Auto-generated method
												// stub
												// check if player is on the
												// team, if NOT, then add them
												Cursor c = (Cursor) listAdapter
														.getItem(which);
												String id = c.getString(c
														.getColumnIndex(DatabaseHelper.COL_ID));
												if (db.playerExistsOnTeam(id,
														team.getId()))
												{
													Toast toast = Toast
															.makeText(
																	context,
																	"Player is already on team",
																	Toast.LENGTH_SHORT);
													toast.show();
												}
												else
												{
													// PlayerDb pickedPlayer =
													// (PlayerDb)
													// listAdapter.getItem(which);
													db.addPlayerToTeam(id,
															team.getId());
													populatePlayerList();
												}
											}
										});
								existingPlayerDialog.show();
							}
						});
				newPlayerDialog.setNeutralButton("Create New Player",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
									int which)
							{
								// Launch an editor
								View customLayout = View.inflate(context,
										R.layout.create_player_dialog_layout,
										null);
								AlertDialog.Builder createNewPlayerDialog = new AlertDialog.Builder(
										context);
								createNewPlayerDialog.setView(customLayout);
								createNewPlayerDialog
										.setTitle("Create New Player");

								final EditText number = (EditText) customLayout
										.findViewById(R.id.edit_dialog_number);
								final EditText fname = (EditText) customLayout
										.findViewById(R.id.edit_dialog_fname);
								final EditText lname = (EditText) customLayout
										.findViewById(R.id.edit_dialog_lname);

								createNewPlayerDialog.setPositiveButton(
										"Create",
										new DialogInterface.OnClickListener()
										{

											@Override
											public void onClick(
													DialogInterface dialog,
													int which)
											{
												// add player to database AND
												// team
												String playerId = UUID
														.randomUUID()
														.toString();

												db.addPlayer(playerId, number
														.getText().toString(),
														fname.getText()
																.toString(),
														lname.getText()
																.toString());
												db.addPlayerToTeam(playerId,
														teamId);
												populatePlayerList();
											}
										});
								createNewPlayerDialog.setNegativeButton(
										"Cancel",
										new DialogInterface.OnClickListener()
										{

											@Override
											public void onClick(
													DialogInterface dialog,
													int which)
											{
												dialog.dismiss();

											}
										});
								createNewPlayerDialog.show();
							}
						});
				newPlayerDialog.show();
			}

		});

		Button deleteTeamButton = (Button) findViewById(R.id.edit_delete_team);
		deleteTeamButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				db.deleteTeam(teamId);
				//kick you out of the acitivty
				finish();
			}

		});
	}

	public void updateActiveCount()
	{
		if (activeCount == null)
		{
			activeCount = (TextView) findViewById(R.id.edit_team_active_count);
		}
		activeCount.setText("" + db.getActivePlayers(teamId).size());
	}

	public void populatePlayerList()
	{
		currentPlayers = (ListView) findViewById(R.id.edit_player_list);
		List<PlayerDb> playerList = new ArrayList<PlayerDb>();
		playerList = db.getAllPlayersFromTeam(team.getId(), 0);
		Collections.sort(playerList, new PlayerDb.OrderByLastName());
		Cursor c = db.getAllPlayersFromTeamCursor(team.getId(), 0);
		final CursorAdapterEditPlayerList listAdapter = new CursorAdapterEditPlayerList(
				context, c, 0);
		currentPlayers.setAdapter(listAdapter);

		currentPlayers.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Cursor c = (Cursor) listAdapter.getItem(position);
				PlayerDb tempPlayer = new PlayerDb();
				tempPlayer.setFname(c.getString(c
						.getColumnIndex(DatabaseHelper.COL_FNAME)));
				tempPlayer.setLname(c.getString(c
						.getColumnIndex(DatabaseHelper.COL_LNAME)));
				tempPlayer.setNumber(c.getString(c
						.getColumnIndex(DatabaseHelper.COL_NUMBER)));
				tempPlayer.setPlayerId(c.getString(c
						.getColumnIndex(DatabaseHelper.COL_ID)));
				editDialog(tempPlayer).show();
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
		if (db.isPlayerActiveOnTeam(teamId, currentPlayer.getPlayerId()))
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
						if (activeBox.isChecked())
						{
							db.updateActiveInfo(teamId,
									updatedPlayer.getPlayerId(), 1);
						}
						else
						{
							db.updateActiveInfo(teamId,
									updatedPlayer.getPlayerId(), 0);
						}

						db.updatePlayerInfo(updatedPlayer);
						populatePlayerList();
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
						/*
						 * PlayerDb newPlayer = new PlayerDb();
						 * newPlayer.setNumber(number.getText().toString());
						 * newPlayer.setFname(firstName.getText().toString());
						 * newPlayer.setLname(lastName.getText().toString());
						 * newPlayer.setTeamName(teamName); if
						 * (activeBox.isChecked()) { newPlayer.setActive(1); }
						 * else { newPlayer.setActive(0); }
						 * 
						 * db.addPlayer(newPlayer);
						 * populatePlayerList(teamName);
						 */
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
