package local.stattaker;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.TeamDb;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class MainActivity extends FragmentActivity
{

	// TODOs
	// Fragment the whole app, I think.... 
	// 7. Make it pretty
	// 		b. Clock could be prettier / better with functionality
	// 		c. Some color
	// 		d. Center things in lists
	// 		e. MRU list for subbing in a certain slot
	// 		f. Deliniate chaser / beater / keeper sections
	// 8. Code efficiency. Currently sucks
	// 9. Delete team!
	//		a. Should put it back on the "online team list" thing
	private String TAG = "MainActivity";

	DatabaseHelper db;

	Button create_team;

	ArrayAdapter<TeamDb> listAdapter;
	ArrayAdapter<TeamDb> listAdapter2;

	Context context = this;
	Activity activity = this;

	protected AlertDialog newTeamDialog = null;

	//Fragment stuff
	MainPagePagerAdapter mMainPagePagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_home_screen);

		mMainPagePagerAdapter = new MainPagePagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mMainPagePagerAdapter);
		

		db = new DatabaseHelper(this);

		/*
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
		 */

	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean isNetworkAvailable()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
