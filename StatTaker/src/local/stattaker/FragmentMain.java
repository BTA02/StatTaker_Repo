package local.stattaker;

import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.GameDb;
import local.stattaker.model.PlayerDb;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class FragmentMain extends FragmentActivity implements TabListener 
{
	DatabaseHelper db;
	
  MyAdapter mAdapter;
  ViewPager mPager;
  ActionBar mActionBar;
  String teamName;
  String opponent;
  int gameId;
    
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_main);
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
	  
	  db = db.getHelper(getApplicationContext());
	  Bundle b = getIntent().getExtras();
	  teamName = b.getString("teamName");
	  //opponent = b.getString("opponent");
	  opponent = "test";
	  gameId = db.getMaxGameId();
	  createNewGame(teamName, opponent, gameId);
	  
  }
  
  public void createNewGame(String teamName, String opponent, int gID)
  {
  	db = db.getHelper(getApplicationContext());
  	List<PlayerDb> playerList = db.getAllPlayers(teamName, 1);
  	for (int i = 0; i < playerList.size(); i++)
  	{
  		GameDb g = new GameDb(gID, teamName, opponent, playerList.get(i).getPlayerId());
  		db.insertGameRow(g);
  	}
  	//insert a new row for every player. nice
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