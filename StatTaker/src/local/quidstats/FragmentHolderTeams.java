package local.quidstats;

import local.quidstats.helper.DatabaseHelper;
import local.quidstats.model.TeamDb;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class FragmentHolderTeams extends FragmentActivity
{

	// TODOs
	// 7. Make it pretty
	// 		b. Clock could be prettier / better with functionality
	// 		c. Some color
	// 		d. Center things in lists
	// 		e. MRU list for subbing in a certain slot
	// 		f. Deliniate chaser / beater / keeper sections
	// 9. Delete team!
	//		a. Should put it back on the "online team list" thing
	private String TAG = "MainActivity";

	DatabaseHelper db;

	Button create_team;

	ArrayAdapter<TeamDb> listAdapter;
	ArrayAdapter<TeamDb> listAdapter2;

	Context context = this;
	Activity activity = this;
	
	TeamDb teamRowList = new TeamDb("aaa-aaa-aaa", "Create new team");

	//Fragment stuff
	PagerAdapterMainPage mPagerAdapterMainPage;
	ViewPager mViewPager;
	ActionBar mActionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_home_screen);

		//--STAET FRAGMENT STUFF
		mPagerAdapterMainPage = new PagerAdapterMainPage(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapterMainPage);
		
		//--END FRAGMENT STUFF

		db = new DatabaseHelper(this);

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
