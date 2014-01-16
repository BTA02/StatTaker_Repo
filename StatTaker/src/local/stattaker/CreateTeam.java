package local.stattaker;

import local.stattaker.helper.DatabaseHelper;
import local.stattaker.model.PlayerDb;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CreateTeam extends Activity 
{
	DatabaseHelper db;
	
	String newName;
	String number;
	String fname;
	String lname;
	
	TextView teamName;
	
	EditText nEdit;
	EditText fEdit;
	EditText lEdit;
	
	CheckBox active;
	
	Button addPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_team);
		
		db = db.getHelper(this);
		
		teamName = (TextView) findViewById(R.id.create_team_name);
		addPlayer = (Button) findViewById(R.id.create_add_player);
		nEdit = (EditText) findViewById(R.id.create_number);
		fEdit = (EditText) findViewById(R.id.create_fname);
		lEdit = (EditText) findViewById(R.id.create_lname);
		active = (CheckBox) findViewById(R.id.create_active_box);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) //something was input
		{
			newName = extras.getString("teamName");
			
		}
		else //nothing found
		{
			Log.e("Error", "no new name found");
			this.finish();
		}
		
		//now I have a team name...
		
		teamName.setText(newName);
		addPlayer.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				PlayerDb p = new PlayerDb();
				number = nEdit.getText().toString();
				fname = fEdit.getText().toString();
				lname = lEdit.getText().toString();
				p.setTeamName(newName);
				p.setNumber(number);
				p.setFname(fname);
				p.setLname(lname);
				if (active.isChecked())
				{
					p.setActive(1);
				}
				else
				{
					p.setActive(0);
				}
				db.addPlayer(p);
				nEdit.setText(null);
				fEdit.setText(null);
				lEdit.setText(null);
				active.setChecked(false);
				CharSequence toastMsg = fname + " " + lname + " added to " + newName;
				Toast toast = Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
				toast.show();
			}
			
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_team, menu);
		return true;
	}

}
