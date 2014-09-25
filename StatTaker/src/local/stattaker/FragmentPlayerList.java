package local.stattaker;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.GameDb;
import local.stattaker.model.PlayerDb;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentPlayerList extends ListFragment
{

	DatabaseHelper	db;
	String TAG = "FragmentPlayerList";

	ListView		currentPlayers;
	Button			opponentGoal;
	Button			opponentSnitch;
	Button			undoButton;
	Button			redoButton;
	TextView		score;
	TextView		time;

	Timer			timer	= new Timer();
	ListAdapter		listAdapter;
	FragmentMain	fm;
	View			rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_player_list, null);

		score = (TextView) rootView.findViewById(R.id.score);
		time = (TextView) rootView.findViewById(R.id.time);
		opponentGoal = (Button) rootView.findViewById(R.id.opponent_goal);
		opponentSnitch = (Button) rootView.findViewById(R.id.opponent_snitch);
		undoButton = (Button) rootView.findViewById(R.id.undo_button);
		redoButton = (Button) rootView.findViewById(R.id.redo_button);

		return rootView;
	}

	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null)
		{

		}
		fm = (FragmentMain) getActivity();
		db = new DatabaseHelper(fm.context);

		score.setText(fm.gInfo.getHomeScore() + " - " + fm.gInfo.getAwayScore());
		int totalSeconds = fm.gInfo.getGameTimeSeconds();

		for (int i = 0; i < 7; i++)
		{
			fm.timeSubbedIn[i] = totalSeconds;
			fm.sinceRefresh[i] = totalSeconds;
		}
		displayTime(totalSeconds, time);

		fm.running = false;
		time.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				switchTime(v);
			}

		});

		populateList();

		getListView().setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3)
			{
				final PlayerDb player = (PlayerDb) arg0.getItemAtPosition(arg2);
				AlertDialog.Builder subBuilder = subDialog(fm.gId, player);
				subBuilder.show();
				return true; // that's all it took, cool
			}

		});

		opponentGoal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//give minuses
				//add 10 to opponent score
				//add event to the undo/redo stack
				List<PlayerDb> onFieldPlayers = db.getOnFieldPlayersFromGame(fm.gId);
				for (int i = 0; i < onFieldPlayers.size(); i++)
				{
					db.updateStat(fm.gId, onFieldPlayers.get(i).getPlayerId(), 1, DatabaseHelper.COL_MINUSES);
				}
				updateScore("opponent", 10);
			}
		});

		opponentSnitch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				updateScore("opponent", 30);
				//stop the clock?
				//nah, let that run I guess, for now
			}
		});

		undoButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (fm.undoQueue.size() == 0)
				{
					Toast toast = Toast.makeText(getActivity(),
							"Nothing to Undo", Toast.LENGTH_SHORT);
					toast.show();
				}
				else
				{

					// fm.redoQueue.add(a);
				}
			}
		});

		redoButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (fm.redoQueue.size() == 0)
				{
					Toast toast = Toast.makeText(getActivity(),
							"Nothing to Redo", Toast.LENGTH_SHORT);
					toast.show();
				}
				else
				{
					// fm.undoQueue.add(a);
				}
			}
		});

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		final PlayerDb player = (PlayerDb) l.getItemAtPosition(position);

		AlertDialog.Builder statsBuilder = statsDialog(fm.gId, player);
		statsBuilder.show();
	}

	Builder statsDialog(final String gId, final PlayerDb player)
	{
		// setup the dialog
		final Context context = getActivity();
		LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
		final View addStatView = dialogFactory.inflate(
				R.layout.stat_choice_dialog, null);
		final AlertDialog.Builder statsBuilder = new AlertDialog.Builder(
				getActivity());

		statsBuilder.setView(addStatView);
		statsBuilder.setTitle("Action");
		statsBuilder.setCancelable(true);

		CharSequence[] items = { "Shot", "Goal", "Assist", "Steal", "Turnover",
				"Save", "Snitch" };
		
		final String pId = player.getPlayerId();

		statsBuilder.setItems(items, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == 0)
				{
					db.updateStat(gId, pId, 1, DatabaseHelper.COL_SHOTS);
					//add to undo/redo stack
				}
				else if (which == 1)
				{
					db.updateStat(gId, pId, 1, DatabaseHelper.COL_GOALS);
					db.updateStat(gId, pId, 1, DatabaseHelper.COL_SHOTS);
					List<PlayerDb> onFieldPlayers = db.getOnFieldPlayersFromGame(fm.gId);
					for (int i = 0; i < onFieldPlayers.size(); i++)
					{
						db.updateStat(fm.gId, onFieldPlayers.get(i).getPlayerId(), 1, DatabaseHelper.COL_PLUSSES);
					}
					//add to the undo/redo stack
					updateScore("homeTeam", 10);
				}
				else if (which == 2)
				{
					db.updateStat(gId, pId, 1, DatabaseHelper.COL_ASSISTS);
					//add to undo/redo
				}
				else if (which == 3)
				{
					db.updateStat(gId, pId, 1, DatabaseHelper.COL_STEALS);
					//add to undo/redo
				}
				else if (which == 4)
				{
					db.updateStat(gId, pId, 1, DatabaseHelper.COL_TURNOVERS);
					//add to undo/redo
				}
				else if (which == 5)
				{
					db.updateStat(gId, pId, 1, DatabaseHelper.COL_SAVES);
					//add to undo/redo
				}
				else if (which == 6)
				{
					db.updateStat(gId, pId, 1, DatabaseHelper.COL_SNITCHES);
					//add to undo/redo
				}
				else
				{
					// do nothing
					Log.e(TAG, "Fell through the stats tree");
				}
			}
		});

		return statsBuilder;
	}

	Builder subDialog(final String gId, final PlayerDb player)
	{
		// setup the dialog
		final Context context = getActivity();
		LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
		final View addSubView = dialogFactory.inflate(
				R.layout.stat_choice_dialog, null);
		final AlertDialog.Builder subBuilder = new AlertDialog.Builder(
				getActivity());
		subBuilder.setView(addSubView);
		subBuilder.setTitle("Sub in:");
		subBuilder.setCancelable(true);

		final List<PlayerDb> list = db.getOffFieldPlayersFromGame(gId);
		Collections.sort(list, new PlayerDb.OrderByFirstName());
		CharSequence[] items = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			items[i] = list.get(i).toString();
		}

		subBuilder.setItems(items, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				/*
				 * int curTime = db.getGameTime(fm.gId); int loc =
				 * db.getOneRowByIdKeys(fm.gId,
				 * player.getPlayerId()).get(0).getOnField(); int indexedLoc =
				 * loc -1; db.updateStat(fm.gId, player.getPlayerId(),
				 * "onField", 0); db.updateStat(fm.gId,
				 * list.get(which).getPlayerId(), "onField", loc);
				 * 
				 * int timeToAdd = curTime - fm.sinceRefresh[indexedLoc];
				 * db.updateStat(fm.gId, player.getPlayerId(), "time",
				 * timeToAdd); //Here? Is this the problem?
				 * 
				 * Action toAdd = new Action(fm.gId,
				 * list.get(which).getPlayerId(), player.getPlayerId(), "sub",
				 * list.get(which).getLname(), fm.timeSubbedIn[indexedLoc]);
				 * fm.undoQueue.add(toAdd);
				 * 
				 * fm.timeSubbedIn[indexedLoc] = curTime;
				 * fm.sinceRefresh[indexedLoc] = curTime;
				 * 
				 * populateList();
				 */
				int curTime = db.getGameTime(fm.gId);
				int location = db.getListLocation(fm.gId, player.getPlayerId());
				location--; // for zero indexing

			}
		});
		return subBuilder;
	}

	public void populateList() // which list is this?
	{
		List<PlayerDb> playerList = db.getOnFieldPlayersFromGame(fm.gId);
		listAdapter = new ArrayAdapter<PlayerDb>(getActivity(),
				R.layout.custom_player_list, playerList);
		setListAdapter(listAdapter);
	}

	public void showMessage(String name, String action)
	{
		String str = action + " by " + name;
		Toast toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void switchTime(final View v)
	{
		if (fm.running == false)
		{
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask()
			{
				@Override
				public void run()
				{
					// update the game time, and the view
					final TextView clock = (TextView) v;
					db.updateTime(fm.gId, 1); // update every second
					int totalSeconds = db.getGameTime(fm.gId);
					int minutes = totalSeconds / 60;
					int seconds = totalSeconds % 60;
					String gameTime = minutes + ":" + seconds;
					if (seconds < 10)
					{
						gameTime = minutes + ":0" + seconds;
					}
					final String gT = gameTime; // cause it needs to be final
					getActivity().runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							clock.setText(gT);
						}
					});
				}

			}, 0, 1000);
			fm.running = true;
		}
		else
		{
			timer.cancel();
			fm.running = false;
		}
	}

	public void updateTime(int location)
	{
		int totalSeconds = db.getGameTime(fm.gId);
		int timeOnField = totalSeconds - fm.timeSubbedIn[location];
	}

	public void onPause()
	{
		super.onPause();
		// stop clock
		// updateTimeStat
	}

	public void displayTime(int time, TextView timeTV)
	{
		int minutes = time / 60;
		int seconds = time % 60;
		String gameTime = minutes + ":" + seconds;
		if (seconds < 10)
		{
			gameTime = minutes + ":0" + seconds;
		}
		if (minutes != 0 || seconds != 0)
		{
			timeTV.setText(gameTime);
		}
	}
	
	public void updateScore(String who, int howMany)
	{
		GameDb curGame = db.getGameInfo(fm.gId);
		if (who.equals("opponent"))
		{
			db.updateScore(fm.gId, "opponent", curGame.getAwayScore() + howMany);
			score.setText(curGame.getHomeScore() + " - " + (curGame.getAwayScore() + howMany) );
		}
		else
		{
			db.updateScore(fm.gId, "homeTeam", curGame.getHomeScore() + howMany);
			score.setText( (curGame.getHomeScore() + howMany) + " - " + curGame.getAwayScore());
		}
	}

}
