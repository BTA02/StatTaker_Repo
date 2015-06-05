package local.quidstats;

import java.util.ArrayList;
import java.util.List;

import local.quidstats.model.TeamDb;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class OnlineTeamsFragment extends Fragment
{
	String TAG = "FragmentOnlineTeam";

	MainActivity ma;

	ListView onlineTeams;
	List<TeamDb> oTeams;
	ProgressDialog teamsDialog = null;
	ProgressDialog loadingTeamDialog = null;

	List<ParseObject> objects = new ArrayList<ParseObject>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_team_online, container,
				false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity) getActivity();
		if (ma.isNetworkAvailable())
		{
			populateOnlineTeams();
		}

	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (ma.isNetworkAvailable())
		{
			populateOnlineTeams();
		}
	}

	public void populateOnlineTeams()
	{
		Parse.initialize(ma.context, "RoDlI2ENBnxSWlPvdG2VEsFPRSt06qHJ78nZop77",
				"fbuEyPT9Exq141IZfueUO1asOcbAFaBjJvdAFI1A");
		ParseAnalytics.trackAppOpened(ma.getIntent());

		onlineTeams = (ListView) ma.findViewById(R.id.fragment_online_teams_list);

		//Just need to put parse on the background thread?
		oTeams = new ArrayList<TeamDb>();
		final ParseQuery<ParseObject> query = ParseQuery.getQuery("Teams");
		query.setLimit(1000);

		//Thread stuffs?
		teamsDialog = new ProgressDialog(ma.context);
		teamsDialog.setTitle("Loading Teams");
		teamsDialog.setCancelable(false);
		teamsDialog.show();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					objects = query.find();
				}
				catch (ParseException e)
				{
					e.printStackTrace();
					Log.e("Test", "Parse .find() didn't work 1");
				}

				for (int i = 0; i < objects.size(); i++)
				{
					String teamName = objects.get(i).getString("team_name");
					String teamId = objects.get(i).getObjectId();
					TeamDb teamToAdd = new TeamDb(teamId, teamName);
					if (! ma.db.teamExists(teamId) && !containsTeam(teamToAdd.getId()))
					{
						oTeams.add(teamToAdd);
					}
				}

				ma.listAdapter2 = new ArrayAdapter<TeamDb>(ma.context,
						R.layout.custom_player_list, oTeams);
				getActivity().runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						onlineTeams.setAdapter(ma.listAdapter2);
					}
				});
				
				ma.listAdapter2.sort(new TeamDb.OrderByTeamName());

				onlineTeams.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id)
					{
						loadingTeamDialog = new ProgressDialog(ma.context);
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
					}
				});
			}
		}).start();
		teamsDialog.dismiss();
	}

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
		ma.db.addTeam(newTeamId, newTeamName);

		for (int i = 0; i < objects.size(); i++)
		{

			String id = objects.get(i).getObjectId();
			String fname = objects.get(i).getString("fname");
			String lname = objects.get(i).getString("lname");
			//String number = objects.get(i).getString("number");
			//check if player exists
			if (!ma.db.playerExists(id))
			{
				ma.db.addPlayer(id, "", fname, lname);
			}
			ma.db.addPlayerToTeam(id, newTeamId);
		}
		loadingTeamDialog.dismiss();
		ma.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				ma.listAdapter.add(new TeamDb(newTeamId, newTeamName));
				ma.listAdapter.sort(new TeamDb.OrderByTeamName());
				for (int i = 0; i < ma.listAdapter2.getCount(); i++)
				{
					Object o = ma.listAdapter2.getItem(i);
					TeamDb t = (TeamDb) o;
					if (t.getId() == newTeamId)
					{
						ma.listAdapter2.remove(ma.listAdapter2.getItem(i));
					}
				}

				ma.listAdapter2.notifyDataSetChanged();
				ma.listAdapter.notifyDataSetChanged();

				ma.listAdapter.remove(ma.teamRowList);
				ma.listAdapter.insert(ma.teamRowList, 0);
			}

		});
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


}
