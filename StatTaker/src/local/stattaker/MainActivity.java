package local.stattaker;

import java.util.ArrayList;
import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener
{
		DatabaseHelper db;
		
		Button create_team;
		Button edit_team;
		Button test_button;
		ListView currentTeams;
		
		ArrayAdapter<String> listAdapter;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        db = new DatabaseHelper(this);
        
        /*
         * This is for parse for later
        Parse.initialize(this, "RoDlI2ENBnxSWlPvdG2VEsFPRSt06qHJ78nZop77", "fbuEyPT9Exq141IZfueUO1asOcbAFaBjJvdAFI1A");
        ParseAnalytics.trackAppOpened(getIntent());
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        */
        create_team = (Button) findViewById(R.id.create_button);
        edit_team = (Button) findViewById(R.id.edit_button);
        test_button = (Button) findViewById(R.id.existing_team_1);
        create_team.setOnClickListener(this);
        edit_team.setOnClickListener(this);
        test_button.setOnClickListener(this);
        
        populateTeamsList();
        
        currentTeams = (ListView) findViewById(R.id.teams_list);
        currentTeams.setOnItemClickListener(new OnItemClickListener()
        {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) 
					{
						//now this is what happens when you click
						final String teamClicked = (String)((TextView) arg1).getText();
						AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

	  				alert.setTitle("Options");
	  				alert.setMessage("Choose One of the Following:");

	  				// Set an EditText view to get user input 
	  				alert.setPositiveButton("Create New Game", new DialogInterface.OnClickListener() 
	  				{
	  					public void onClick(DialogInterface dialog, int whichButton) 
	  					{
	  						//This needs to take you to the FragmentMain screen
	  						///Log.i("Test", "input: " + input.getText().toString());
	  						//Intent i = new Intent(getApplicationContext(), CreateTeam.class);
	  						//i.putExtra("teamName", teamClicked);
	  	          //startActivity(i);
	  				  }
	  				});
	  				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
	  				{
	  				  public void onClick(DialogInterface dialog, int whichButton) 
	  				  {
	  				  	//Cancel
	  				  }
	  				});
	  				alert.setNeutralButton("Edit Team", new DialogInterface.OnClickListener()
	  				{

							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								Intent i = new Intent(getApplicationContext(), EditTeam.class);
								i.putExtra("teamName", teamClicked);
								startActivity(i);
							}
	  					
	  				});
	  				alert.show();
					}
        	
        });
        
        
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	populateTeamsList();
    }
		@Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
		
    public void populateTeamsList() 
    {      
    	currentTeams = (ListView) findViewById(R.id.teams_list);
      List<String> teams = new ArrayList<String>();
      teams = db.getCurrentTeams();
      listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teams);
      currentTeams.setAdapter(listAdapter);
      
		}
    
		@Override
		public void onClick(View v) 
		{
  		switch(v.getId())
  		{
  			case(R.id.create_button):
  			{
  				String newTeamName;
  				AlertDialog.Builder alert = new AlertDialog.Builder(this);

  				alert.setTitle("New Team Name");
  				alert.setMessage("Enter Name of Team:");

  				// Set an EditText view to get user input 
  				final EditText input = new EditText(this);
  				alert.setView(input);

  				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
  				{
  					public void onClick(DialogInterface dialog, int whichButton) 
  					{
  						///Log.i("Test", "input: " + input.getText().toString());
  						Intent i = new Intent(getApplicationContext(), CreateTeam.class);
  						i.putExtra("teamName", input.getText().toString());
  	          startActivity(i);
  				  }
  				});

  				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
  				{
  				  public void onClick(DialogInterface dialog, int whichButton) 
  				  {
  				    // Canceled.
  				  }
  				});
  				alert.show();
  				break;
  			}
  			case(R.id.edit_button):
  			{
  				break;
  			}
  			case(R.id.existing_team_1):
  			{
  				//this is just something to use
  				break;
  			}
  		}
			
		}

}
