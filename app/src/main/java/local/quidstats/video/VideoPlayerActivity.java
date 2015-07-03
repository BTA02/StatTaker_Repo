package local.quidstats.video;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.List;
import java.util.UUID;

import local.quidstats.R;
import local.quidstats.database.DatabaseHelper;
import local.quidstats.database.NewActionDb;
import local.quidstats.database.PlayerDb;


public class VideoPlayerActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener,
        View.OnClickListener,
        DialogInterface.OnClickListener,
        AdapterView.OnItemClickListener {

    private YouTubePlayer mPlayer;
    private RelativeLayout mTopBar;
    private View mSideBar;

    private String mVideoId;
    private String mActualVideoId;
    private DatabaseHelper db;
    private String mTeamId;
    private AlertDialog mStatsOverlay;
    private AlertDialog mPlayerOverlay;
    private AlertDialog mPreviewOverlay;
    private AlertDialog mSubOverlay;

    private static int TOP_BAR_ITEM_WIDTH = 10;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.youtube_player);

        mTopBar = (RelativeLayout) findViewById(R.id.layout_top_bar);
        mSideBar = (View) findViewById(R.id.layout_side_bar);
        mTopBar.setOnClickListener(this);
        mSideBar.setOnClickListener(this);
        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);


        youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
        db = new DatabaseHelper(this);
        String opponentName = "Unnamed opponent";
        if (getIntent() != null) {
            mVideoId = getIntent().getExtras().getString("videoId");
            mActualVideoId = getIntent().getExtras().getString("videoId");
            //UPDATE THE DATABASE
            mTeamId = getIntent().getExtras().getString("teamId");
            // DON'T RUN EVER AGAIN
            //db.update1(mVideoId, mTeamId);
            mVideoId = mVideoId + mTeamId;
        }

        if (getActionBar() != null) {
            getActionBar().hide();
        }

    }



    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(mActualVideoId);
        }
        mPlayer = player;
    }

    private void drawVisualCues() {

        mTopBar.removeAllViews();

        List<NewActionDb> list = db.getAllActionsFromGame(mVideoId);

        for(NewActionDb action : list) {
            addVisualCue(action.getYoutubeTime(), action.getId(), action.getActualAction());
        }
    }

    // Regular onClickListener
    @Override
    public void onClick(View v) {
        if (mPlayer == null) {
            return;
        }
        NewActionDb toAdd = new NewActionDb();
        toAdd.setId(UUID.randomUUID().toString());
        toAdd.setGameId(mVideoId);
        toAdd.setYoutubeTime(mPlayer.getCurrentTimeMillis());
        toAdd.setPlayerOut("");
        toAdd.setPlayerIn("");
        toAdd.setLoc(-1);
        switch (v.getId()) {

            case R.id.layout_top_bar:
                mPlayer.pause();
                launchPreviewOverlay("");
                drawVisualCues();
                break;
            case R.id.layout_side_bar:
                mPlayer.pause();
                launchStatsOverlay();
                break;
            case R.id.overlay_shot:
                launchPlayerOverlay(NewActionDb.NewAction.SHOT);
                break;
            case R.id.overlay_goal:
                launchPlayerOverlay(NewActionDb.NewAction.GOAL);
                break;
            case R.id.overlay_assist:
                launchPlayerOverlay(NewActionDb.NewAction.ASSIST);
                break;
            case R.id.overlay_turnover:
                launchPlayerOverlay(NewActionDb.NewAction.TURNOVER);
                break;
            case R.id.overlay_snitch_catch:
                launchPlayerOverlay(NewActionDb.NewAction.SNITCH_CATCH);
                break;
            case R.id.overlay_start_clock:
                toAdd.setActualAction(NewActionDb.NewAction.START_CLOCK);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), NewActionDb.NewAction.START_CLOCK);
                break;
            case R.id.overlay_pause_clock:
                toAdd.setActualAction(NewActionDb.NewAction.PAUSE_CLOCK);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), NewActionDb.NewAction.PAUSE_CLOCK);
                break;
            case R.id.overlay_start_game:
                toAdd.setActualAction(NewActionDb.NewAction.GAME_START);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), NewActionDb.NewAction.GAME_START);
                break;
            case R.id.overlay_end_game:
                toAdd.setActualAction(NewActionDb.NewAction.GAME_END);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), NewActionDb.NewAction.GAME_END);
                break;
            case R.id.overlay_yellow_card:
                toAdd.setActualAction(NewActionDb.NewAction.YELLOW_CARD);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), NewActionDb.NewAction.YELLOW_CARD);
                break;
            case R.id.overlay_red_card:
                toAdd.setActualAction(NewActionDb.NewAction.RED_CARD);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), NewActionDb.NewAction.RED_CARD);
                break;
            case R.id.overlay_away_score:
                toAdd.setActualAction(NewActionDb.NewAction.AWAY_GOAL);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), NewActionDb.NewAction.AWAY_GOAL);
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    // When clicking on an on field player
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlayerDb selected = (PlayerDb) parent.getAdapter().getItem(position);

        launchSubOverlay(position);
    }

    private void launchStatsOverlay() {
        AlertDialog.Builder overlay = overlayDialog();
        if (mStatsOverlay == null || !mStatsOverlay.isShowing()) {
            mStatsOverlay = overlay.show();
        }

    }

    private void launchPlayerOverlay(NewActionDb.NewAction a) {
        AlertDialog.Builder fieldPlayers = onFieldDialog(a);
        if (mPlayerOverlay == null || !mPlayerOverlay.isShowing()) {
            mPlayerOverlay = fieldPlayers.show();
        }
    }

    private void launchPreviewOverlay(String id) {
        // Called with "" to begin with
        AlertDialog.Builder prevOverlay = previewDialog(id);
        if ((mPreviewOverlay == null || !mPreviewOverlay.isShowing()) && prevOverlay != null) {
            mPreviewOverlay = prevOverlay.show();
        }
    }

    private void launchSubOverlay(int loc) {
        AlertDialog.Builder subOverlay = subDialog(loc);
        if (mSubOverlay == null || !mSubOverlay.isShowing()) {
            mSubOverlay = subOverlay.show();
        }
    }

    AlertDialog.Builder overlayDialog() {
        // setup the dialog
        LayoutInflater dialogFactory = LayoutInflater.from(this);
        View overlayView = dialogFactory.inflate(R.layout.video_player_overlay, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(overlayView);
        builder.setCancelable(true);

        TextView tv1 = (TextView) overlayView.findViewById(R.id.overlay_shot);
        TextView tv2 = (TextView) overlayView.findViewById(R.id.overlay_goal);
        TextView tv3 = (TextView) overlayView.findViewById(R.id.overlay_assist);
        TextView tv4 = (TextView) overlayView.findViewById(R.id.overlay_turnover);


        TextView tv5 = (TextView) overlayView.findViewById(R.id.overlay_start_clock);
        TextView tv10 = (TextView) overlayView.findViewById(R.id.overlay_pause_clock);
        TextView tv11 = (TextView) overlayView.findViewById(R.id.overlay_start_game);
        TextView tv6 = (TextView) overlayView.findViewById(R.id.overlay_end_game);
        TextView tv7 = (TextView) overlayView.findViewById(R.id.overlay_snitch);
        TextView tv8 = (TextView) overlayView.findViewById(R.id.overlay_yellow_card);
        TextView tv9 = (TextView) overlayView.findViewById(R.id.overlay_red_card);
        TextView tv12 = (TextView) overlayView.findViewById(R.id.overlay_away_score);
        TextView tv13 = (TextView) overlayView.findViewById(R.id.overlay_home_score);


        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv10.setOnClickListener(this);
        tv11.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tv7.setOnClickListener(this);
        tv8.setOnClickListener(this);
        tv9.setOnClickListener(this);
        tv12.setOnClickListener(this);

        ListView playersList = (ListView) overlayView.findViewById(R.id.overlay_onfield_players);


        List<PlayerDb> players = getOnFieldPlayersAtTime(mPlayer.getCurrentTimeMillis());
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, players);

        setScoreAtTime(mPlayer.getCurrentTimeMillis(), tv13, tv12);

        playersList.setAdapter(adapter);
        playersList.setOnItemClickListener(this);

        return builder;

    }

    // This is the click that launches the onfield players for
    // assigning the stats.
    AlertDialog.Builder onFieldDialog(final NewActionDb.NewAction a) {
        LayoutInflater dialogFactory = LayoutInflater.from(this);
        final View addSubView = dialogFactory.inflate(
                android.R.layout.simple_list_item_1, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(addSubView);
        builder.setTitle("Assign to...");
        builder.setCancelable(true);

        final List<PlayerDb> list = getOnFieldPlayersAtTime(mPlayer.getCurrentTimeMillis());
        final CharSequence[] playersOnField = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            playersOnField[i] = list.get(i).toString();
        }

        builder.setItems(playersOnField, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewActionDb toAdd = new NewActionDb();
                toAdd.setId(UUID.randomUUID().toString());
                toAdd.setGameId(mVideoId);
                toAdd.setActualAction(a);
                toAdd.setYoutubeTime(mPlayer.getCurrentTimeMillis());
                toAdd.setPlayerOut(list.get(which).getPlayerId());
                toAdd.setPlayerIn("");
                toAdd.setLoc(-1);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), a);
            }
        });

        return builder;
    }

    AlertDialog.Builder previewDialog(final String id) {

        LayoutInflater dialogFactory = LayoutInflater.from(this);
        final View addSubView = dialogFactory.inflate(
                R.layout.preview_stat_dialog, null);


        final List<NewActionDb> allActions = db.getAllActionsFromGame(mVideoId);
        if (allActions.isEmpty()) {
            return null;
        }
        int preIndex;
        if (id.isEmpty()) {
            preIndex = getClosestItemIndex(allActions);
            //preIndex = 0;
        } else {
            preIndex = allActions.indexOf(db.getNewAction(id));
        }

        final int index = preIndex;
        // Normally I'd want to get the specific
        final NewActionDb a = allActions.get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(addSubView);

        builder.setCancelable(true);

        TextView backButton = (TextView) addSubView.findViewById(R.id.preview_stat_prev);
        TextView nextButton = (TextView) addSubView.findViewById(R.id.preview_stat_next);
        TextView number = (TextView) addSubView.findViewById(R.id.preview_stat_number);
        TextView delButton = (TextView) addSubView.findViewById(R.id.preview_stat_delete);

        if (index != 0) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreviewOverlay != null && mPreviewOverlay.isShowing()) {
                        mPreviewOverlay.dismiss();
                    }
                    launchPreviewOverlay(allActions.get(index - 1).getId());
                }
            });
        }
        if (index < allActions.size()-1) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreviewOverlay != null && mPreviewOverlay.isShowing()) {
                        mPreviewOverlay.dismiss();
                    }
                    launchPreviewOverlay(allActions.get(index + 1).getId());
                }
            });
        }

        number.setText((index+1) + "/" + allActions.size());


        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteNewAction(a.getId());
                if (mPreviewOverlay != null && mPreviewOverlay.isShowing()) {
                    mPreviewOverlay.dismiss();
                }
            }
        });

        TextView action = (TextView) addSubView.findViewById(R.id.preview_stat_action);
        TextView name1 = (TextView) addSubView.findViewById(R.id.preview_stat_name);
        TextView name2 = (TextView) addSubView.findViewById(R.id.preview_stat_sub_name);
        TextView time = (TextView) addSubView.findViewById(R.id.preview_stat_time);

        action.setText(a.getActualAction().toString());

        if (!a.getPlayerOut().isEmpty()) {
            PlayerDb p = db.getPlayerById(a.getPlayerOut());
            name1.setText(p.getFname() + " " + p.getLname());
        } else {
            name1.setVisibility(View.GONE);
        }
        if (!a.getPlayerIn().isEmpty()) {
            PlayerDb p = db.getPlayerById(a.getPlayerIn());
            name2.setText(p.getFname() + " " + p.getLname());
        } else {
            name2.setVisibility(View.GONE);
        }
        double secs = (double) a.getYoutubeTime();
        secs = secs/1000;
        int minutes = (int) secs/60;
        int seconds = (int) secs%60;
        if (seconds < 10) {
            time.setText(minutes + ":0" + seconds);
        } else {
            time.setText(minutes + ":" + seconds);
        }
        return builder;
    }

    private int getClosestItemIndex(List<NewActionDb> actions) {
        int ret = 0;
        for (int i = 0; i < actions.size(); i++) {
            NewActionDb action = actions.get(i);
            if (action.getYoutubeTime() <= mPlayer.getCurrentTimeMillis()) {
                ret = i;
            } else {
                break;
            }
        }
        return ret;
    }

    AlertDialog.Builder subDialog(final int loc) {
        LayoutInflater dialogFactory = LayoutInflater.from(this);
        final View addSubView = dialogFactory.inflate(
                android.R.layout.simple_list_item_1, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(addSubView);
        builder.setCancelable(true);

        final List<PlayerDb> offField = db.getAllPlayersFromTeam(mTeamId, 0);
        final List<PlayerDb> list = getOnFieldPlayersAtTime(mPlayer.getCurrentTimeMillis());

        for (PlayerDb onFieldPlayer : list) {
            offField.remove(onFieldPlayer);
        }

        final CharSequence[] playersOffField = new String[offField.size()];
        for (int i = 0; i < offField.size(); i++) {
            playersOffField[i] = offField.get(i).toString();
        }

        builder.setItems(playersOffField, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewActionDb toAdd = new NewActionDb();
                toAdd.setId(UUID.randomUUID().toString());
                toAdd.setGameId(mVideoId);
                toAdd.setActualAction(NewActionDb.NewAction.SUB);
                toAdd.setYoutubeTime(mPlayer.getCurrentTimeMillis());
                toAdd.setPlayerOut("");
                toAdd.setPlayerIn(offField.get(which).getPlayerId());
                toAdd.setLoc(loc);
                db.addNewAction(toAdd);
                addVisualCue(toAdd.getYoutubeTime(), toAdd.getId(), NewActionDb.NewAction.SUB);
            }
        });

        return builder;
    }

    private List<PlayerDb> getOnFieldPlayersAtTime(int time) {
        List<PlayerDb> players = db.getAllPlayersFromTeam(mTeamId, 0);

        List<NewActionDb> substitutionActions = db.getAllSubActionsFromGame(mVideoId);

        List<PlayerDb> onFieldPlayers = players.subList(0,6);
        for (NewActionDb action : substitutionActions) {
            if (action.getYoutubeTime() <= time) {
                onFieldPlayers.set(action.getLoc(), db.getPlayerById(action.getPlayerIn()));
            }
        }

        return onFieldPlayers;
    }

    private void addVisualCue(int time, String id, NewActionDb.NewAction act) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TOP_BAR_ITEM_WIDTH,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int totalMillis = mPlayer.getDurationMillis();
        double percent = (double) time / (double) totalMillis;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int i = (int) Math.round(width * percent);

        TextView view = new TextView(this);
        view.setTag(id);
        view.setBackground(getResources().getDrawable(R.drawable.top_bar_item_shape));
        setBackgroundColor(view, act);
        view.setX(i);
        view.setLayoutParams(params);

        mTopBar.addView(view);

        if (mPlayerOverlay != null && mPlayerOverlay.isShowing()) {
            mPlayerOverlay.dismiss();
        }

        if (mStatsOverlay != null && mStatsOverlay.isShowing()) {
            mStatsOverlay.dismiss();
        }
    }

    private void setScoreAtTime(int time, TextView homeScore, TextView awayScore) {
        List<NewActionDb> homeScores = db.getAllHomeScoresFromGame(mVideoId);
        List<NewActionDb> awayScores = db.getAllAwayScoresFromGame(mVideoId);


        int home = 0;
        int away = 0;

        for (NewActionDb score : homeScores) {
            if (score.getYoutubeTime() <= time) {
                home++;
            }
        }
        for (NewActionDb score : awayScores) {
            if (score.getYoutubeTime() <= time) {
                away++;
            }
        }
        homeScore.setText(String.valueOf((home * 10)));
        awayScore.setText(String.valueOf((away * 10)));
    }

    private void setBackgroundColor(View v, NewActionDb.NewAction a) {
        switch (a) {
            case SHOT:
                v.setBackgroundColor(getResources().getColor(R.color.shot_blue));
                break;
            case GOAL:
                v.setBackgroundColor(getResources().getColor(R.color.goal_green));
                break;
            case ASSIST:
                v.setBackgroundColor(getResources().getColor(R.color.assist_purple));
                break;
            case TURNOVER:
                v.setBackgroundColor(getResources().getColor(R.color.turnover_red));
                break;
            case AWAY_GOAL:
                v.setBackgroundColor(getResources().getColor(R.color.opponent_goal_black));
                break;
            case SUB:
                v.setBackgroundColor(Color.GRAY);
            case START_CLOCK:
                //v.setBackgroundColor(getResources().getColor(R.color.shot_blue));
            case PAUSE_CLOCK:
                //v.setBackgroundColor(getResources().getColor(R.color.shot_blue));
            case GAME_END:
                //v.setBackgroundColor(getResources().getColor(R.color.shot_blue));

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mPlayer.release();
    }


}
