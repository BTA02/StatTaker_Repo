package local.quidstats;

import java.util.HashMap;
import java.util.List;

import local.quidstats.helper.DatabaseHelper;
import local.quidstats.model.MetaStatDb;
import local.quidstats.model.PlayerDb;
import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AdvancedStats extends Activity
{
	private String gameId;
	private LinearLayout mParentLayout;
	private List<MetaStatDb> mMetaStats;
	private SparseArray<List<String> > mTimeArray;
	private DatabaseHelper db;
	
	
	private ListView mStatsList;
	
	private ArrayAdapter mAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advanced_stats);
		
		mParentLayout = (LinearLayout) findViewById(R.id.advanced_stats_parent_layout);
		Bundle b = getIntent().getExtras();
		gameId = b.getString("gameId");
		
		db = new DatabaseHelper(this);
		
		mStatsList = (ListView) findViewById(R.id.statList);
		mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
		mStatsList.setAdapter(mAdapter);
		
		mMetaStats = db.getAllMetaStats(gameId);
		mTimeArray = db.getGameInfo(gameId).getTimeArray();
		
		calcAndDispTopChaser();
		calcAndDispTopChaserMin();
		
		calcAndDispTopLineup();
		
	}

	private void calcAndDispTopChaser()
	{
		
	}
	
	private void calcAndDispTopChaserMin()
	{
		
	}
	
	private void calcAndDispTopLineup()
	{
		HashMap<List<String>, Integer> map = new HashMap<List<String>, Integer>();
		for (MetaStatDb action : mMetaStats ) 
		{
			if (action.getStatType().equals(DatabaseHelper.COL_GOALS))
			{
				List<String> lineUp = mTimeArray.get(action.getTimeOfAction());
				Integer i = (Integer) map.get(lineUp);
				if (i == null) 
				{
					i = 0;
				}
				map.put(lineUp, ++i); 
			}
			else if (action.getStatType().equals("away_goal"))
			{
				List<String> lineUp = mTimeArray.get(action.getTimeOfAction());
				Integer i = (Integer) map.get(lineUp);
				if (i == null) 
				{
					i = 0;
				}
				map.put(lineUp, --i);
			}
		}
		
		for (List<String> entry : map.keySet())
		{
			TextView players = new TextView(this);
			for (String playerId : entry)
			{
				PlayerDb p = db.getPlayerById(playerId);
				if (p != null)
				{
					String str = players.getText().toString();
					players.setText(str + p.getLname() + "\n");
				}
				
			}
			String str = players.getText().toString();
			players.setText(str + map.get(entry).toString());
			mAdapter.add(players.getText().toString());
			mAdapter.notifyDataSetChanged();
		}
		
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
