package local.quidstats.video;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import local.quidstats.R;
import local.quidstats.database.DatabaseHelper;
import local.quidstats.database.GameDb;
import local.quidstats.database.MetaStatDb;
import local.quidstats.database.NewActionDb;
import local.quidstats.database.PlayerDb;
import local.quidstats.main.AdvancedStats;
import local.quidstats.main.LocalTeamsFragment;

public class VideoStatsActivity extends Activity implements
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private static int HEADER_CHASERS = 0;
    private static int HEADER_KEEPER = 1;
    private static int HEADER_BEATERS = 2;

    private String mTeamId;
    private DatabaseHelper db;
    private List<String> mGamesAdded;
    private Map<String, SparseArray<List<String> > > mTimeArrays;
    private Map<String, List<NewActionDb> > mActionLists;

    private Spinner mQuerySpinner;
    private Spinner mGamesSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_stats);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            mTeamId = b.getString("teamId");
        }

        db = new DatabaseHelper(this);

        mGamesAdded = new ArrayList<>();

        mQuerySpinner = (Spinner) findViewById(R.id.video_stats_query_chooser);
        mGamesSpinner = (Spinner) findViewById(R.id.video_stats_game_chooser);

        ArrayAdapter<CharSequence> querySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.queries_array, android.R.layout.simple_spinner_item);
        querySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQuerySpinner.setAdapter(querySpinnerAdapter);
        mQuerySpinner.setOnItemSelectedListener(this);

        mTimeArrays = new HashMap<>();
        mActionLists = new HashMap<>();

        displayAllGames();
    }

    private void displayAllGames() {
        new getAllGamesTask(this).execute("hello");
    }

    private class getAllGamesTask extends AsyncTask<String, Void, List> {

        private Activity mActivity;

        private getAllGamesTask(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        protected List doInBackground(String... params) {
            List<LocalTeamsFragment.Video> vids = new ArrayList<>();
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Videos");
            query.setLimit(1000);
            query.whereEqualTo("team_id", mTeamId);
            List<ParseObject> objects = new ArrayList<ParseObject>();
            try {
                objects = query.find();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            String gameName;
            String gameId;
            for (int i = 0; i < objects.size(); i++) {
                gameId = objects.get(i).getString("vid_id");
                gameName = objects.get(i).getString("description");
                vids.add(new LocalTeamsFragment.Video(gameName, gameId));
            }
            return vids;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            GridLayout allGamesLayout = (GridLayout) findViewById(R.id.video_stats_all_games);
            List<LocalTeamsFragment.Video> allGames = list;
            for(LocalTeamsFragment.Video vid : allGames) {
                // Add each game here
                Switch newSwitch = new Switch(getApplicationContext());
                newSwitch.setChecked(false);
                newSwitch.setText(vid.description);
                newSwitch.setTag(vid.id);

                newSwitch.setOnCheckedChangeListener(
                        (CompoundButton.OnCheckedChangeListener) mActivity);
                newSwitch.setPadding(10, 10, 10, 10);
                newSwitch.setTextColor(Color.BLACK);
                allGamesLayout.addView(newSwitch);
            }
        }
    }










    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String gameId = (String) buttonView.getTag();
        if (isChecked) {
            mGamesAdded.add(gameId);
        } else {
            mGamesAdded.remove(gameId);
        }
        calcAndDispStats();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}



    private void calcAndDispStats() {
        String curSelection = (String) mQuerySpinner.getSelectedItem();
        if (curSelection.equals("Best lineups")) {
            calcPlusMinusStat(mGamesAdded, new int[][]{{0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5},
                    {0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5}, {0, 1, 2, 3, 4, 5}});
        } else if (curSelection.equals("Best beater pairs")) {
            calcPlusMinusStat(mGamesAdded, new int[][]{{4,5},{4,5}});
        } else if (curSelection.equals("Best quaffle players")) {
            calcPlusMinusStat(mGamesAdded, new int[][] {{0,1,2,3},{0,1,2,3},{0,1,2,3},{0,1,2,3}});
        } else if (curSelection.equals("Best quaffle pairs")) {
            calcPlusMinusStat(mGamesAdded, new int[][]{{0,1,2,3},{0,1,2,3}});
        } else if (curSelection.equals("Best chaser / beater pair")) {
            calcPlusMinusStat(mGamesAdded, new int[][]{{0,1,2,3},{4,5}});
        } else if (curSelection.equals("Best pair")) {
            calcPlusMinusStat(mGamesAdded, new int[][] {{0,1,2,3,4,5},{0,1,2,3,4,5}});
        } else if (curSelection.equals("Individual plus / minus")) {
            calcPlusMinusStat(mGamesAdded, new int[][]{{0,1,2,3,4,5}});
        }
    }

    private void calcPlusMinusStat(List<String> games, int[][] arrs) {
        final LinearLayout statsParent = (LinearLayout) findViewById(R.id.video_stats_parent);
        statsParent.removeAllViews();

        AsyncTask task = new CalcPlusMinusTask(games, arrs, statsParent);
        task.execute(null);
    }


    private class CalcPlusMinusTask extends AsyncTask<Object, Void, HashMap> {

        private List<String> games;
        private int[][] arrs;
        private LinearLayout statsParent;

        public CalcPlusMinusTask(List<String> g, int[][] a, LinearLayout s) {
            games = g;
            arrs = a;
            statsParent = s;
        }

        @Override
        protected HashMap doInBackground(Object... params) {
            // Map of the plus minuses for each pair
            HashMap<List<Pair<String, Integer>>, Pair<Integer, Integer>> plusMinusMap = new HashMap<>();
            // Loop through selected games
            for (int k = 0; k < games.size(); k++) {
                // Get game info
                String gameId = games.get(k);
                SparseArray<List<String>> timeArray = getTimeArray(gameId);
                List<NewActionDb> newActions = getAllActions(gameId);

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
                for (NewActionDb action : newActions) {
                    int actionTime = action.getYoutubeTime() / 1000;
                    List<String> lineUp = timeArray.get(actionTime);
                    if (!(action.getActualAction() == NewActionDb.NewAction.GOAL) &&
                            !(action.getActualAction() == NewActionDb.NewAction.AWAY_GOAL)) {
                        continue;
                    }
                    lineUp = AdvancedStats.sortByPosition(lineUp);

                    for (List<Integer> combo : allCombos) {
                        // Sublineup is the lineup I am looking at to evaluate
                        // EX: Axtell, 0
                        //     ZSchepers, 2
                        //     Witt, 2
                        List<Pair<String, Integer>> subLineup = new ArrayList<>();
                        for (Integer ii : combo) {
                            Pair temp = new Pair(lineUp.get(ii),
                                    AdvancedStats.getPositionFromArrPosition(ii));
                            subLineup.add(temp);
                        }
                        // Get the plus/minus value of the given combo
                        Pair<Integer, Integer> p = plusMinusMap.get(subLineup);
                        if (p == null) {
                            p = new Pair(0, 0);
                        }
                        Integer pos = p.first;
                        Integer neg = p.second;
                        if (action.getActualAction() == NewActionDb.NewAction.GOAL) {
                            Pair toPut = new Pair(++pos, neg);
                            plusMinusMap.put(subLineup, toPut);
                        } else if (action.getActualAction() == NewActionDb.NewAction.AWAY_GOAL) {
                            Pair toPut = new Pair(pos, ++neg);
                            plusMinusMap.put(subLineup, toPut);
                        }
                    }
                }
            }
            return plusMinusMap;
        }

        @Override
        protected void onPostExecute(HashMap hashMap) {
            super.onPostExecute(hashMap);
            HashMap<List<Pair<String, Integer>>, Pair<Integer, Integer>> map = hashMap;
            displayStatMap(map, statsParent);
        }


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

    private List<List<Pair<String, Integer> > > sortKeys(HashMap<List<Pair<String, Integer> >,
            Pair<Integer, Integer> > map) {

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


    // Build the entire "timeArray", or get it from the map
    private SparseArray<List<String>> getTimeArray(String gameId) {
        if (mTimeArrays.get(gameId) != null) {
            return mTimeArrays.get(gameId);
        }
        SparseArray<List<String> > timeArray = new SparseArray<>();
        // Second 0 is where "game start" happens
        List<NewActionDb> actions = db.getAllActionsFromGame(gameId);

        List<String> onFieldPlayers = getDummyLineupIds();
        int lastSub = 0;

        for (NewActionDb action : actions) {
            int t = action.getYoutubeTime() / 1000;
            if (action.getActualAction() == NewActionDb.NewAction.GAME_START) {
                lastSub = t;
            }

            switch (action.getActualAction()) {
                case SUB:
                    // Give the players on the field their due
                    String str = "";
                    List<String> toStore = new ArrayList<>();
                    toStore.addAll(onFieldPlayers);
                    for (int i = lastSub; i < t; i++) {
                        timeArray.put(i, toStore);
                    }
                    // Sub out the player with their sub
                    onFieldPlayers.set(action.getLoc(), action.getPlayerIn());
                    lastSub = t;
                    break;
                case GAME_END:
                    for (int i = lastSub; i < t; i++) {
                        timeArray.put(i, onFieldPlayers);
                    }
                    break;
            }
        }
        mTimeArrays.put(gameId, timeArray);
        return timeArray;
    }

    private List<NewActionDb> getAllActions(String gameId) {
        if (mActionLists.get(gameId) != null) {
            return mActionLists.get(gameId);
        }
        return db.getAllActionsFromGame(gameId);
    }

    private List<String> getDummyLineupIds() {
        List<String> dummy = new ArrayList<>();
        dummy.add("a");
        dummy.add("b");
        dummy.add("c");
        dummy.add("d");
        dummy.add("e");
        dummy.add("f");
        return dummy;
    }









}

