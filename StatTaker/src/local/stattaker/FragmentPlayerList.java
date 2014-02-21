package local.stattaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import local.stattaker.helper.DatabaseHelper;
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
	DatabaseHelper db;
	
	ListView currentPlayers;
	Button opponentGoal;
	Button opponentSnitch;
	Button undoButton;
	Button redoButton;
	TextView score;
	
	ListAdapter listAdapter;
	
	//int fm.homeScore = 0;
	//int fm.awayScore = 0;		

	
	FragmentMain fm;
	
	View rootView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.fragment_player_list, null);
		
		score = (TextView) rootView.findViewById(R.id.score);
		opponentGoal = (Button) rootView.findViewById(R.id.opponent_goal);
		opponentSnitch = (Button) rootView.findViewById(R.id.opponent_snitch);
		undoButton = (Button) rootView.findViewById(R.id.undo_button);
		redoButton = (Button) rootView.findViewById(R.id.redo_button);
		
    return rootView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		fm = (FragmentMain) getActivity();

		score.setText(fm.homeScore + " - " + fm.awayScore);
		
		db = db.getHelper(fm.getApplicationContext());
		
		//need to re-run this
		//made this a function called, "populate list"
		populateList();
		
		getListView().setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				final PlayerDb player = (PlayerDb) arg0.getItemAtPosition(arg2);
				AlertDialog.Builder subBuilder = subDialog(fm.gId, player);
				subBuilder.show();
				return true; //that's all it took, cool
			}
			
		});
		
		
		opponentGoal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//update everyone with a minus
				List<PlayerDb> p = db.getOnFieldPlayersFromGame(fm.teamName, fm.gId);
				for (int i = 0; i < p.size(); i++)
				{
					int pid = p.get(i).getPlayerId();
					db.updateStat(fm.gId, pid, "minuses", 1);
				}
				fm.awayScore += 10;
				score.setText(fm.homeScore + " - " + fm.awayScore);
				showMessage("Opponent", "Goal");
				Action toAdd = new Action(fm.gId, -1, -1, "oGoal", "oppo", 1);
				fm.undoQueue.add(toAdd);
			}
		});
		
		opponentSnitch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				fm.awayScore += 30;
				score.setText(fm.homeScore + " - " + fm.awayScore);
				showMessage("Opponent", "Snitch Catch");
				Action toAdd = new Action(fm.gId, -1, -1, "oSnitch", "oppo", 1);
				fm.undoQueue.add(toAdd);
			}
		});
		
		undoButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if (fm.undoQueue.size() == 0)
				{
					Toast toast = Toast.makeText(getActivity(), "Nothing to Undo", Toast.LENGTH_SHORT);
					toast.show();
				}
				else
				{
					Action a = fm.undoQueue.lastElement();
					fm.undoQueue.removeElementAt(fm.undoQueue.size()-1);
					if (a.getpId() == -1) //opponent did it
					{
						if (a.getCategory().equals("oGoal")) //they scored
						{
							List<PlayerDb> p = db.getOnFieldPlayersFromGame(fm.teamName, fm.gId);
							for (int i = 0; i < p.size(); i++)
							{
								int pid = p.get(i).getPlayerId();
								db.updateStat(fm.gId, pid, "minuses", -1);
							}
							fm.awayScore -= 10;
							score.setText(fm.homeScore + " - " + fm.awayScore);
							showMessage("Opponent", "Remove Goal");
						}
						else
						{
							fm.awayScore -= 30;
							score.setText(fm.homeScore + " - " + fm.awayScore);
							showMessage("Opponent", "Remove Snitch Catch");
						}
					}
					else //a player on my team did it
					{
						if (a.getCategory().equals("shots"))
						{
							db.updateStat(a.getgId(), a.getpId(), "shots", -1);
							showMessage(a.getName(), "Undo shot");
						}
						else if (a.getCategory().equals("goals"))
						{
							List<PlayerDb> p = db.getOnFieldPlayersFromGame(fm.teamName, fm.gId);
							for (int i = 0; i < p.size(); i++)
							{
								int pid = p.get(i).getPlayerId();
								db.updateStat(fm.gId, pid, "plusses", -1);
							}
							db.updateStat(a.getgId(), a.getpId(), "goals", -1);
							db.updateStat(a.getgId(), a.getpId(), "shots", -1);
							//plus / minus stuff here
							fm.homeScore -= 10;
							score.setText(fm.homeScore + " - " + fm.awayScore);
							showMessage(a.getName(), "Undo goal");
						}
						else if (a.getCategory().equals("assists"))
						{
							db.updateStat(a.getgId(), a.getpId(), "assists", -1);
							showMessage(a.getName(), "Undo assist");
						}
						else if (a.getCategory().equals("steals"))
						{
							db.updateStat(a.getgId(), a.getpId(), "steals", -1);
							showMessage(a.getName(), "Undo steal");
						}
						else if (a.getCategory().equals("turnovers"))
						{
							db.updateStat(a.getgId(), a.getpId(), "turnovers", -1);
							showMessage(a.getName(), "Undo turnover");
						}
						else if (a.getCategory().equals("saves"))
						{
							db.updateStat(a.getgId(), a.getpId(), "saves", -1);
							showMessage(a.getName(), "Undo save");
						}
						else if (a.getCategory().equals("snitches"))
						{
							db.updateStat(a.getgId(), a.getpId(), "snitches", -1);
							fm.homeScore -= 30;
							score.setText(fm.homeScore + " - " + fm.awayScore);
							showMessage(a.getName(), "Undo snitch catch");
						}
						else if (a.getCategory().equals("sub"))
						{
							PlayerDb wentIn = db.getOnePlayerRow(fm.teamName, a.getpId());
							PlayerDb wentOut = db.getOnePlayerRow(fm.teamName, a.getpIdOut());
							int wentInNum = wentIn.getOnField();
							int wentOutNum = wentOut.getOnField();
							wentIn.setOnField(wentOutNum);
							wentOut.setOnField(wentInNum);
							db.updatePlayerInfo(wentIn);
							db.updatePlayerInfo(wentOut);
							showMessage(wentOut.getLname(), "Undo sub");
							populateList();
						}
					}
					fm.redoQueue.add(a);
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
					Toast toast = Toast.makeText(getActivity(), "Nothing to Redo", Toast.LENGTH_SHORT);
					toast.show();
				}
				else
				{
					Action a = fm.redoQueue.lastElement();
					fm.redoQueue.removeElementAt(fm.redoQueue.size()-1);
					if (a.getpId() == -1) //opponent did it
					{
						if (a.getCategory().equals("oGoal")) //they scored
						{
							List<PlayerDb> p = db.getOnFieldPlayersFromGame(fm.teamName, fm.gId);
							for (int i = 0; i < p.size(); i++)
							{
								int pid = p.get(i).getPlayerId();
								db.updateStat(fm.gId, pid, "minuses", 1);
							}
							fm.awayScore += 10;
							score.setText(fm.homeScore + " - " + fm.awayScore);
							showMessage("Opponent", "Redo Goal");
						}
						else
						{
							fm.awayScore += 30;
							score.setText(fm.homeScore + " - " + fm.awayScore);
							showMessage("Opponent", "Redo Snitch Catch");
						}
					}

					else //a player on my team did it
					{
						if (a.getCategory().equals("shots"))
						{
							db.updateStat(a.getgId(), a.getpId(), "shots", 1);
							showMessage(a.getName(), "Redo shot");
						}
						else if (a.getCategory().equals("goals"))
						{
							List<PlayerDb> p = db.getOnFieldPlayersFromGame(fm.teamName, fm.gId);
							for (int i = 0; i < p.size(); i++)
							{
								int pid = p.get(i).getPlayerId();
								db.updateStat(fm.gId, pid, "plusses", 1);
							}
							db.updateStat(a.getgId(), a.getpId(), "goals", 1);
							db.updateStat(a.getgId(), a.getpId(), "shots", 1);
							//plus / minus stuff here
							fm.homeScore += 10;
							score.setText(fm.homeScore + " - " + fm.awayScore);
							showMessage(a.getName(), "Redo goal");
						}
						else if (a.getCategory().equals("assists"))
						{
							db.updateStat(a.getgId(), a.getpId(), "assists", 1);
							showMessage(a.getName(), "Redo assist");
						}
						else if (a.getCategory().equals("steals"))
						{
							db.updateStat(a.getgId(), a.getpId(), "steals", 1);
							showMessage(a.getName(), "Redo steal");
						}
						else if (a.getCategory().equals("turnovers"))
						{
							db.updateStat(a.getgId(), a.getpId(), "turnovers", 1);
							showMessage(a.getName(), "Redo turnover");
						}
						else if (a.getCategory().equals("saves"))
						{
							db.updateStat(a.getgId(), a.getpId(), "saves", 1);
							showMessage(a.getName(), "Redo save");
						}
						else if (a.getCategory().equals("snitches"))
						{
							db.updateStat(a.getgId(), a.getpId(), "snitches", 1);
							fm.homeScore += 30;
							score.setText(fm.homeScore + " - " + fm.awayScore);
							showMessage(a.getName(), "Redo snitch catch");
						}
						else if (a.getCategory().equals("sub"))
						{
							
							PlayerDb wentIn = db.getOnePlayerRow(fm.teamName, a.getpId());
							PlayerDb wentOut = db.getOnePlayerRow(fm.teamName, a.getpIdOut());
							int wentInNum = wentIn.getOnField();
							int wentOutNum = wentOut.getOnField();
							wentIn.setOnField(wentOutNum);
							wentOut.setOnField(wentInNum);
							db.updatePlayerInfo(wentIn);
							db.updatePlayerInfo(wentOut);
							showMessage(wentIn.getLname(), "Put back in");
							populateList();
						}
					}
					fm.undoQueue.add(a);
				}
			}
		});
		
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		
		final PlayerDb player = (PlayerDb) l.getItemAtPosition(position);
		//Log.i("Test", "player on click: " + player.getFname());
		
		AlertDialog.Builder statsBuilder = statsDialog(fm.gId, player);
    statsBuilder.show();

	}

	Builder statsDialog(final int gID, final PlayerDb player)
  {
    // setup the dialog
		final Context context = getActivity();
		LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
		final View addStatView = dialogFactory.inflate(R.layout.stat_choice_dialog, null);
		final AlertDialog.Builder statsBuilder =  new AlertDialog.Builder(getActivity());
		//final AlertDialog dialog = statsBuilder.create();
		statsBuilder.setView(addStatView);
		statsBuilder.setTitle("Action");
		statsBuilder.setCancelable(true);
		
		CharSequence[] items = {"Shot", "Goal", "Assist", "Steal", "Turnover",
				"Save", "Snitch"};
		
		statsBuilder.setItems(items, new DialogInterface.OnClickListener() 
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				if (which == 0)
				{
					db.updateStat(gID, player.getPlayerId(), "shots", 1);
					showMessage(player.getLname(), "Shot");
					Action toAdd = new Action(gID, player.getPlayerId(), -1, "shots", player.getLname(), 1);
					fm.undoQueue.add(toAdd);
				}
				else if (which == 1)
				{
					List<PlayerDb> p = db.getOnFieldPlayersFromGame(fm.teamName, fm.gId);
					for (int i = 0; i < p.size(); i++)
					{
						int pid = p.get(i).getPlayerId();
						db.updateStat(fm.gId, pid, "plusses", 1);
					}
					db.updateStat(gID, player.getPlayerId(), "goals", 1);
					db.updateStat(gID, player.getPlayerId(), "shots", 1);
					//db.updateStat(gID, player.getPlayerId(), "plusses", 1);
					showMessage(player.getLname(), "Goal");
					fm.homeScore += 10;
					score.setText(fm.homeScore + " - " + fm.awayScore);
					Action toAdd = new Action(gID, player.getPlayerId(), -1, "goals", player.getLname(), 1);
					fm.undoQueue.add(toAdd);
				}
				else if (which == 2)
				{
					db.updateStat(gID, player.getPlayerId(), "assists", 1);
					showMessage(player.getLname(), "Assist");
					Action toAdd = new Action(gID, player.getPlayerId(), -1, "assists", player.getLname(), 1);
					fm.undoQueue.add(toAdd);
				}
				else if (which == 3)
				{
					db.updateStat(gID, player.getPlayerId(), "steals", 1);
					showMessage(player.getLname(), "Steal");
					Action toAdd = new Action(gID, player.getPlayerId(), -1, "steals", player.getLname(), 1);
					fm.undoQueue.add(toAdd);
				}
				else if (which == 4)
				{
					db.updateStat(gID, player.getPlayerId(), "turnovers", 1);
					showMessage(player.getLname(), "Turnover");
					Action toAdd = new Action(gID, player.getPlayerId(), -1, "turnovers", player.getLname(), 1);
					fm.undoQueue.add(toAdd);
				}
				else if (which == 5)
				{
					db.updateStat(gID, player.getPlayerId(), "saves", 1);
					showMessage(player.getLname(), "Save");
					Action toAdd = new Action(gID, player.getPlayerId(),-1, "saves", player.getLname(), 1);
					fm.undoQueue.add(toAdd);
				}
				else if (which == 6)
				{
					db.updateStat(gID, player.getPlayerId(), "snitches", 1);
					showMessage(player.getLname(), "Snitch Catch");
					fm.homeScore += 30;
					score.setText(fm.homeScore + " - " + fm.awayScore);
					Action toAdd = new Action(gID, player.getPlayerId(), -1, "snitches", player.getLname(), 1);
					fm.undoQueue.add(toAdd);
				}
				else
				{
					//do nothing
				}
			}
		});

    
  	
		
		return statsBuilder;
  }

	Builder subDialog(final int gID, final PlayerDb player)
	{
	// setup the dialog
			final Context context = getActivity();
			LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
			final View addSubView = dialogFactory.inflate(R.layout.stat_choice_dialog, null);
			//Im assuminn the layout doesn't matter right now
			final AlertDialog.Builder subBuilder =  new AlertDialog.Builder(getActivity());
			subBuilder.setView(addSubView);
			subBuilder.setTitle("Sub in:");
			subBuilder.setCancelable(true);
			
			final List<PlayerDb> list = db.getOffFieldPlayersFromGame(fm.teamName, gID);
			CharSequence[] items = new String[list.size()];
			for (int i=0; i < list.size(); i++) 
			{
			   items[i] = list.get(i).toString();
			}
			
			
			subBuilder.setItems(items, new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					int loc = player.getOnField();
					player.setOnField(0);
					db.updatePlayerInfo(player);
					list.get(which).setOnField(loc);
					db.updatePlayerInfo(list.get(which));
					Action toAdd = new Action(fm.gId, list.get(which).getPlayerId(), 
							player.getPlayerId(), "sub", list.get(which).getLname(), 1);
					fm.undoQueue.add(toAdd);
					populateList();
					
				}
			});
			return subBuilder;
	}
	
	
	public void populateList() //which list is this?
	{
		//I don't want to sort this, I want this in the same order it always is in. Or something
		List<PlayerDb> playerList = db.getOnFieldPlayersFromGame(fm.teamName, fm.gId);
		/*
    Collections.sort(playerList, new Comparator<PlayerDb>()
    {
      public int compare(PlayerDb o1, PlayerDb o2) 
      {
      		Log.i("Test", "comp");
          if (o1.getOnField() <= o2.getOnField())
          {
          	Log.i("Test", "ret 0");
          	return 1;
          }
          else
          {
          	Log.i("Test", "ret 1");
          	return 0;
          }
      }
		});
		*/
		Collections.sort(playerList, new PlayerDb.OrderByOnField());
		listAdapter = new ArrayAdapter<PlayerDb>(getActivity(), R.layout.custom_player_list, playerList);
		setListAdapter(listAdapter);
	}
	
	public void showMessage(String name, String action)
	{
		String str = action + " by " + name;
		Toast toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
}
