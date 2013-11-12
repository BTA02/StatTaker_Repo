package local.stattaker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

public class RecordGame extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_game);
		
		Context c = this.getApplicationContext();
		
		Bundle rosterNames = getIntent().getExtras();
		if (rosterNames == null)
		{
			Log.i("Failure", "Failed to load roster names");
		}
		String hT = rosterNames.getString("home_team");
		String aT = rosterNames.getString("away_team");
		Team homeTeam = new Team(c, hT);
    //Team awayTeam = new Team(c, aT);
		
		
		
		
		//at the end of it all, save it as a game
		//Game saveGame = new Game (homeTeam, awayTeam);

	}
	

	public void showSeekerHomeMenu(View v)
	{
		PopupMenu seeker_popup = new PopupMenu(this, v);
		MenuInflater inflater = seeker_popup.getMenuInflater();
		inflater.inflate(R.menu.seeker_home_menu, seeker_popup.getMenu() );
		seeker_popup.show();
	}
	
	public void showChaser1HomeMenu(View v)
	{
		PopupMenu chaser_popup = new PopupMenu(this, v);
		//chaser_popup.setOnMenuItemClickListener((OnMenuItemClickListener) this);
		MenuInflater inflater = chaser_popup.getMenuInflater();
		inflater.inflate(R.menu.chaser1_home_menu, chaser_popup.getMenu() );
		chaser_popup.show();
	}
	public void showChaser2HomeMenu(View v)
	{
		PopupMenu chaser_popup = new PopupMenu(this, v);
		//chaser_popup.setOnMenuItemClickListener((OnMenuItemClickListener) this);
		MenuInflater inflater = chaser_popup.getMenuInflater();
		inflater.inflate(R.menu.chaser2_home_menu, chaser_popup.getMenu() );
		chaser_popup.show();
	}
	
	public void showChaser3HomeMenu(View v)
	{
		PopupMenu chaser_popup = new PopupMenu(this, v);
		chaser_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{

			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				switch ( item.getItemId() )
				{
					case R.id.chaser3_home_assist:
						//get player name 
						//homeTeam.players.get("2").stats.get("assits").value += 1;
						showMessage("Assist, Chaser 3");
						return true;
					case R.id.chaser3_home_goal:
						//add 1 goal to chaser 3
						//add 1 plus to every player as well
						//add 1 minus to all opposing players when you get there
						showMessage("Goal, chaser 3");
						return true;
					case R.id.chaser3_home_shot:
						//add 1 shot to chaser 3
						showMessage("Shot, chaser 3");
						return true;
					case R.id.chaser3_home_steal:
						//add 1 steal to chaser 3
						showMessage("Steal, chaser 3");
						return true;
					case R.id.chaser3_home_turnover:
						//add 1 turnover to chaser 3
						showMessage("Turnover, chaser 3");
						return true;
					case R.id.chaser3_home_sub_out:
						subOut();
						return true;
				}
				return false;
			}
			
		});
		MenuInflater inflater = chaser_popup.getMenuInflater();
		inflater.inflate(R.menu.chaser3_home_menu, chaser_popup.getMenu() );
		chaser_popup.show();
	}
	



	public void showKeeperHomeMenu(View v)
	{
		PopupMenu keeper_popup = new PopupMenu(this, v);
		MenuInflater inflater = keeper_popup.getMenuInflater();
		inflater.inflate(R.menu.keeper_home_menu, keeper_popup.getMenu() );
		keeper_popup.show();
	}
	
	protected void showMessage(String string) 
	{
		Toast msg = Toast.makeText(RecordGame.this, string, Toast.LENGTH_SHORT);
		msg.show();
	}


	protected void subOut()
	{
		//1. menu for selecting players
		//2. assign button to edit new players stats
		//3. change the name on the button to #___
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_game, menu);
		return true;
	}

}


