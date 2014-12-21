package local.stattaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import local.stattaker.model.GameDb;
import local.stattaker.model.TeamDb;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentLocalTeam extends Fragment
{
	String TAG = "FragmentLocalTeam";

	MainActivity ma;

	ListView currentTeams;

	List<TeamDb> teamList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_local_team, container,
				false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();
		
		populateLocalList();
	}

	void populateLocalList()
	{

		currentTeams = (ListView) ma.findViewById(R.id.fragment_local_teams_list);
		Cursor c = ma.db.getAllTeamsCursor();

		teamList = new ArrayList<TeamDb>();
		teamList = ma.db.getAllTeamsList();
		Collections.sort(teamList, new TeamDb.OrderByTeamName());
		ma.listAdapter = new ArrayAdapter<TeamDb>(ma.context,
				R.layout.custom_player_list, teamList);
		currentTeams.setAdapter(ma.listAdapter);
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
				LayoutInflater dialogFactory = LayoutInflater.from(ma.context);
				final View newGameView = dialogFactory.inflate(
						R.layout.custom_new_game_alert, null);
				AlertDialog.Builder alert = new AlertDialog.Builder(ma.context);
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
								String gId = ma.db.createNewGame(team.getId(),
										opponentName);
								// when creating a new game, I need to add
								// everyone who is "active"
								// to the "onField" stuff
								Intent i = new Intent(ma.context,
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
								Intent i = new Intent(ma.context,
										EditTeam.class);
								i.putExtra("teamId", id);
								startActivity(i);
							}
						});

				ListView oldGames = (ListView) newGameView
						.findViewById(R.id.new_game_list);
				List<GameDb> gameList = new ArrayList<GameDb>();
				gameList = ma.db.getAllGamesForTeam(team.getId());
				final ListAdapter listAdapter = new ArrayAdapter(ma.context,
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
						Intent i = new Intent(ma.context,
								FragmentMain.class);
						i.putExtra("gameId", gameClicked.getId());

						startActivity(i);
					}

				});

				alert.show();

			}

		});

	}

}