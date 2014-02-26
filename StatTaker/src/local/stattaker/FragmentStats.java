package local.stattaker;

import local.stattaker.helper.DatabaseHelper;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FragmentStats extends Fragment
{
	View rootView;
	
	DatabaseHelper db;
	
	FragmentMain fm;
	
	TableLayout table;
	
	@Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
  {
		rootView = inflater.inflate(R.layout.fragment_stats, container, false);
		updateStats();
		
		Button refresh = (Button) rootView.findViewById(R.id.stats_refresh_button);
		refresh.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				updateStats();
			}
			
		});
		
		return rootView;
  }
	
	@Override
	public void onResume()
	{
		super.onResume();
		//updateStats();
	}
	
	public void updateStats()
	{
		fm = (FragmentMain) getActivity();
		db = db.getHelper(fm.getApplicationContext());
		table = (TableLayout) rootView.findViewById(R.id.stats);
		int size = table.getChildCount();
		table.removeViews(1, size-1);
		
		Cursor c = db.getGameStats(fm.gId);
		if (c.moveToFirst())
		{
			do
			{
				//fill in the table here.
				String number = c.getString(2);
				String lname = c.getString(4);
				int shots = c.getInt(10);
				int goals = c.getInt(11);
				int assists = c.getInt(12);
				int steals = c.getInt(13);
				int turnovers = c.getInt(14);
				int saves = c.getInt(15);
				int snitches = c.getInt(16);
				int plus = c.getInt(17);
				int minus = c.getInt(18);
				
				//Now I have data, load it
				TableRow row = new TableRow(getActivity());
				
				TextView t = new TextView(getActivity());
				TextView t1 = new TextView(getActivity());
				TextView t2 = new TextView(getActivity());
				TextView t3 = new TextView(getActivity());
				TextView t4 = new TextView(getActivity());
				TextView t5 = new TextView(getActivity());
				TextView t6 = new TextView(getActivity());
				TextView t7 = new TextView(getActivity());
				TextView t8 = new TextView(getActivity());
				TextView t9 = new TextView(getActivity());
				TextView t10 = new TextView(getActivity());
				
				t.setText(number);
				t1.setText(lname);
				t2.setText(String.valueOf(shots));
				t3.setText(String.valueOf(goals));
				t4.setText(String.valueOf(assists));
				t5.setText(String.valueOf(steals));
				t6.setText(String.valueOf(turnovers));
				t7.setText(String.valueOf(saves));
				t8.setText(String.valueOf(snitches));
				t9.setText(String.valueOf(plus));
				t10.setText(String.valueOf(minus));
				
				row.addView(t);
				row.addView(t1);
				row.addView(t2);
				row.addView(t3);
				row.addView(t4);
				row.addView(t5);
				row.addView(t6);
				row.addView(t7);
				row.addView(t8);
				row.addView(t9);
				row.addView(t10);
				
				table.addView(row);
			}
			while (c.moveToNext());
		}
	}
}
