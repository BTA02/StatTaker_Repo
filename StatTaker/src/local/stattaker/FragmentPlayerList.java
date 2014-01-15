package local.stattaker;

import local.stattaker.helper.DatabaseHelper;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class FragmentPlayerList extends ListFragment
{
	DatabaseHelper db;
	
	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		FragmentMain fm = (FragmentMain) getActivity();
		db = db.getHelper(fm.getApplicationContext());
		Cursor test = db.getAllPlayers("Ano", 0); //points at first person, right?
		String blah = test.getString(1);
		
		//String[] playersInString = new String[50];
		/*
		Cursor cursor = db.getAllPlayers("University_Of_Michigan", 1);
		String str;
		for(int i = 0; i < cursor.getCount(); i++)
		{
			str = cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(4);
			playersInString[i] = str;
		}
		*/
		//playersInString[0] = "Andrew";
		//playersInString[1] = "Phuc";
		/*
		Cursor cursor = db.getAllPlayers("University_Of_Michigan", 1);
		String[] columns = {"number", "fname", "lname"};
		int[] tViews; //set this later, when I have textViews to set 
		SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, 
																						cursor, columns, tViews, FLAG_REGISTER_CONTENT_OBSERVER);
		*/
		String[] playersInString = {blah};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), 
				android.R.layout.simple_list_item_1, playersInString);
		setListAdapter(adapter);
		
		
		
		
		
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		
	}
	
	
}
