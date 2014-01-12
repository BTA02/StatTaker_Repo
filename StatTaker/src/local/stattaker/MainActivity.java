package local.stattaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /*
         * This is for parse for later
        Parse.initialize(this, "RoDlI2ENBnxSWlPvdG2VEsFPRSt06qHJ78nZop77", "fbuEyPT9Exq141IZfueUO1asOcbAFaBjJvdAFI1A");
        ParseAnalytics.trackAppOpened(getIntent());
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        */
        Button b = (Button) findViewById(R.id.existing_team_1);
        b.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		Intent i = new Intent(getApplicationContext(), FragmentMain.class);
        		startActivity(i);
        	}
        });
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
