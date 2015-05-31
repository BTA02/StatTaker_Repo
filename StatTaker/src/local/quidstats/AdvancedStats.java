package local.quidstats;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class AdvancedStats extends Activity
{
	private String gameId;
	private LinearLayout mParentLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advanced_stats);
		
		mParentLayout = (LinearLayout) findViewById(R.id.advanced_stats_parent_layout);
		Bundle b = getIntent().getExtras();
		gameId = b.getString("gameId");
		
		calAndDispTopChaser();
		calcAndDispTopChaserMin();
		
		
		
	}

	private void calAndDispTopChaser()
	{
		
	}
	
	private void calcAndDispTopChaserMin()
	{
		
	}
	
	
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.advanced_stats, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
