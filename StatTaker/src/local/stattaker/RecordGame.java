package local.stattaker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class RecordGame extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_game);
		
		
		
	}
	
	public void showSeekerMenu(View v)
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
		//chaser_popup.setOnMenuItemClickListener((OnMenuItemClickListener) this);
		MenuInflater inflater = chaser_popup.getMenuInflater();
		inflater.inflate(R.menu.chaser3_home_menu, chaser_popup.getMenu() );
		chaser_popup.show();
	}
	
	public void showKeeperMenu(View v)
	{
		PopupMenu keeper_popup = new PopupMenu(this, v);
		MenuInflater inflater = keeper_popup.getMenuInflater();
		inflater.inflate(R.menu.keeper_home_menu, keeper_popup.getMenu() );
		keeper_popup.show();
	}
	
	//this runs everything
	public boolean onMenuItemClick(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.chaser1_home_assist:
				//do something
				return true;
			case R.id.chaser1_home_goal:
				//do something
				return true;
			case R.id.chaser1_home_shot:
				//do something
				return true;
		
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_game, menu);
		return true;
	}

}


