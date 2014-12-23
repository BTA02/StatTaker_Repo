package local.stattaker;

import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.GameDb;
import local.stattaker.model.PlayerDb;
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

public class FragmentWorkStats extends Fragment
{
	View rootView;

	DatabaseHelper db;

	FragmentHolderWork fm;

	TableLayout table;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.fragment_stats, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null)
		{

		}
		updateStats();

		Button refresh = (Button) rootView
				.findViewById(R.id.stats_refresh_button);
		refresh.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				for (int i = 0; i < 7; i++)
				{
					List<PlayerDb> playerList = db
							.getOnFieldPlayersFromGame(fm.gId);

					/*
					 * List<PlayerDb> pp =
					 * db.getOnFieldPlayersFromGame(fm.teamName, fm.gId);
					 * List<GameDb> gg = db.getOneRowByIdKeys(fm.gId,
					 * pp.get(i).getPlayerId()); int tt = db.getGameTime(fm.gId)
					 * - fm.sinceRefresh[i]; fm.sinceRefresh[i] =
					 * db.getGameTime(fm.gId); db.updateStat(fm.gId,
					 * pp.get(i).getPlayerId(), "time", tt);
					 */
				}
				updateStats();
			}

		});

	}

	@Override
	public void onResume()
	{
		super.onResume();
		// updateStats();
	}

	public void updateStats()
	{
		fm = (FragmentHolderWork) getActivity();
		db = fm.db;
		table = (TableLayout) rootView.findViewById(R.id.stats);
		int size = table.getChildCount();
		table.removeViews(1, size - 1);

		Cursor c = db.getGameStats(fm.gId);
		int i = 0;
		if (c != null)
		{
			do
			{
				i++;
				// fill in the table here.

				String number = c.getString(c
						.getColumnIndex(DatabaseHelper.COL_NUMBER));
				String lname = c.getString(c
						.getColumnIndex(DatabaseHelper.COL_LNAME));
				int shots = c
						.getInt(c.getColumnIndex(DatabaseHelper.COL_SHOTS));
				int goals = c
						.getInt(c.getColumnIndex(DatabaseHelper.COL_GOALS));
				int assists = c.getInt(c
						.getColumnIndex(DatabaseHelper.COL_ASSISTS));
				int steals = c.getInt(c
						.getColumnIndex(DatabaseHelper.COL_STEALS));
				int turnovers = c.getInt(c
						.getColumnIndex(DatabaseHelper.COL_TURNOVERS));
				int saves = c
						.getInt(c.getColumnIndex(DatabaseHelper.COL_SAVES));
				int snitches = c.getInt(c
						.getColumnIndex(DatabaseHelper.COL_SNITCHES));
				int plus = c.getInt(c
						.getColumnIndex(DatabaseHelper.COL_PLUSSES));
				int minus = c.getInt(c
						.getColumnIndex(DatabaseHelper.COL_MINUSES));
				int time = c.getInt(c
						.getColumnIndex(DatabaseHelper.COL_TOTAL_TIME));

				// Now I have data, load it
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
				TextView t11 = new TextView(getActivity());

				int totalSeconds = time;
				int minutes = totalSeconds / 60;
				int seconds = totalSeconds % 60;
				String dummy = minutes + ":" + seconds;
				if (seconds < 10)
				{
					dummy = minutes + ":0" + seconds;
				}
				final String timeStr = dummy;

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
				t11.setText(timeStr);

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
				row.addView(t11);

				if (i % 2 == 1)
				{
					row.setBackgroundResource(R.color.row_background);
				}
				table.addView(row);
			}
			while (c.moveToNext());
		}
	}

}
