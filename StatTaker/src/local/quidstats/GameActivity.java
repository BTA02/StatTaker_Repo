package local.quidstats;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import local.quidstats.helper.DatabaseHelper;
import local.quidstats.model.GameDb;
import local.quidstats.model.PlayerDb;
import local.quidstats.util.Action;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class GameActivity extends FragmentActivity
{
	DatabaseHelper db;
	Context mContext;

	MyAdapter mAdapter;
	ViewPager mPager;
	ActionBar mActionBar;
	String homeTeamName;
	String awayTeamName;
	GameDb gInfo;
	String gId;
	boolean running;
	int[] timeSubbedIn = new int[7];
	int[] sinceRefresh = new int[7];

	public Fragment playerFrag;

	Stack<Action> undoStack = new Stack<Action>();
	Stack<Action> redoStack = new Stack<Action>();

	//Fragment stuff
	PagerAdapterWorkPage mPagerAdapterWorkPage;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_fragment_holder);

		mContext = this;
		//------FRAGMENT STUFF---------
		mPagerAdapterWorkPage = new PagerAdapterWorkPage(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.work_fragment_holder_pager);
		mViewPager.setAdapter(mPagerAdapterWorkPage);
		//------END FRAGMENT STUFF--------------

		db = new DatabaseHelper(mContext);

		Bundle b = getIntent().getExtras();
		gId = b.getString("gameId");
		gInfo = db.getGameInfo(gId);
		homeTeamName = gInfo.getHomeTeam();
		awayTeamName = gInfo.getAwayTeam();

		running = false;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle presses on the action bar items
		switch (item.getItemId())
		{
			case R.id.action_help:
				showHelpPopup();
				return true;
			case R.id.action_settings:
				//do nothing
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void showHelpPopup()
	{
		AlertDialog.Builder helpDialog = new AlertDialog.Builder(mContext);
		helpDialog.setTitle("How to use:");

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
	
		View layout = inflater.inflate(R.layout.help_layout_work, null);
		helpDialog.setView(layout);
		helpDialog.setCancelable(true);
		AlertDialog alert = helpDialog.show();
	}

}