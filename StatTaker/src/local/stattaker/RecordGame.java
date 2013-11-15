package local.stattaker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class RecordGame extends Activity {

	Team homeTeam;
	Team awayTeam;

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
	  homeTeam = new Team(c, hT);
    //Team awayTeam = new Team(c, aT);
	  
	  TextView score = (TextView) findViewById(R.id.score);
	  score.setOnClickListener(new View.OnClickListener() 
	  {
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent (getApplicationContext(), ViewStatsActivity.class);
				i.putExtra("homeScores", homeTeam);
				startActivity(i);
			}
		});
		
		
		
		
		//at the end of it all, save it as a game
		//Game saveGame = new Game (homeTeam, awayTeam);
	}
	

	public void showSeekerHomeMenu(final View v)
	{
		Button btn = (Button) findViewById(R.id.seeker_home);
		final String playerName = btn.getText().toString();
		final String keyVal = btn.getText().toString().split(" ")[0];
		
		PopupMenu seeker_popup = new PopupMenu(this, v);
		seeker_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{

			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				Player a;
				switch ( item.getItemId() )
				{
					case R.id.seeker_home_snitch_catch:
						a = homeTeam.players.get(keyVal);
						a.stats.put("snitches", a.stats.get("snitches")+1 );
						showMessage("Snitch caught by " + playerName );
						return true;
					case R.id.seeker_home_sub_out:
						subOut(homeTeam);
						showHomeBenchMenu(v, R.id.seeker_home);
						return true;
				}
				return false;
			}
			
		});
		MenuInflater inflater = seeker_popup.getMenuInflater();
		inflater.inflate(R.menu.seeker_home_menu, seeker_popup.getMenu() );
		seeker_popup.show();
	}
	
	public void showChaser1HomeMenu(final View v)
	{
		Button btn = (Button) findViewById(R.id.chaser_home_1);
		final String playerName = btn.getText().toString();
		final String keyVal = btn.getText().toString().split(" ")[0];
		
		PopupMenu chaser_popup = new PopupMenu(this, v);
		chaser_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{

			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				Player a;
				switch ( item.getItemId() )
				{
					case R.id.chaser1_home_assist:
						a = homeTeam.players.get(keyVal);
						a.stats.put("assists", a.stats.get("assists")+1 );
						showMessage("Assist, " + playerName );
						return true;
					case R.id.chaser1_home_goal:
						//add 1 plus to every player as well
						//add 1 minus to all opposing players when you get there
						a = homeTeam.players.get(keyVal);
						a.stats.put("goals", a.stats.get("goals")+1);
						a.stats.put("shots", a.stats.get("shots")+1);
						showMessage("Goal, " + playerName );
						return true;
					case R.id.chaser1_home_shot:
						a = homeTeam.players.get(keyVal);
						a.stats.put("shots", a.stats.get("shots")+1);
						showMessage("Shot, " + playerName );
						return true;
					case R.id.chaser1_home_steal:
						a = homeTeam.players.get(keyVal);
						a.stats.put("steals", a.stats.get("steals")+1);
						showMessage("Steal, " + playerName );
						return true;
					case R.id.chaser1_home_turnover:
						a = homeTeam.players.get(keyVal);
						a.stats.put("turnovers", a.stats.get("turnovers")+1);
						showMessage("Turnover, " + playerName );
						return true;
					case R.id.chaser1_home_sub_out:
						subOut(homeTeam);
						showHomeBenchMenu(v, R.id.chaser_home_1);
						return true;
				}
				return false;
			}
			
		});
		MenuInflater inflater = chaser_popup.getMenuInflater();
		inflater.inflate(R.menu.chaser1_home_menu, chaser_popup.getMenu() );
		chaser_popup.show();
	}
	
	public void showChaser2HomeMenu(final View v)
	{
		Button btn = (Button) findViewById(R.id.chaser_home_2);
		final String playerName = btn.getText().toString();
		final String keyVal = btn.getText().toString().split(" ")[0];
		
		PopupMenu chaser_popup = new PopupMenu(this, v);
		chaser_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				Player a;
				switch ( item.getItemId() )
				{
					case R.id.chaser2_home_assist:
						a = homeTeam.players.get(keyVal);
						a.stats.put("assists", a.stats.get("assists")+1 );
						showMessage("Assist, " + playerName );
						return true;
					case R.id.chaser2_home_goal:
						//add 1 plus to every player as well
						//add 1 minus to all opposing players when you get there
						a = homeTeam.players.get(keyVal);
						a.stats.put("goals", a.stats.get("goals")+1);
						a.stats.put("shots", a.stats.get("shots")+1);
						showMessage("Goal, " + playerName );
						return true;
					case R.id.chaser2_home_shot:
						a = homeTeam.players.get(keyVal);
						a.stats.put("shots", a.stats.get("shots")+1);
						showMessage("Shot, " + playerName );
						return true;
					case R.id.chaser2_home_steal:
						a = homeTeam.players.get(keyVal);
						a.stats.put("steals", a.stats.get("steals")+1);
						showMessage("Steal, " + playerName );
						return true;
					case R.id.chaser2_home_turnover:
						a = homeTeam.players.get(keyVal);
						a.stats.put("turnovers", a.stats.get("turnovers")+1);
						showMessage("Turnover, " + playerName );
						return true;
					case R.id.chaser2_home_sub_out:
						showHomeBenchMenu(v, R.id.chaser_home_2);
						return true;
				}
				return false;
			}
			
		});
		MenuInflater inflater = chaser_popup.getMenuInflater();
		inflater.inflate(R.menu.chaser2_home_menu, chaser_popup.getMenu() );
		chaser_popup.show();
	}
	
	public void showChaser3HomeMenu(final View v)
	{

		Button btn = (Button) findViewById(R.id.chaser_home_3);
		final String playerName = btn.getText().toString();
		final String keyVal = btn.getText().toString().split(" ")[0];
		
		PopupMenu chaser_popup = new PopupMenu(this, v);
		chaser_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{

			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				Player a;
				switch ( item.getItemId() )
				{
					case R.id.chaser3_home_assist:
						a = homeTeam.players.get(keyVal);
						a.stats.put("assists", a.stats.get("assists")+1 );
						showMessage("Assist, " + playerName );
						return true;
					case R.id.chaser3_home_goal:
						//add 1 plus to every player as well
						//add 1 minus to all opposing players when you get there
						a = homeTeam.players.get(keyVal);
						a.stats.put("goals", a.stats.get("goals")+1);
						a.stats.put("shots", a.stats.get("shots")+1);
						showMessage("Goal, " + playerName );
						return true;
					case R.id.chaser3_home_shot:
						a = homeTeam.players.get(keyVal);
						a.stats.put("shots", a.stats.get("shots")+1);
						showMessage("Shot, " + playerName );
						return true;
					case R.id.chaser3_home_steal:
						a = homeTeam.players.get(keyVal);
						a.stats.put("steals", a.stats.get("steals")+1);
						showMessage("Steal, " + playerName );
						return true;
					case R.id.chaser3_home_turnover:
						a = homeTeam.players.get(keyVal);
						a.stats.put("turnovers", a.stats.get("turnovers")+1);
						showMessage("Turnover, " + playerName );
						return true;
					case R.id.chaser3_home_sub_out:
						showHomeBenchMenu(v, R.id.chaser_home_3);
						return true;
				}
				return false;
			}
			
		});
		MenuInflater inflater = chaser_popup.getMenuInflater();
		inflater.inflate(R.menu.chaser3_home_menu, chaser_popup.getMenu() );
		chaser_popup.show();
	}
	
	public void showKeeperHomeMenu(final View v)
	{
		Button btn = (Button) findViewById(R.id.keeper_home);
		final String playerName = btn.getText().toString();
		final String keyVal = btn.getText().toString().split(" ")[0];
		
		PopupMenu keeper_popup = new PopupMenu(this, v);
		keeper_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{

			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				Player a;
				switch ( item.getItemId() )
				{
					case R.id.keeper_home_assist:
						a = homeTeam.players.get(keyVal);
						a.stats.put("assists", a.stats.get("assists")+1 );
						showMessage("Assist, " + playerName );
						return true;
					case R.id.keeper_home_goal:
						//add 1 plus to every player as well
						//add 1 minus to all opposing players when you get there
						a = homeTeam.players.get(keyVal);
						a.stats.put("goals", a.stats.get("goals")+1);
						a.stats.put("shots", a.stats.get("shots")+1);
						showMessage("Goal, " + playerName );
						return true;
					case R.id.keeper_home_shot:
						a = homeTeam.players.get(keyVal);
						a.stats.put("shots", a.stats.get("shots")+1);
						showMessage("Shot, " + playerName );
						return true;
					case R.id.keeper_home_steal:
						a = homeTeam.players.get(keyVal);
						a.stats.put("steals", a.stats.get("steals")+1);
						showMessage("Steal, " + playerName );
						return true;
					case R.id.keeper_home_turnover:
						a = homeTeam.players.get(keyVal);
						a.stats.put("turnovers", a.stats.get("turnovers")+1);
						showMessage("Turnover, " + playerName );
						return true;
					case R.id.keeper_home_save:
						a = homeTeam.players.get(keyVal);
						a.stats.put("saves", a.stats.get("saves") + 1);
						showMessage("Save, " + playerName );
						return true;
					case R.id.keeper_home_sub_out:
						showHomeBenchMenu(v, R.id.keeper_home);
						return true;
				}
				return false;
			}
			
		});
		MenuInflater inflater = keeper_popup.getMenuInflater();
		inflater.inflate(R.menu.keeper_home_menu, keeper_popup.getMenu() );
		keeper_popup.show();
	}
	
	protected void showHomeBenchMenu(View v, int buttonId) 
	{
		final int btnId = buttonId;
		PopupMenu bench_popup = new PopupMenu(this, v);
		bench_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				Log.i("Test", "item.id: " + item.getTitle() );
				Button btn = (Button) findViewById(btnId);
				btn.setText(item.getTitle() );
				return true;
			}
		});
		String f;
		String l;
		//it would be nice if it were sorted
		//----populating menu-------------
		for (String key : homeTeam.players.keySet() )
		{
			//put them in order by their number
			
			
			
			f = homeTeam.players.get(key).fname;
			l = homeTeam.players.get(key).lname;
			bench_popup.getMenu().add(key + " " + f + " " + l );
		}
		//----end populating menu----------
		
		MenuInflater inflater = bench_popup.getMenuInflater();
		inflater.inflate(R.menu.home_bench_menu, bench_popup.getMenu() );
		bench_popup.show();
		
	}
	
	protected void showMessage(String string) 
	{
		Toast msg = Toast.makeText(RecordGame.this, string, Toast.LENGTH_SHORT);
		msg.show();
	}


	protected void subOut(Team t)
	{
		//1. menu/drawer for selecting players
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


