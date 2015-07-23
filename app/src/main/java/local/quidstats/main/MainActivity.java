package local.quidstats.main;

import local.quidstats.R;
import local.quidstats.database.DatabaseHelper;
import local.quidstats.database.TeamDb;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class MainActivity extends FragmentActivity {

    private String TAG = "MainActivity";

    DatabaseHelper db;

    ArrayAdapter<TeamDb> listAdapter;
    ArrayAdapter<TeamDb> listAdapter2;

    TeamDb teamRowList = new TeamDb("aaa-aaa-aaa", "Create new team");

    //Fragment stuff
    PagerAdapterMainPage mPagerAdapterMainPage;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_home_screen);

        //--START FRAGMENT STUFF
        mPagerAdapterMainPage = new PagerAdapterMainPage(
                getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapterMainPage);

        //--END FRAGMENT STUFF

        db = new DatabaseHelper(this);
        //db.update2();
        //db.update3();
        //db.update4();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
