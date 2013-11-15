package local.stattaker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewStatsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_stats);
		
		Team homeTeam = null;
		Team awayTeam = null;
		homeTeam = (Team) getIntent().getSerializableExtra("homeScores");
		
		int g;
		int a;
		int s;
		int t;
		int sh;
		int sa;
		int sn;
		TextView fname;
		TextView lname;
		TextView number;
		TextView goals;
		TextView assists;
		TextView steals;
		TextView turnovers;
		TextView shots;
		TextView saves;
		TextView snitches;
		TableLayout homeTeamTable = (TableLayout) findViewById(R.id.homeTable);
		
		
		for (String key : homeTeam.players.keySet() )
		{
			
			TableRow row = new TableRow(this);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
			
			row.setLayoutParams(lp);
			fname = new TextView(this);
			fname.setText(homeTeam.players.get(key).fname);
			
			lname = new TextView(this);
			lname.setText(homeTeam.players.get(key).lname);
			
			number = new TextView(this);
			number.setText(key);
			
			goals = new TextView(this);
			g = homeTeam.players.get(key).stats.get("goals") ;
			goals.setText(""+ g);
			
			assists = new TextView(this);
			a = homeTeam.players.get(key).stats.get("assists");
			assists.setText(""+a);
			
			steals = new TextView(this);
			s = homeTeam.players.get(key).stats.get("steals");
			steals.setText(""+s);
			
			turnovers = new TextView(this);
			t = homeTeam.players.get(key).stats.get("turnovers");
			turnovers.setText(""+t);
			
			shots = new TextView(this);
			sh = homeTeam.players.get(key).stats.get("shots");
			shots.setText(""+sh);
			
			saves = new TextView(this);
			sa = homeTeam.players.get(key).stats.get("saves");
			saves.setText(""+sa);
			
			snitches = new TextView(this);
			sn = homeTeam.players.get(key).stats.get("snitches");
			snitches.setText(""+sn);
			
			
			row.addView(number);
			row.addView(fname);
			row.addView(lname);
			row.addView(shots);
			row.addView(goals);
			row.addView(assists);
			row.addView(turnovers);
			row.addView(steals);
			row.addView(saves);
			row.addView(snitches);
			TableLayout table = (TableLayout)findViewById(R.id.homeTable);
			table.addView(row, lp);
			}
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_stats, menu);
		return true;
	}

}
