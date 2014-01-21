package local.stattaker;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.PlayerDb;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FragmentPlayerList extends ListFragment
{
	DatabaseHelper db;
	
	ListView currentPlayers;
	
	FragmentMain fm;
	
	View rootView;

	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		fm = (FragmentMain) getActivity();
		
		db = db.getHelper(fm.getApplicationContext());
		
		List<PlayerDb> playerList = db.getAllPlayers(fm.teamName, 1);
    Collections.sort(playerList, new Comparator()
    {
      public int compare(Object o1, Object o2) {
          PlayerDb p1 = (PlayerDb) o1;
          PlayerDb p2 = (PlayerDb) o2;
         return p1.getFname().compareToIgnoreCase(p2.getFname());
      }

  });
		ListAdapter listAdapter = new ArrayAdapter<PlayerDb>(getActivity(), R.layout.custom_player_list, playerList);
		setListAdapter(listAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		
		final PlayerDb player = (PlayerDb) l.getItemAtPosition(position);
		//Log.i("Test", "player on click: " + player.getFname());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(true);
		builder.setTitle("Action");
		
		LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
		final View addStatView = dialogFactory.inflate(R.layout.stat_choice_dialog, null);
		builder.setView(addStatView);
		
		AlertDialog alert = builder.create();
		alert.show();
	}

	
	
}
