package local.stattaker;

import android.app.Activity;
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
		chaser_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{

			@Override
			public boolean onMenuItemClick(MenuItem item) 
			{
				switch ( item.getItemId() )
				{
					case R.id.chaser3_home_assist:
						//add 1 assist to chaser 3
						Toast msg = Toast.makeText(RecordGame.this, "Assist, chaser 3", Toast.LENGTH_SHORT);
						msg.show();
						return true;
				}
				return false;
			}
			
		});
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
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_game, menu);
		return true;
	}

}


