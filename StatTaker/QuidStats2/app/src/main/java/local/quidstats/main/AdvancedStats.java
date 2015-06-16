package local.quidstats.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import local.quidstats.R;
import local.quidstats.database.DatabaseHelper;
import local.quidstats.database.GameDb;
import local.quidstats.database.MetaStatDb;
import local.quidstats.database.PlayerDb;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class AdvancedStats extends Activity implements
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private static int HEADER_CHASERS = 0;
    private static int HEADER_KEEPER = 1;
    private static int HEADER_BEATERS = 2;

    private String mTeamId;
	private DatabaseHelper db;
    private List<GameDb> mGamesAdded;
    ArrayAdapter<GameDb> mQueryGamesAdapter;
    private String mCurStat;

    private Spinner mQuerySpinner;
    private Spinner mGamesSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advanced_stats);

		Bundle b = getIntent().getExtras();
		mTeamId = b.getString("teamId");
		
		db = new DatabaseHelper(this);

        mGamesAdded = new ArrayList<>();

        mQuerySpinner = (Spinner) findViewById(R.id.query_chooser);
        mGamesSpinner = (Spinner) findViewById(R.id.game_chooser);

        ArrayAdapter<CharSequence> querySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.queries_array, android.R.layout.simple_spinner_item);
        querySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQuerySpinner.setAdapter(querySpinnerAdapter);
        mQuerySpinner.setOnItemSelectedListener(this);

        displayAllGames();

        //populateGamesSpinner();
		
	}

    private void populateGamesSpinner(){
        ArrayAdapter<CharSequence> gameSpinnerAdapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_dropdown_item);

        mGamesSpinner.setAdapter(gameSpinnerAdapter);
        mGamesSpinner.setOnItemSelectedListener(this);
        List<GameDb> allGames = db.getAllGamesForTeam(mTeamId);
        gameSpinnerAdapter.add("Add game to stats");
        for(int i = 0; i < allGames.size(); i++) {
            gameSpinnerAdapter.add(allGames.get(i).getAwayTeam());
        }


    }

    private void displayAllGames() {
        GridLayout allGamesLayout = (GridLayout) findViewById(R.id.advanced_stats_all_games);
        List<GameDb> allGames = db.getAllGamesForTeam(mTeamId);
        for(int i = 0; i < allGames.size(); i++) {
            // Add each game here
            GameDb curGame = allGames.get(i);
            Switch newSwitch = new Switch(this);
            newSwitch.setChecked(false);
            newSwitch.setText(curGame.getAwayTeam());
            newSwitch.setTag(curGame.getId());

            newSwitch.setOnCheckedChangeListener(this);
            newSwitch.setPadding(10, 10, 10, 10);
            allGamesLayout.addView(newSwitch);
        }
    }

    private void calcAndDispStats() {
        String curSelection = (String) mQuerySpinner.getSelectedItem();
        if (curSelection.equals("Best lineups")) {
            calcAndDispTopLineup(mGamesAdded);
        } else if (curSelection.equals("Best beater pairs")) {
            calcAndDispTopBeatePairs(mGamesAdded);
        } else if (curSelection.equals("Best quaffle players")) {
            calcAndDispTopQuaffle(mGamesAdded);
        } else if (curSelection.equals("Best quaffle pairs")) {
            calcAndDispTopQuafflePairs(mGamesAdded);
        } else if (curSelection.equals("Best chaser / beater pair")) {
            calcAndDispTopBeaterChaser(mGamesAdded);
        } else if (curSelection.equals("Best pair")) {
            calcAndDispTopPair(mGamesAdded);
        }
    }

    private List<String> sortByPosition(List<String> playerIds) {
        List<String> retArray = new ArrayList<>();

        List<String> chasers = playerIds.subList(0, 3);
        String keeper = playerIds.get(3);
        List<String> beaters = playerIds.subList(4, 6);

        Collections.sort(chasers);
        Collections.sort(beaters);

        retArray.addAll(chasers);
        retArray.add(keeper);
        retArray.addAll(beaters);

        return retArray;
    }


    private void displayStatMap(HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer>> map,
                                LinearLayout statsParent) {
        // Displaying just got a lot easier
        // Do this in the background
        statsParent.removeAllViews();

        boolean chaserHeader = false;
        boolean beaterHeader = false;
        boolean keeperHeader = false;

        List<List<Pair<String, Integer>>> sortedKeys = sortKeys(map);
        for (List<Pair<String, Integer>> entry : sortedKeys) {
            for (Pair<String, Integer> pair : entry) {
                PlayerDb p = db.getPlayerById(pair.first);
                int pos = pair.second;
                if (p != null) {
                    switch (pos) {
                        case 0:
                            if (!chaserHeader) {
                                displaySectionHeader(0, statsParent);
                                chaserHeader = true;
                            }
                            break;
                        case 1:
                            if (!keeperHeader) {
                                displaySectionHeader(1, statsParent);
                                keeperHeader = true;
                            }
                            break;
                        case 2:
                            if (!beaterHeader) {
                                displaySectionHeader(2, statsParent);
                                beaterHeader = true;
                            }
                            break;
                    }
                    TextView playerTV = new TextView(this);
                    playerTV.setText(p.getFname() + " " + p.getLname());
                    statsParent.addView(playerTV);
                }
            }
            TextView plusStatVal = new TextView(this);
            plusStatVal.setText("+" + map.get(entry).first.toString());
            statsParent.addView(plusStatVal);

            TextView minusStatVal = new TextView(this);
            minusStatVal.setText("-" + map.get(entry).second.toString());
            statsParent.addView(minusStatVal);

            int total = map.get(entry).first - map.get(entry).second;
            TextView totalStatVal = new TextView(this);
            if (total > 0) {
                totalStatVal.setText("+" + String.valueOf(total));
            } else {
                totalStatVal.setText(String.valueOf(total));
            }
            statsParent.addView(totalStatVal);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, 1);
            View divider = new View(this);
            divider.setLayoutParams(params);
            divider.setBackgroundColor(getResources().getColor(R.color.quid_stats_red));
            statsParent.addView(divider);
            chaserHeader = false;
            keeperHeader = false;
            beaterHeader = false;
        }

    }

    private void displaySectionHeader(int section, LinearLayout statsParent) {
        TextView header = new TextView(this);
        if (section == 0) {
            header.setText("Chasers");
        } else if (section == 1) {
            header.setText("Keeper");
        } else if (section == 2) {
            header.setText("Beaters");
        }
        header.setTypeface(null, Typeface.BOLD);
        statsParent.addView(header);
    }

    private List<List<Pair<String, Integer> > > sortKeys(HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map) {

        List<List<Pair<String, Integer> > > sortedKeys = new ArrayList<>();
        for (List<Pair<String, Integer> > key : map.keySet()) {
            int val = map.get(key).first - map.get(key).second;
            int index = 0;
            for (List<Pair<String, Integer> > sortedKey : sortedKeys) {
                int oldVal = map.get(sortedKey).first - map.get(sortedKey).second;
                if (oldVal > val) {
                    index++;
                } else {
                    break;
                }
            }
            sortedKeys.add(index, key);
        }

        return sortedKeys;
    }

    private HashMap calcStatFromMap(List<GameDb> games, int[][] arrs) {
        final LinearLayout statsParent = (LinearLayout) findViewById(R.id.advanced_stats_parent);
        statsParent.removeAllViews();

        AsyncTask task = new CalcTask(games, arrs, statsParent);
        task.execute(null);
        return null;
    }

    private class CalcTask extends AsyncTask<Object, Void, HashMap>{

        private List<GameDb> games;
        private int[][] arrs;
        private LinearLayout statsParent;

        public CalcTask(List<GameDb> g, int[][] a, LinearLayout s) {
            games = g;
            arrs = a;
            statsParent = s;
        }

        @Override
        protected HashMap doInBackground(Object... params) {
            HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map = new HashMap<>();
            // Loop through selected games
            for(int k = 0; k < games.size(); k++) {
                // Get game info
                String gameId = games.get(k).getId();
                SparseArray<List<String>> timeArray = db.getGameInfo(gameId).getTimeArray();
                List<MetaStatDb> metaStats = db.getAllMetaStats(gameId);

                // Generate all combos of positions
                Set<List<Integer>> allCombos = new HashSet<>();
                int solutions = 1;
                for (int i = 0; i < arrs.length; solutions *= arrs[i].length, i++) ;
                for (int i = 0; i < solutions; i++) {
                    List<Integer> newList = new ArrayList<>();
                    int j = 1;
                    for (int[] arr : arrs) {
                        newList.add(arr[(i / j) % arr.length]);
                        j *= arr.length;
                    }
                    Collections.sort(newList);
                    Set testSet = new HashSet(newList);
                    if (testSet.size() == arrs.length) {
                        allCombos.add(newList);
                    }
                }

                // Loop through each game action
                for (MetaStatDb action : metaStats) {
                    List<String> lineUp = timeArray.get(action.getTimeOfAction());
                    lineUp = sortByPosition(lineUp);

                    for (List<Integer> combo : allCombos) {
                        // Sublineup is the lineup I am looking at to evaluate
                        // EX: Axtell, 0
                        //     ZSchepers, 2
                        //     Witt, 2
                        List<Pair<String, Integer>> subLineup = new ArrayList<>();
                        for (Integer ii : combo) {
                            Pair temp = new Pair(lineUp.get(ii), getPositionFromArrPosition(ii));
                            subLineup.add(temp);
                        }
                        // Get the plus/minus value of the given combo
                        Pair<Integer, Integer> p = map.get(subLineup);
                        if (p == null) {
                            p = new Pair(0, 0);
                        }
                        Integer pos = p.first;
                        Integer neg = p.second;
                        if (action.getStatType().equals(DatabaseHelper.COL_GOALS)) {
                            Pair toPut = new Pair(++pos, neg);
                            map.put(subLineup, toPut);
                        } else if (action.getStatType().equals("away_goal")) {
                            Pair toPut = new Pair(pos, ++neg);
                            map.put(subLineup, toPut);
                        }
                    }
                }
            }
            return map;
        }

        @Override
        protected void onPostExecute(HashMap hashMap) {
            super.onPostExecute(hashMap);
            HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map = hashMap;
            displayStatMap(map, statsParent);
        }
    }



    private static int getPositionFromArrPosition(int i) {
        if (i == 0 || i == 1 || i == 2) {
            return 0;
        } else if (i == 3) {
            return 1;
        } else if (i == 4 || i == 5) {
            return 2;
        }
        return 0; // by default return chaser
    }

	private void calcAndDispTopQuafflePairs(List<GameDb> games) {
        //LinearLayout statsParent = (LinearLayout) findViewById(R.id.advanced_stats_parent);
        //statsParent.removeAllViews();

        HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map = new HashMap<>();
        map = calcStatFromMap(games, new int[][]{{0,1,2,3},{0,1,2,3}});
        //displayStatMap(map, statsParent);
    }


    private void calcAndDispTopBeatePairs(List<GameDb> games)
    {
        //LinearLayout statsParent = (LinearLayout) findViewById(R.id.advanced_stats_parent);
        //statsParent.removeAllViews();

        HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map = new HashMap<>();
        map = calcStatFromMap(games, new int[][] {{4,5},{4,5}});
        //displayStatMap(map, statsParent);
    }

	private void calcAndDispTopQuaffle(List<GameDb> games)
    {
        //LinearLayout statsParent = (LinearLayout) findViewById(R.id.advanced_stats_parent);
        //statsParent.removeAllViews();

        HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map = new HashMap<>();
        map = calcStatFromMap(games, new int[][] {{0,1,2,3},{0,1,2,3},{0,1,2,3},{0,1,2,3}});
        //displayStatMap(map, statsParent);
    }

	private void calcAndDispTopLineup(List<GameDb> games)
	{

        HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map = new HashMap<>();
        calcStatFromMap(games, new int[][]{{0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5},
                {0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5}});
        //displayStatMap(map, statsParent);
	}

    private void calcAndDispTopBeaterChaser(List<GameDb> games) {
        //LinearLayout statsParent = (LinearLayout) findViewById(R.id.advanced_stats_parent);
        //statsParent.removeAllViews();

        HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map = new HashMap<>();
        map = calcStatFromMap(games, new int[][] {{0,1,2},{4,5},{4,5}});
        //displayStatMap(map, statsParent);
    }

    private void calcAndDispTopPair(List<GameDb> games) {
        //LinearLayout statsParent = (LinearLayout) findViewById(R.id.advanced_stats_parent);
        //statsParent.removeAllViews();

        HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer> > map = new HashMap<>();
        map = calcStatFromMap(games, new int[][] {{0,1,2,3,4,5}, {0,1,2,3,4,5}});
        //displayStatMap(map, statsParent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //mAdapter.clear();
        //mAdapter.notifyDataSetInvalidated();
        switch (parent.getId()) {
            case R.id.query_chooser:
                CharSequence s = (CharSequence) parent.getItemAtPosition(position);
                if (s.toString().equals("Best lineups")) {
                    //mCurStat = "Best lineups";
                } else if (s.toString().equals("Best beater pairs")) {
                    //mCurStat = "Best beater pairs";
                } else if (s.toString().equals("Best quaffle players")) {
                    //calcAndDispTopQuaffle(mGamesAdded);
                } else if (s.toString().equals("Best quaffle pairs")) {
                    //calcAndDispTopQuafflePairs(mGamesAdded);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String gameId = (String) buttonView.getTag();
        GameDb game = db.getGameInfo(gameId);
        if (isChecked) {
            mGamesAdded.add(game);
        } else {
            mGamesAdded.remove(game);
        }
        calcAndDispStats();
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
