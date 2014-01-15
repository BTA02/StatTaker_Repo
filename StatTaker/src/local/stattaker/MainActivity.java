package local.stattaker;

import java.util.ArrayList;
import java.util.List;

import local.stattaker.helper.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
        
        currentTeams = (ListView) findViewById(R.id.teams_list);
        
        populateTeamsList();
        

        
        
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
