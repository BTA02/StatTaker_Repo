package local.stattaker;

import java.util.Vector;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.GameDb;
import local.stattaker.util.Action;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class FragmentMain extends FragmentActivity implements TabListener 
{
	DatabaseHelper db;
	Context context;
	//Activity activity;

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

	Vector<Action> undoQueue = new Vector<Action>();
	Vector<Action> redoQueue = new Vector<Action>();
	//Vector<Integer> times = new Vector<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		context = this;
		//------FRAGMENT STUFF---------
		mAdapter = new MyAdapter( getSupportFragmentManager() );
		mPager = (ViewPager)findViewById(R.id.pager);
		mActionBar = getActionBar();
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				getActionBar().setSelectedNavigationItem(position);
			}
		});

		mPager.setAdapter(mAdapter);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setHomeButtonEnabled(false);
		mActionBar.addTab(mActionBar.newTab().setText("Players").setTabListener(this) );
		mActionBar.addTab(mActionBar.newTab().setText("Stats").setTabListener(this) );
		mPager.setCurrentItem(0);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		//------END FRAGMENT STUFF--------------

		db = new DatabaseHelper(context);

		Bundle b = getIntent().getExtras();
		gId = b.getString("gameId");
		gInfo = db.getGameInfo(gId);
		homeTeamName = gInfo.getHomeTeam();
		awayTeamName = gInfo.getAwayTeam();
		
		running = false;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}


	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) 
	{

	}
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) 
	{
		mPager.setCurrentItem(tab.getPosition());
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) 
	{

	}
}