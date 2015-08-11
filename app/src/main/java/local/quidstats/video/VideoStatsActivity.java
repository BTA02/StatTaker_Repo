package local.quidstats.video;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import local.quidstats.database.NewActionDb;
import local.quidstats.database.PlayerDb;
import local.quidstats.database.SeekerStats;
import local.quidstats.database.StatDb;
import local.quidstats.main.AdvancedStats;
import local.quidstats.main.LocalTeamsFragment;
import local.quidstats.util.MapUtil;

public class VideoStatsActivity extends Activity implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private String mTeamId;
    private DatabaseHelper db;
    private List<String> mGamesAdded;
    private Map<String, SparseArray<List<String> > > mTimeArrays;
    private Map<String, List<NewActionDb> > mActionLists;

    private Spinner mQuerySpinner;
    private LinearLayout mStatsLinearLayout;
    private LinearLayout mTopLinearLayout;
    private boolean fullScreenMode = false;

    private AsyncTask mPlusMinusTask;
    private AsyncTask<Object, Void, Map<String, StatDb>> mRawStatsTask;
    private AsyncTask mSeekerTask;

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

        mTopLinearLayout = (LinearLayout) findViewById(R.id.video_stats_selector_layout);
        mStatsLinearLayout = (LinearLayout) findViewById(R.id.video_stats_parent);
        mStatsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreenMode = !fullScreenMode;
                if (fullScreenMode) {
                    mTopLinearLayout.setVisibility(View.GONE);
                } else {
                    mTopLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        mQuerySpinner = (Spinner) findViewById(R.id.video_stats_query_chooser);
        Button uploadButton = (Button) findViewById(R.id.video_stats_upload);
        uploadButton.setOnClickListener(this);

        ArrayAdapter<CharSequence> querySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.queries_array, android.R.layout.simple_spinner_item);
        querySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQuerySpinner.setAdapter(querySpinnerAdapter);
        mQuerySpinner.setOnItemSelectedListener(this);

        mTimeArrays = new HashMap<>();
        mActionLists = new HashMap<>();

        displayAllGames();
    }

    private LinearLayout getStatsParent() {
        if (mStatsLinearLayout == null) {
            mStatsLinearLayout = (LinearLayout) findViewById(R.id.video_stats_parent);
            mStatsLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullScreenMode = !fullScreenMode;
                    if (fullScreenMode) {
                        mTopLinearLayout.setVisibility(View.GONE);
                    } else {
                        mTopLinearLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        return mStatsLinearLayout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_stats_upload:
                final AlertDialog.Builder uploadBuilder = new AlertDialog.Builder(this);
                final EditText editText = new EditText(this);
                uploadBuilder.setView(editText);
                uploadBuilder.setTitle(getResources().getString(R.string.enter_auth_code));
                uploadBuilder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Background thread for upload
                        UploadTask task = new UploadTask();
                        if (mGamesAdded.size() != 1) {
                            Toast.makeText(getBaseContext(),
                                    "Please select one game", Toast.LENGTH_SHORT).show();
                        }
                        task.execute(mGamesAdded.get(0), editText.getText().toString());

                    }
                });
                uploadBuilder.setNegativeButton("Cancel", null);
                uploadBuilder.show();
                break;
        }
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
            Collections.sort(vids);
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
        calcAndDispStats();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}



    private void calcAndDispStats() {
        cancelPreviousTasks();
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
        } else if (curSelection.equals("Raw stats")) {
            calcRawStats();
        } else if (curSelection.equals("Quaffle trios")) {
            calcPlusMinusStat(mGamesAdded, new int[][]{{0, 1, 2, 3}, {0, 1, 2, 3}, {0, 1, 2, 3}});
        } else if (curSelection.equals("Seeker performance")) {
            calcSeekerPerformance();
        }
    }

    private void cancelPreviousTasks() {
        if (mPlusMinusTask != null) {
            mPlusMinusTask.cancel(true);
        }
        if (mRawStatsTask != null) {
            mRawStatsTask.cancel(true);
        }
        if (mSeekerTask != null) {
            mSeekerTask.cancel(true);
        }
    }

    private void calcRawStats() {
        if (mRawStatsTask != null) {
            mRawStatsTask.cancel(true);
        }
        mRawStatsTask = new CalcRawStatsTask(mGamesAdded);
        mRawStatsTask.execute(new Object());
    }

    private void calcPlusMinusStat(List<String> games, int[][] arrs) {
        final LinearLayout statsParent = (LinearLayout) findViewById(R.id.video_stats_parent);
        statsParent.removeAllViews();

        if (mPlusMinusTask != null) {
            mPlusMinusTask.cancel(true);
        }
        new CalcPlusMinusTask(games, arrs, statsParent).execute(new Object());
    }

    private void calcSeekerPerformance() {
        if (mSeekerTask != null) {
            mSeekerTask.cancel(true);
        }
        new CalcSeekerTask().execute();
    }

    private class CalcPlusMinusTask extends AsyncTask<Object, Void, HashMap> {
        private List<String> games;
        private int[][] arrs;
        private LinearLayout statsParent;
        private HashMap<List<Pair<String, Integer>>, Integer> mTimeOfGroupMap;

        public CalcPlusMinusTask(List<String> g, int[][] a, LinearLayout s) {
            games = g;
            arrs = a;
            statsParent = s;
        }

        @Override
        protected HashMap doInBackground(Object... params) {
            // Map of the plus minuses for each pair
            HashMap<List<Pair<String, Integer>>, Pair<Integer, Integer>> plusMinusMap = new HashMap<>();
            // Map of on field time
            HashMap<List<Pair<String, Integer>>, Integer> timeOfGroupMap = new HashMap<>();

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
                Set<Integer> testSet = new HashSet<Integer>(newList);
                if (testSet.size() == arrs.length) {
                    allCombos.add(newList);
                }
            }

            // Loop through selected games
            for (int k = 0; k < games.size(); k++) {
                // Get game info
                String gameId = games.get(k);
                SparseArray<List<String>> timeArray = getTimeArray(gameId);
                List<NewActionDb> newActions = getAllActions(gameId);
                
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
                        List<Pair<String, Integer>> subLineup = new ArrayList<>();
                        for (Integer ii : combo) {
                            Pair<String, Integer> temp = new Pair<String, Integer>(lineUp.get(ii),
                                    AdvancedStats.getPositionFromArrPosition(ii));
                            subLineup.add(temp);
                        }
                        // Get the plus/minus value of the given combo
                        Pair<Integer, Integer> p = plusMinusMap.get(subLineup);
                        if (p == null) {
                            p = new Pair<Integer, Integer>(0, 0);
                        }
                        Integer pos = p.first;
                        Integer neg = p.second;
                        if (action.getActualAction() == NewActionDb.NewAction.GOAL) {
                            Pair<Integer, Integer> toPut = new Pair<Integer, Integer>(++pos, neg);
                            plusMinusMap.put(subLineup, toPut);
                        } else if (action.getActualAction() == NewActionDb.NewAction.AWAY_GOAL) {
                            Pair<Integer, Integer> toPut = new Pair<Integer, Integer>(pos, ++neg);
                            plusMinusMap.put(subLineup, toPut);
                        }
                    }
                }
                boolean paused = true;
                for (int i = 0; i < timeArray.size()+1; i++) {

                    List<String> lineUp = timeArray.get(i);
                    if (lineUp == null) {
                        continue;
                    }
                    int curPaused = db.pauseAction(gameId + mTeamId, i);
                    if (curPaused == -1) {
                        paused = true;
                    } else if (curPaused == 1) {
                        paused = false;
                    }
                    if (paused) {
                        continue;
                    }
                    lineUp = AdvancedStats.sortByPosition(lineUp);
                    for (List<Integer> combo : allCombos) {
                        List<Pair<String, Integer>> subLineup = new ArrayList<>();
                        for (Integer ii : combo) {
                            Pair<String, Integer> temp = new Pair<String, Integer>(lineUp.get(ii),
                                    AdvancedStats.getPositionFromArrPosition(ii));
                            subLineup.add(temp);
                        }
                        // Get the plus/minus value of the given combo
                        Integer timeInt = timeOfGroupMap.get(subLineup);
                        if (timeInt == null) {
                            timeInt = 0;
                        }
                        timeInt++;
                        timeOfGroupMap.put(subLineup, timeInt);
                    }

                }

            }
            mTimeOfGroupMap = timeOfGroupMap;
            return plusMinusMap;
        }

        @Override
        protected void onPostExecute(HashMap hashMap) {
            super.onPostExecute(hashMap);
            HashMap<List<Pair<String, Integer>>, Pair<Integer, Integer>> map = hashMap;
            displayStatMap(map, statsParent, mTimeOfGroupMap);
        }


    }

    private HashMap<List<Pair<String, Integer>>, Integer> calcTimeForGroup(
            HashMap<List<Pair<String, Integer>>, Integer> curMap,
            SparseArray<List<String>> timeArray,
            Set<List<Integer>> allCombos, String gameId) {
        HashMap<List<Pair<String, Integer>>, Integer> newMap =
                new HashMap<List<Pair<String, Integer>>, Integer>(curMap);
        boolean paused = true;
        for (int i = 0; i < timeArray.size(); i++) {
            List<String> lineUp = timeArray.get(i);
            if (lineUp == null) {
                continue;
            }
            int curPaused = db.pauseAction(gameId + mTeamId, i);
            if (curPaused == -1) {
                paused = true;
            } else if (curPaused == 1) {
                paused = false;
            }
            if (paused) {
                continue;
            }
            lineUp = AdvancedStats.sortByPosition(lineUp);
            for (List<Integer> combo : allCombos) {
                List<Pair<String, Integer>> subLineup = new ArrayList<>();
                for (Integer ii : combo) {
                    Pair<String, Integer> temp = new Pair<String, Integer>(lineUp.get(ii),
                            AdvancedStats.getPositionFromArrPosition(ii));
                    subLineup.add(temp);
                }
                // Get the plus/minus value of the given combo
                Integer timeInt = curMap.get(subLineup);
                if (timeInt == null) {
                    timeInt = 0;
                }
                timeInt++;
                curMap.put(subLineup, timeInt);
            }

        }
        return newMap;
    }

    private void displayStatMap(HashMap<List<Pair<String, Integer> >, Pair<Integer, Integer>> map,
                                LinearLayout statsParent,
                                HashMap<List<Pair<String, Integer> >, Integer> timeOfGroupMap) {
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

            if (timeOfGroupMap.get(entry) == null) {
                continue;
            }
            int timeForGroup = timeOfGroupMap.get(entry);
            TextView totalTime = new TextView(this);
            String pretty = getPrettyTimeFromSeconds(timeForGroup);
            totalTime.setText(pretty);
            statsParent.addView(totalTime);

            displayDivider(statsParent);
            chaserHeader = false;
            keeperHeader = false;
            beaterHeader = false;
        }

    }

    private void displayDivider(LinearLayout statsParent) {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, 1);
        View divider = new View(this);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getResources().getColor(R.color.quid_stats_red));
        statsParent.addView(divider);
    }

    public static String getPrettyTimeFromSeconds(int seconds) {
        int dispMinutes = seconds / 60;
        int dispSeconds = seconds % 60;
        String ret;
        if (dispSeconds < 10) {
            return (dispMinutes + ":0" + dispSeconds);
        } else {
            return (dispMinutes + ":" + dispSeconds);
        }
    }

    public static String getPrettyTimeFromMilliseconds(int milliseconds) {
        return getPrettyTimeFromSeconds(milliseconds / 1000);
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

    // <editor-fold desc="Raw stats functions">

    private class CalcRawStatsTask extends AsyncTask<Object, Void, Map<String, StatDb>> {

        private List<String> games;

        private CalcRawStatsTask(List<String> games) {
            this.games = games;
        }

        @Override
        protected Map<String, StatDb> doInBackground(Object... params) {
            Map<String, StatDb> statMap = new HashMap<>();
            for (String game : games) {
                List<NewActionDb> actions = getAllActions(game);
                String[] onField = new String[]{"a","b","c","d","e","f","g"};

                int startTime = -1;
                // I think what's happening is that I never add the new "fullStats" object
                // to the statMab
                for (NewActionDb action : actions ) {
                    StatDb fullStats = new StatDb();
                    if (!action.getPlayerOut().isEmpty()) {
                        fullStats = statMap.get(action.getPlayerOut());
                        if (fullStats == null) {
                            fullStats = new StatDb(action.getPlayerOut());
                        }
                    } else if (!action.getPlayerIn().isEmpty()) {
                        fullStats = statMap.get(action.getPlayerIn());
                        if (fullStats == null) {
                            fullStats = new StatDb(action.getPlayerIn());
                        }
                    }
                    switch(action.getActualAction()) {
                        case SHOT:
                            fullStats.setShots(fullStats.getShots() + 1);
                            statMap.put(action.getPlayerOut(), fullStats);
                            break;
                        case GOAL:
                            fullStats.setGoals(fullStats.getGoals() + 1);
                            fullStats.setShots(fullStats.getShots() + 1);
                            statMap.put(action.getPlayerOut(), fullStats);
                            for(int i = 0; i < onField.length-1; i++) {
                                String pId = onField[i];
                                StatDb s = statMap.get(pId);
                                if (s == null) {
                                    s = new StatDb(pId);
                                }
                                s.setPlusses(s.getPlusses() + 1);
                                statMap.put(pId, s);
                            }
                            break;
                        case ASSIST:
                            fullStats.setAssists(fullStats.getAssists() + 1);
                            statMap.put(action.getPlayerOut(), fullStats);
                            break;
                        case TURNOVER:
                            fullStats.setTurnovers(fullStats.getTurnovers() + 1);
                            statMap.put(action.getPlayerOut(), fullStats);
                            break;
                        case TAKEAWAY:
                            fullStats.setTakeaways(fullStats.getTakeaways() + 1);
                            statMap.put(action.getPlayerOut(), fullStats);
                            break;
                        case YELLOW_CARD:
                            fullStats.setYellows(fullStats.getYellows() + 1);
                            statMap.put(action.getPlayerOut(), fullStats);
                            break;
                        case RED_CARD:
                            fullStats.setReds(fullStats.getReds() + 1);
                            statMap.put(action.getPlayerOut(), fullStats);
                            break;
                        case AWAY_GOAL:
                            for(int i = 0; i < onField.length-1; i++) {
                                String pId = onField[i];
                                StatDb s = statMap.get(pId);
                                if (s == null) {
                                    s = new StatDb(pId);
                                }
                                s.setMinuses(s.getMinuses() + 1);
                                statMap.put(pId, s);
                            }
                            break;
                        case SUB:
                            if (startTime != -1) {
                                int totalTime = action.getYoutubeTime() - startTime;
                                addTimeToEachPlayer(statMap, onField, totalTime);
                                startTime = action.getYoutubeTime();
                            }
                            onField[action.getLoc()] = action.getPlayerIn();
                            break;
                        case PAUSE_CLOCK:
                            if (startTime != -1) {
                                int totalTime = action.getYoutubeTime() - startTime;
                                addTimeToEachPlayer(statMap, onField, totalTime);
                                startTime = -1;
                            }
                            break;
                        case START_CLOCK:
                            startTime = action.getYoutubeTime();
                    }
                }
            }
            return statMap;
        }

        @Override
        protected void onPostExecute(Map<String, StatDb> stringStatDbMap) {
            super.onPostExecute(stringStatDbMap);
            displayRawStats(stringStatDbMap);
        }
    }

    private void addTimeToEachPlayer(Map<String, StatDb> statMap, String[] onField, int timeToAdd) {
        for (int i = 0; i < onField.length; i++) {
            StatDb fullStats = statMap.get(onField[i]);
            if (fullStats == null) {
                fullStats = new StatDb(onField[i]);
                if (onField[i].length() > 1) {
                    statMap.put(onField[i], fullStats);
                }
            }
            fullStats.setTime(fullStats.getTime() + timeToAdd);
        }
    }

    private void displayRawStats(Map<String, StatDb> map) {
        List<StatDb> sortedStats = new ArrayList<>();
        for (String key : map.keySet()) {
            PlayerDb player = db.getPlayerById(key);
            if (player == null) {
                continue;
            }
            String lname = player.getLname();
            //int val = map.get(key).getPlayerId() - map.get(key).second;
            int index = 0;
            for (StatDb oldStat : sortedStats) {
                String oldName = db.getPlayerById(oldStat.getPlayerId()).getLname();
                if (oldName.compareTo(lname) < 0) {
                    index++;
                } else {
                    break;
                }
            }
            sortedStats.add(index, map.get(key));
        }

        int PAD = 10;
        LinearLayout statsParent = (LinearLayout) findViewById(R.id.video_stats_parent);
        statsParent.removeAllViews();
        TableLayout table = new TableLayout(this);
        TableRow row1 = new TableRow(this);
        TextView name = new TextView(this);
        name.setPadding(PAD,0,PAD,0);
        TextView shots = new TextView(this);
        shots.setPadding(PAD,0,PAD,0);
        TextView goals = new TextView(this);
        goals.setPadding(PAD,0,PAD,0);
        TextView assists = new TextView(this);
        assists.setPadding(PAD,0,PAD,0);
        TextView turnovers = new TextView(this);
        turnovers.setPadding(PAD,0,PAD,0);
        TextView takeaways = new TextView(this);
        takeaways.setPadding(PAD,0,PAD,0);
        TextView yellowCards = new TextView(this);
        yellowCards.setPadding(PAD,0,PAD,0);
        TextView redCards = new TextView(this);
        redCards.setPadding(PAD,0,PAD,0);
        TextView plusses = new TextView(this);
        plusses.setPadding(PAD,0,PAD,0);
        TextView minuses = new TextView(this);
        minuses.setPadding(PAD,0,PAD,0);
        TextView minutes = new TextView(this);
        minutes.setPadding(PAD,0,PAD,0);

        name.setText("Name");
        shots.setText("Shots");
        goals.setText("Goals");
        assists.setText("Assists");
        turnovers.setText("Turnovers");
        takeaways.setText("Takeaways");
        yellowCards.setText("Yellows");
        redCards.setText("Reds");
        plusses.setText("Plusses");
        minuses.setText("Minuses");
        minutes.setText("Minutes");



        row1.addView(name);
        row1.addView(shots);
        row1.addView(goals);
        row1.addView(assists);
        row1.addView(turnovers);
        row1.addView(takeaways);
        row1.addView(yellowCards);
        row1.addView(redCards);
        row1.addView(plusses);
        row1.addView(minuses);
        row1.addView(minutes);

        table.addView(row1);


        int i = 0;
        for(StatDb stat : sortedStats) {
            TableRow row = new TableRow(this);

            TextView t = new TextView(this);
            t.setPadding(PAD,0,PAD,0);
            TextView t1 = new TextView(this);
            t1.setPadding(PAD,0,PAD,0);
            TextView t2 = new TextView(this);
            t2.setPadding(PAD,0,PAD,0);
            TextView t3 = new TextView(this);
            t3.setPadding(PAD,0,PAD,0);
            TextView t4 = new TextView(this);
            t4.setPadding(PAD,0,PAD,0);
            TextView t9 = new TextView(this);
            t9.setPadding(PAD,0,PAD,0);
            TextView t7 = new TextView(this);
            t7.setPadding(PAD,0,PAD,0);
            TextView t8 = new TextView(this);
            t8.setPadding(PAD,0,PAD,0);
            TextView t5 = new TextView(this);
            t5.setPadding(PAD,0,PAD,0);
            TextView t6 = new TextView(this);
            t6.setPadding(PAD,0,PAD,0);
            TextView t10 = new TextView(this);
            t7.setPadding(PAD,0,PAD,0);


            t.setText(db.getPlayerById(stat.getPlayerId()).getLname());
            t1.setText(String.valueOf(stat.getShots()));
            t2.setText(String.valueOf(stat.getGoals()));
            t3.setText(String.valueOf(stat.getAssists()));
            t4.setText(String.valueOf(stat.getTurnovers()));
            t9.setText(String.valueOf(stat.getTakeaways()));
            t7.setText(String.valueOf(stat.getYellows()));
            t8.setText(String.valueOf(stat.getReds()));
            t5.setText(String.valueOf(stat.getPlusses()));
            t6.setText(String.valueOf(stat.getMinuses()));
            t10.setText(getPrettyTimeFromMilliseconds(stat.getTime()));


            row.addView(t);
            row.addView(t1);
            row.addView(t2);
            row.addView(t3);
            row.addView(t4);
            row.addView(t9);
            row.addView(t7);
            row.addView(t8);
            row.addView(t5);
            row.addView(t6);
            row.addView(t10);

            if (i % 2 == 1)
            {
                row.setBackgroundResource(R.color.row_background);
            }
            table.addView(row);
            i++;
        }
        statsParent.addView(table);

    }

    //</editor-fold>

    // <editor-fold desc="Seeker task functions"

    private class CalcSeekerTask extends AsyncTask<Void, Void, Void> {
        Map<String, SeekerStats> mSeekerStats;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSeekerStats = new HashMap<String, SeekerStats>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (String gameId : mGamesAdded) {
                SparseArray<List<String>> timeArray = getTimeArray(gameId);
                List<NewActionDb> actions = db.getAllActionsFromGame(gameId + mTeamId);
                int scoreDifferential = 0;
                int startTime = -1;
                for (NewActionDb action : actions) {
                    List<String> onField = timeArray.get(action.getYoutubeTime()/1000);
                    if (onField == null) {
                        continue;
                    }
                    String seeker = onField.get(6);
                    SeekerStats stats = mSeekerStats.get(seeker);
                    if (stats == null) {
                        stats = new SeekerStats(seeker);
                        if (!seeker.equals("Seeker")) {
                            mSeekerStats.put(seeker, stats);
                        }
                    }
                    switch (action.getActualAction()) {
                        case SNITCH_ON_PITCH:
                        startTime = action.getYoutubeTime();
                        case SUB:
                            if (seeker.equals("Seeker")) {
                                startTime = action.getYoutubeTime();
                                continue;
                            }
                            if (startTime != -1) {
                                startTime = addSeekerTime(startTime, gameId, action,
                                        stats, scoreDifferential);
                            }
                            break;
                        case SNITCH_CATCH:
                            if (scoreDifferential > 30) {
                                stats.osrUpCatchesFor += 1;
                            } else if (scoreDifferential < -30) {
                                stats.osrDownCatchesFor += 1;
                            } else {
                                stats.isrCatchesFor += 1;
                            }
                            break;
                        case AWAY_SNITCH_CATCH:
                            if (scoreDifferential > 30) {
                                stats.osrUpCatchesAgainst += 1;
                            } else if (scoreDifferential < -30) {
                                stats.osrDownCatchesAgainst += 1;
                            } else {
                                stats.isrCatchesAgainst += 1;
                            }
                            break;
                        case GOAL:
                            if (startTime != -1) {
                                startTime = addSeekerTime(startTime, gameId, action,
                                        stats, scoreDifferential);
                            }
                            scoreDifferential += 10;
                            checkSeekerGameType(gameId, stats, scoreDifferential);
                            break;
                        case AWAY_GOAL:
                            if (startTime != -1) {
                                startTime = addSeekerTime(startTime, gameId, action,
                                        stats, scoreDifferential);
                            }
                            scoreDifferential -= 10;
                            checkSeekerGameType(gameId, stats, scoreDifferential);
                            break;
                        case PAUSE_CLOCK:
                            if (startTime != -1) {
                                checkSeekerGameType(gameId, stats, scoreDifferential);
                                stats.gamesSeeked.add(gameId);
                                int tt = action.getYoutubeTime() - startTime;
                                stats.timeSeeking += tt;
                                if (scoreDifferential < -30) {
                                    stats.osrDownTime += tt;
                                } else if (scoreDifferential > 30) {
                                    stats.osrUpTime += tt;
                                } else {
                                    stats.isrTime += tt;
                                }
                                startTime = -1;
                            }
                            break;
                        case START_CLOCK:
                            startTime = action.getYoutubeTime();
                            break;
                    }

                }

            }
            return null;
        }
        private int addSeekerTime(int startTime, String gameId, NewActionDb action,
                                  SeekerStats stats, int scoreDifferential) {

            checkSeekerGameType(gameId, stats, scoreDifferential);
            int tt = action.getYoutubeTime() - startTime;
            stats.gamesSeeked.add(gameId);
            stats.timeSeeking += tt;
            if (scoreDifferential < -30) {
                stats.osrDownTime += tt;
            } else if (scoreDifferential > 30) {
                stats.osrUpTime += tt;
            } else {
                stats.isrTime += tt;
            }
            return action.getYoutubeTime();

        }
        private void checkSeekerGameType(String gameId, SeekerStats stats, int scoreDifferential) {
            if (stats.isrGames.contains(gameId)) {
                return;
            }
            if (scoreDifferential > 30) {
                stats.osrUpGamesTotally.add(gameId);
            } else if (scoreDifferential < -30) {
                stats.osrDownGamesTotally.add(gameId);
            } else {
                stats.isrGames.add(gameId);
            }
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            displaySeekerStats(mSeekerStats);
        }
    }

    private void displaySeekerStats(Map<String, SeekerStats> seekerMap) {
        LinearLayout statsParent = getStatsParent();
        statsParent.removeAllViews();
        seekerMap = MapUtil.sortByValue(seekerMap);
        for(Map.Entry<String, SeekerStats> entry : seekerMap.entrySet()) {
            String playerId = entry.getKey();
            SeekerStats stats = entry.getValue();

            TextView name = new TextView(this);
            name.setText(db.getPlayerById(playerId).toString());
            statsParent.addView(name);

            TextView gamesSeeked = new TextView(this);
            gamesSeeked.setText("Games seeked: " + stats.gamesSeeked.size());
            statsParent.addView(gamesSeeked);

            TextView isrGamesSeeked = new TextView(this);
            isrGamesSeeked.setText("SWIM games: " + stats.isrGames.size());
            statsParent.addView(isrGamesSeeked);

            TextView isrCatches = new TextView(this);
            isrCatches.setText("SWIM Catches: " + stats.isrCatchesFor);
            statsParent.addView(isrCatches);

            TextView isrPercent = new TextView(this);
            double percent;
            if (stats.isrGames.size() == 0) {
                percent = 0;
            } else {
                percent = (double) stats.isrCatchesFor / (double) stats.isrGames.size();
            }
            percent *= 100;
            isrPercent.setText("SWIM Percent: " + percent + "%");
            statsParent.addView(isrPercent);

            TextView isrAvgTime = new TextView(this);
            int isrAT;
            if (stats.isrGames.size() == 0) {
                isrAT = 0;
            } else {
                isrAT = stats.isrTime / stats.isrGames.size();
            }
            String isrATString = getPrettyTimeFromMilliseconds(isrAT);
            isrAvgTime.setText("Time per game in SWIM: " + isrATString);
            statsParent.addView(isrAvgTime);

            displayDivider(getStatsParent());
        }
    }

    private class SeekerObject {
        public String key;
        public SeekerStats val;
    }

    // </editor-fold>


    // Build the entire "timeArray", or get it from the map
    private SparseArray<List<String>> getTimeArray(String gameId) {
        if (mTimeArrays.get(gameId + mTeamId) != null) {
            return mTimeArrays.get(gameId + mTeamId);
        }
        SparseArray<List<String> > timeArray = new SparseArray<>();
        // Second 0 is where "game start" happens
        List<NewActionDb> actions = db.getAllActionsFromGame(gameId + mTeamId);

        List<String> onFieldPlayers = getDummyLineupIds();
        int lastSub = 0;

        for (NewActionDb action : actions) {
            int t = action.getYoutubeTime() / 1000;
            if (action.getActualAction() == NewActionDb.NewAction.GAME_START) {
                lastSub = t;
            }

            switch (action.getActualAction()) {
                case SUB:
                    List<String> toStore = new ArrayList<>();
                    toStore.addAll(onFieldPlayers);
                    for (int i = lastSub; i < t; i++) {
                        timeArray.put(i, toStore);
                    }
                    // Sub out the player with their sub
                    onFieldPlayers.set(action.getLoc(), action.getPlayerIn());
                    lastSub = t;
                    break;
                case PAUSE_CLOCK:
                    List<String> toStore1 = new ArrayList<>();
                    toStore1.addAll(onFieldPlayers);
                    for (int i = lastSub; i <= t; i++) {
                        timeArray.put(i, toStore1);
                    }
                    lastSub = t;
                    break;
            }
        }
        mTimeArrays.put(gameId + mTeamId, timeArray);
        return timeArray;
    }

    private List<NewActionDb> getAllActions(String gameId) {
        if (mActionLists.get(gameId + mTeamId) != null) {
            return mActionLists.get(gameId + mTeamId);
        }
        return db.getAllActionsFromGame(gameId + mTeamId);
    }

    public static List<PlayerDb> getDummyLineupPlayers(boolean seeker) {

        List<PlayerDb> dummy = new ArrayList<>();
        dummy.add(new PlayerDb("", "", "", "Chaser 1", "", -1, -1));
        dummy.add(new PlayerDb("", "", "", "Chaser 2", "", -1, -1));
        dummy.add(new PlayerDb("", "", "", "Chaser 3", "", -1, -1));
        dummy.add(new PlayerDb("", "", "", "Keeper", "", -1, -1));
        dummy.add(new PlayerDb("", "", "", "Beater 1", "", -1, -1));
        dummy.add(new PlayerDb("", "", "", "Beater 2", "", -1, -1));
        if (seeker) {
            dummy.add(new PlayerDb("", "", "", "Seeker", "", -1, -1));
        }
        return dummy;
    }

    public static List<String> getDummyLineupIds() {
        List<String> dummy = new ArrayList<>();
        dummy.add("Chaser 1");
        dummy.add("Chaser 2");
        dummy.add("Chaser 3");
        dummy.add("Keeper");
        dummy.add("Beater 1");
        dummy.add("Beater 2");
        dummy.add("Seeker");
        return dummy;
    }

    private class UploadTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            String vidId = mGamesAdded.get(0);
            String authCode = params[1].toString();

            if (authCode.isEmpty()) {
                return false;
            }

            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Videos");
            query.setLimit(1000);
            query.whereEqualTo("vid_id", vidId);
            List<ParseObject> objects = new ArrayList<ParseObject>();

            try {
                objects = query.find();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            String objectId = null;
            String publicAuthCode = null;
            String privateAuthCode = "aa1";
            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i).getString("team_id").equals(mTeamId)) {
                    publicAuthCode = objects.get(i).getString("auth_code");
                    objectId = objects.get(i).getObjectId();
                }
            }
            if (publicAuthCode != null && authCode.equals(publicAuthCode)) {
                List<NewActionDb> list = getAllActions(vidId);
                String uploadString = NewActionDb.convertActionsToJSON(list);
                try{
                    ParseObject ret = query.get(objectId);
                    ret.put("events_community", uploadString);
                    ret.save();
                } catch (com.parse.ParseException e) {
                    return false;
                }

            } else if (privateAuthCode.equals(authCode)) {
                List<NewActionDb> list = getAllActions(vidId);
                String uploadString = NewActionDb.convertActionsToJSON(list);
                try{
                    ParseObject ret = query.get(objectId);
                    ret.put("events_json", uploadString);
                    ret.save();
                } catch (com.parse.ParseException e) {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            String message = "";
            if (aBoolean) {
                message = "Upload successful";
            } else {
                message = "Upload unsuccessful";
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        }
    }







}

