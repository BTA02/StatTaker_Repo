package local.stattaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.GameDb;
import local.stattaker.model.TeamDb;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ProgressBar;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity
{

	// TODOs
	// 4. Active checkboxes
	// 5. Download rosters from internet
	// a. Make sure it works offline though
	// 6. Secondary button for subbing
	// 7. Make it pretty
	// a. Active count on the edit team screen
	// b. Clock could be prettier
	// c. Some color
	// d. Center things in lists
	// e. MRU list for subbing in a certain slot
	// f. Deliniate chaser / beater / keeper sections
	// 8. Code efficiency. Currently sucks
	private String			TAG					= "MainActivity";

	DatabaseHelper			db;

	List<TeamDb>			teamList;
	List<TeamDb>			oTeams;

	Button					create_team;
	ListView				currentTeams;
	ListView				onlineTeams;

	ArrayAdapter<TeamDb>	listAdapter;
	ArrayAdapter<TeamDb>	listAdapter2;

	Context					context				= this;
	Activity				activity			= this;

	protected AlertDialog	newTeamDialog		= null;
	ProgressDialog			loadingTeamDialog	= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		db = new DatabaseHelper(this);

		if (isNetworkAvailable())
		{
			Parse.initialize(this, "RoDlI2ENBnxSWlPvdG2VEsFPRSt06qHJ78nZop77",
					"fbuEyPT9Exq141IZfueUO1asOcbAFaBjJvdAFI1A");
			ParseAnalytics.trackAppOpened(getIntent());
		}

		create_team = (Button) findViewById(R.id.create_button);
		create_team.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (newTeamDialog != null && newTeamDialog.isShowing())
				{
					return;
				}
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
						context);

				alertBuilder.setTitle("Create New Team");
				alertBuilder.setMessage("Enter Name of Team:");

				final EditText input = new EditText(context);
				alertBuilder.setView(input);

				alertBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog,
							int whichButton)
					{
						String newTeamId = UUID.randomUUID().toString();
						db.addTeam(newTeamId, input.getText()
								.toString());
						Intent i = new Intent(getApplicationContext(),
								EditTeam.class);
						i.putExtra("teamId", newTeamId);
						startActivity(i);
					}
				});

				alertBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog,
							int whichButton)
					{
						dialog.dismiss();
					}
				});
				newTeamDialog = alertBuilder.create();
				newTeamDialog.show();
			}

		});

		populateTeamList();

		if (isNetworkAvailable())
		{
			populateOnlineTeamList();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		populateTeamList();
		if (isNetworkAvailable())
		{
			populateOnlineTeamList();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// still works, shows local teams, which is perfect
	// this is totally wrong code
	@SuppressWarnings("unchecked")
	public void populateTeamList()
	{
		currentTeams = (ListView) findViewById(R.id.teams_list);
		Cursor c = db.getAllTeamsCursor();

		teamList = new ArrayList<TeamDb>();
		teamList = db.getAllTeamsList();
		Collections.sort(teamList, new TeamDb.OrderByTeamName());
		listAdapter = new ArrayAdapter<TeamDb>(this,
				R.layout.custom_player_list, teamList);
		currentTeams.setAdapter(listAdapter);
		currentTeams.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				final TeamDb team = (TeamDb) currentTeams
						.getItemAtPosition(arg2);
				// create an options pop up to record new game or edit team or
				// see old games
				LayoutInflater dialogFactory = LayoutInflater.from(context);
				final View newGameView = dialogFactory.inflate(
						R.layout.custom_new_game_alert, null);
				AlertDialog.Builder alert = new AlertDialog.Builder(
						MainActivity.this);
				alert.create();

				alert.setView(newGameView);
				alert.setTitle("Options");
				alert.setMessage("Resume Old Game:");

				final EditText oppo = (EditText) newGameView
						.findViewById(R.id.new_game_opponent_name);
				alert.setPositiveButton("Create New Game",
						new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog,
							int which)
					{
						// create a new game with "oppo" as the opponent
						// name
						String opponentName = oppo.getText().toString();
						String mod = UUID.randomUUID().toString();
						mod = mod.substring(0, 4);
						if (opponentName.length() == 0)
						{
							opponentName = "Unnamed Opponent " + "("
									+ mod + ")";
						}
						String gId = db.createNewGame(team.getId(),
								opponentName);
						// when creating a new game, I need to add
						// everyone who is "active"
						// to the "onField" stuff
						Intent i = new Intent(getApplicationContext(),
								FragmentMain.class);
						i.putExtra("gameId", gId);
						startActivity(i);
					}
				});

				alert.setNeutralButton("Edit Team",
						new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog,
							int which)
					{
						String id = team.getId();
						Intent i = new Intent(getApplicationContext(),
								EditTeam.class);
						i.putExtra("teamId", id);
						startActivity(i);
					}
				});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog,
							int which)
					{
						// do nothing
						dialog.dismiss();
					}
				});

				ListView oldGames = (ListView) newGameView
						.findViewById(R.id.new_game_list);
				List<GameDb> gameList = new ArrayList<GameDb>();
				gameList = db.getAllGamesForTeam(team.getId());
				final ListAdapter listAdapter = new ArrayAdapter(context,
						R.layout.custom_player_list, gameList);
				oldGames.setAdapter(listAdapter);
				oldGames.setOnItemClickListener(new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id)
					{
						GameDb gameClicked = (GameDb) listAdapter
								.getItem(position);
						Intent i = new Intent(getApplicationContext(),
								FragmentMain.class);
						i.putExtra("gameId", gameClicked.getId());

						startActivity(i);
					}

				});

				alert.show();

			}

		});

	}

	public void populateOnlineTeamList()
	{
		onlineTeams = (ListView) findViewById(R.id.online_teams_list);

		List<ParseObject> objects = new ArrayList<ParseObject>();
		oTeams = new ArrayList<TeamDb>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Players");
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
			String teamId = objects.get(i).getString("team_id");
			TeamDb teamToAdd = new TeamDb(teamId, teamName);
			if (!teamExists(teamId) && !containsTeam(teamToAdd.getId()))
			{
				oTeams.add(teamToAdd);
			}
		}
		listAdapter2 = new ArrayAdapter<TeamDb>(this,
				R.layout.custom_player_list, oTeams);
		onlineTeams.setAdapter(listAdapter2);

		onlineTeams.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id)
			{
				loadingTeamDialog = new ProgressDialog(context);
				loadingTeamDialog.setTitle("Downloading team");
				loadingTeamDialog.setCancelable(false);
				loadingTeamDialog.show();
				new Thread(new Runnable()
				{
					public void run()
					{
						Object o = onlineTeams.getItemAtPosition(position);
						TeamDb teamClicked = (TeamDb) o;
						loadInTeam(teamClicked.getId());
					}
				}).start();
				//t.start();
				/*
				try
				{
					t.join();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				listAdapter2.notifyDataSetChanged();
				listAdapter.notifyDataSetChanged();
				*/
			}
		});

	}

	public boolean teamExists(String teamId)
	{
		for (int i = 0; i < teamList.size(); i++)
		{
			if (teamList.get(i).getId().equals(teamId))
			{
				return true;
			}
		}
		return false;
	}

	public boolean containsTeam(String teamId)
	{
		for (int i = 0; i < oTeams.size(); i++)
		{
			if (oTeams.get(i).getId().equals(teamId))
			{
				return true;
			}
		}
		return false;
	}

	// There is a better way. Give every team online an ID, display the name,
	// check against the ID

	public void loadInTeam(String teamId)
	{
		List<ParseObject> objects = new ArrayList<ParseObject>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Players");
		query.whereEqualTo("team_id", teamId);
		try
		{
			objects = query.find();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			Log.e("Test", "Parse .find() didn't work 2");
		}

		final String newTeamName = objects.get(0).getString("team_name");
		final String newTeamId = teamId;
		final TeamDb newTeamObj = new TeamDb();
		newTeamObj.setId(newTeamId);
		newTeamObj.setName(newTeamName);
		db.addTeam(newTeamId, newTeamName);
		
		

		for (int i = 0; i < objects.size(); i++)
		{

			String id = objects.get(i).getObjectId();
			String fname = objects.get(i).getString("fname");
			String lname = objects.get(i).getString("lname");
			String number = objects.get(i).getString("number");
			db.addPlayer(id, number, fname, lname);
			db.addPlayerToTeam(id, newTeamId);
		}
		loadingTeamDialog.dismiss();
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				listAdapter.add(new TeamDb(newTeamId, newTeamName));
				for(int i = 0; i < listAdapter2.getCount(); i++)
				{
					Object o = listAdapter2.getItem(i);
					TeamDb t = (TeamDb)o;
					if (t.getId() == newTeamId)
					{
						listAdapter2.remove(listAdapter2.getItem(i));
					}
				}
				
				listAdapter2.notifyDataSetChanged();
				listAdapter.notifyDataSetChanged();
			}
			
		});
	}

	private boolean isNetworkAvailable()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
