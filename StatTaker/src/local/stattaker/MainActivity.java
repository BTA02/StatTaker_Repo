package local.stattaker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.PlayerDb;
import local.stattaker.util.CursorAdapterTeamList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity
{
	private String TAG = "MainActivity";
	
	DatabaseHelper db;

	List<String> teams;
	List<String> oTeams;

	Button create_team;
	ListView currentTeams;
	ListView onlineTeams;

	ArrayAdapter<String> listAdapter;
	ArrayAdapter<String> listAdapter2;

	Context context = this;
	Activity activity = this;
	
	protected AlertDialog newTeamDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		db = new DatabaseHelper(this);
		
		if (isNetworkAvailable())
		{
			Parse.initialize(this, 
					"RoDlI2ENBnxSWlPvdG2VEsFPRSt06qHJ78nZop77", 
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
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

				alertBuilder.setTitle("Create New Team");
				alertBuilder.setMessage("Enter Name of Team:");

				// Set an EditText view to get user input 
				final EditText input = new EditText(context);
				alertBuilder.setView(input);

				alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						String newTeamId = UUID.randomUUID().toString();
						db.addTeam(newTeamId, input.getText().toString());
						Intent i = new Intent(getApplicationContext(), EditTeam.class);
						i.putExtra("teamId", newTeamId);
						startActivity(i);
					}
				});

				alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						dialog.dismiss();
					}
				});
				newTeamDialog = alertBuilder.create();
				newTeamDialog.show();
			}
			
		});

		populateTeamsList();

		if (isNetworkAvailable())
		{
			//populateOnlineTeamList();
		}
		
		onlineTeams = (ListView) findViewById(R.id.online_teams_list);
		onlineTeams.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				//do this properly soon
				final String teamClicked = (String)((TextView) view).getText();
				//loadInTeam(teamClicked);
			}

		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		populateTeamsList();
		if (isNetworkAvailable())
		{
			//populateOnlineTeamList();
		}
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
		Cursor c = db.getAllTeamsCursor();
		final CursorAdapterTeamList adapter = new CursorAdapterTeamList(this, c, 0);
		currentTeams.setAdapter(adapter);
		
		currentTeams.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				Cursor c = (Cursor) currentTeams.getItemAtPosition(arg2);
				String id = c.getString(c.getColumnIndex(DatabaseHelper.COL_ID));
				Intent i = new Intent(getApplicationContext(), EditTeam.class);
				i.putExtra("teamId", id);
				startActivity(i);
			}
		
		});
		
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
			//PlayerDb p = new PlayerDb(teamName, -1, num, fname, lname, 0, active);
			//db.addPlayer(p);
		}
		populateTeamsList();
		populateOnlineTeamList();
	}

	private boolean isNetworkAvailable() 
	{
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
