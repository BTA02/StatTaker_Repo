package local.stattaker;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.GameDb;
import local.stattaker.model.PlayerDb;
import local.stattaker.util.Action;
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
	String			TAG		= "FragmentPlayerList";

	ListView		currentPlayers;
	Button			opponentGoal;
	Button			opponentSnitch;
	Button			homeSnitch;
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
		homeSnitch = (Button) rootView.findViewById(R.id.home_snitch);
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
				opponentScored();
			}
		});

		opponentSnitch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				opponentSnitch();
			}
		});

		homeSnitch.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				homeSnitch();
			}

		});

		undoButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (fm.undoStack.size() == 0)
				{
					Toast toast = Toast.makeText(getActivity(),
							"Nothing to Undo", Toast.LENGTH_SHORT);
					toast.show();
				}
				else
				{
					Action a = fm.undoStack.pop();
					String col = a.getDatabaseColumn();
					// for opponents
					if (a.getPlayerId().equals("AWAY"))
					{
						// do away team shit
						if (a.getDatabaseColumn().equals(
								DatabaseHelper.COL_AWAY_SCORE))
						{
							updateScore("opponent", -10);
							// get rid of the minuses
							List<PlayerDb> onFieldPlayers = db
									.getOnFieldPlayersFromGame(fm.gId);
							for (int i = 0; i < onFieldPlayers.size(); i++)
							{
								db.updateStat(fm.gId, onFieldPlayers.get(i)
										.getPlayerId(), -1,
										DatabaseHelper.COL_MINUSES);
							}
						}
						else
						{
							updateScore("opponent", -(a.getValueAdded()));
						}
					}
					//for home team...
					else
					{
						if (a.getDatabaseColumn().equals(
								DatabaseHelper.COL_TOTAL_TIME))
						{
							int curTime = db.getGameTime(fm.gId);
							int timeChange = curTime - a.getTimeSwitched();
							//actually change who is in the game
							db.subOut(fm.gId, a.getPlayerId(), a.getPlayerSubbedOut(), a.getValueAdded());
							db.updateStat(fm.gId, a.getPlayerId(), -timeChange, DatabaseHelper.COL_TOTAL_TIME);
							db.updateStat(fm.gId, a.getPlayerSubbedOut(), timeChange, DatabaseHelper.COL_TOTAL_TIME);
							populateList();
						}
						else
						{
							db.updateStat(fm.gId, a.getPlayerId(),
									-(a.getValueAdded()), col);
							if (col.equals(DatabaseHelper.COL_GOALS))
							{
								db.updateStat(fm.gId, a.getPlayerId(), -1,
										DatabaseHelper.COL_SHOTS);
								List<PlayerDb> onFieldPlayers = db
										.getOnFieldPlayersFromGame(fm.gId);
								for (int i = 0; i < onFieldPlayers.size(); i++)
								{
									db.updateStat(fm.gId, onFieldPlayers.get(i)
											.getPlayerId(),
											-(a.getValueAdded()),
											DatabaseHelper.COL_PLUSSES);
								}
								updateScore("homeTeam", -10);
							}
							else if (col.equals(DatabaseHelper.COL_SNITCHES))
							{
								updateScore("homeTeam", -30);
							}
						}
					}
					fm.redoStack.push(a);
					//Clear the stack on certain undos
				}
			}
		});

		redoButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (fm.redoStack.size() == 0)
				{
					Toast toast = Toast.makeText(getActivity(),
							"Nothing to Redo", Toast.LENGTH_SHORT);
					toast.show();
				}
				else
				{
					redoAction();
				}
			}
		});

	}
	
	public void opponentScored()
	{
		List<PlayerDb> onFieldPlayers = db
				.getOnFieldPlayersFromGame(fm.gId);
		for (int i = 0; i < onFieldPlayers.size(); i++)
		{
			db.updateStat(fm.gId, onFieldPlayers.get(i).getPlayerId(),
					1, DatabaseHelper.COL_MINUSES);
		}
		Action toAdd = new Action();
		toAdd.setDatabaseColumn(DatabaseHelper.COL_AWAY_SCORE);
		toAdd.setGameId(fm.gId);
		toAdd.setPlayerId("AWAY");
		toAdd.setValueAdded(10);// because I'll want to subtract this
		fm.undoStack.add(toAdd);

		updateScore("opponent", 10);
	}
	
	public void opponentSnitch()
	{
		updateScore("opponent", 30);
		Action toAdd = new Action();
		toAdd.setDatabaseColumn("away_snitch");
		toAdd.setGameId(fm.gId);
		toAdd.setPlayerId("AWAY");
		toAdd.setValueAdded(1);// because I'll want to subtract this
		fm.undoStack.add(toAdd);
	}
	
	public void homeSnitch()
	{
		//assign to a player
		//launch a list of players
		AlertDialog.Builder snitchBuilder = snitchDialog(fm.gId);
		snitchBuilder.show();
	}

	public void homeStat(String playerId, String statColumn)
	{
		db.updateStat(fm.gId, playerId, 1, statColumn);
		Action toAdd = new Action();
		toAdd.setDatabaseColumn(DatabaseHelper.COL_SHOTS);
		toAdd.setGameId(fm.gId);
		toAdd.setPlayerId(playerId);
		toAdd.setValueAdded(1);// because I'll want to subtract this
		fm.undoStack.add(toAdd);
		if (statColumn.equals(DatabaseHelper.COL_GOALS))
		{
			db.updateStat(fm.gId, playerId, 1, DatabaseHelper.COL_SHOTS);
			List<PlayerDb> onFieldPlayers = db
					.getOnFieldPlayersFromGame(fm.gId);
			for (int i = 0; i < onFieldPlayers.size(); i++)
			{
				db.updateStat(fm.gId, onFieldPlayers.get(i)
						.getPlayerId(), 1, DatabaseHelper.COL_PLUSSES);
			}
			updateScore("homeTeam", 10);
		}
		else if (statColumn.equals(DatabaseHelper.COL_SNITCHES))
		{
			db.updateScore(fm.gId, "homeTeam", 30);
		}
	}
	
	public void redoAction()
	{
		Action a = fm.redoStack.pop();
		//for a substitution
		if (a.getDatabaseColumn().equals(DatabaseHelper.COL_TOTAL_TIME))
		{
			int curTime = db.getGameTime(fm.gId);
			int timeChange = curTime - a.getTimeSwitched();
			//actually change who is in the game
			db.subOut(fm.gId, a.getPlayerSubbedOut(), a.getPlayerId(), a.getValueAdded());
			db.updateStat(fm.gId, a.getPlayerId(), timeChange, DatabaseHelper.COL_TOTAL_TIME);
			db.updateStat(fm.gId, a.getPlayerSubbedOut(), -timeChange, DatabaseHelper.COL_TOTAL_TIME);
			populateList();
			fm.undoStack.push(a);
		}
		if (a.getPlayerId().equals("AWAY"))
		{
			if (a.getDatabaseColumn().equals(
					DatabaseHelper.COL_AWAY_SCORE))
			{
				opponentScored();
				return;
			}
			else //caught snitch
			{
				opponentSnitch();
				return;
			}
		}
		//Something we did...
		if (a.getDatabaseColumn().equals("home_snitch"))
		{
			homeSnitch();
			return;
		}
		homeStat(a.getPlayerId(), a.getDatabaseColumn());
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
					homeStat(pId, DatabaseHelper.COL_SHOTS);
					
				}
				else if (which == 1)
				{
					homeStat(pId, DatabaseHelper.COL_GOALS);
				}
				else if (which == 2)
				{
					homeStat(pId, DatabaseHelper.COL_ASSISTS);
				}
				else if (which == 3)
				{
					homeStat(pId, DatabaseHelper.COL_STEALS);
				}
				else if (which == 4)
				{
					homeStat(pId, DatabaseHelper.COL_TURNOVERS);
				}
				else if (which == 5)
				{
					homeStat(pId, DatabaseHelper.COL_SAVES);
				}
				else if (which == 6)
				{
					homeStat(pId, DatabaseHelper.COL_SNITCHES);
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
				Action subOutAct = new Action();
				subOutAct.setDatabaseColumn(DatabaseHelper.COL_TOTAL_TIME);
				subOutAct.setGameId(fm.gId);
				subOutAct.setPlayerId(list.get(which).getPlayerId());
				subOutAct.setPlayerSubbedOut(player.getPlayerId());
				subOutAct.setTimeSwitched(db.getGameTime(fm.gId));
				int spotOnList = db.getListLocation(fm.gId,
						player.getPlayerId());
				subOutAct.setValueAdded(spotOnList); // location WRONG!
				if (spotOnList > 0)
				{
					db.subOut(fm.gId, player.getPlayerId(), list.get(which)
							.getPlayerId(), spotOnList);
				}
				else
				{
					showMessage("An error has occured", "The Guardians");
				}

				fm.undoStack.add(subOutAct);

				populateList();

			}
		});
		return subBuilder;
	}
	
	Builder snitchDialog(final String gId)
	{
		// setup the dialog
		LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
		final View addSubView = dialogFactory.inflate(
				R.layout.stat_choice_dialog, null);
		final AlertDialog.Builder snitchBuilder = new AlertDialog.Builder(
				getActivity());
		snitchBuilder.setView(addSubView);
		snitchBuilder.setTitle("Who caught the snitch?");
		snitchBuilder.setCancelable(false);

		final List<PlayerDb> list = db.getOffFieldPlayersFromGame(gId);
		Collections.sort(list, new PlayerDb.OrderByFirstName());
		CharSequence[] items = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			items[i] = list.get(i).toString();
		}

		snitchBuilder.setItems(items, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				updateScore("homeTeam", 30);
				Action toAdd = new Action();
				toAdd.setDatabaseColumn(DatabaseHelper.COL_SNITCHES);
				toAdd.setGameId(fm.gId);
				toAdd.setPlayerId(list.get(which).getPlayerId());
				toAdd.setValueAdded(1);
				db.updateStat(fm.gId, list.get(which).getPlayerId(), 1, DatabaseHelper.COL_SNITCHES);
				fm.undoStack.add(toAdd);

			}
		});
		return snitchBuilder;
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
					// add one second to every player on the field
					List<PlayerDb> players = db
							.getOnFieldPlayersFromGame(fm.gId);
					for (int i = 0; i < players.size(); i++)
					{
						db.updateStat(fm.gId, players.get(i).getPlayerId(), 1,
								DatabaseHelper.COL_TOTAL_TIME);
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
			score.setText(curGame.getHomeScore() + " - "
					+ (curGame.getAwayScore() + howMany));
		}
		else
		{
			db.updateScore(fm.gId, "homeTeam", curGame.getHomeScore() + howMany);
			score.setText((curGame.getHomeScore() + howMany) + " - "
					+ curGame.getAwayScore());
		}
	}

}
