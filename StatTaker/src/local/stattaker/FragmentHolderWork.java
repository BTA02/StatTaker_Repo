package local.stattaker;

import java.util.Stack;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.GameDb;
import local.stattaker.util.Action;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class FragmentHolderWork extends FragmentActivity
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
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}


}