package local.quidstats.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import local.quidstats.R;
import local.quidstats.database.GameDb;
import local.quidstats.database.NewActionDb;
import local.quidstats.database.TeamDb;
import local.quidstats.video.VideoPlayerActivity;
import local.quidstats.video.VideoStatsActivity;

public class LocalTeamsFragment extends Fragment implements
        View.OnClickListener {
    String TAG = "FragmentLocalTeam";

    private TeamDb mTeamSelected;

    private MainActivity ma;
    private ListView currentTeams;
    private List<TeamDb> teamList;

    protected AlertDialog newTeamDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(getActivity(), "RoDlI2ENBnxSWlPvdG2VEsFPRSt06qHJ78nZop77",
                "fbuEyPT9Exq141IZfueUO1asOcbAFaBjJvdAFI1A");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_local,
                container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ma = (MainActivity) getActivity();

        populateLocalList();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateLocalList();
    }

    void populateLocalList() {
        currentTeams = (ListView) ma
                .findViewById(R.id.fragment_local_teams_list);
        Cursor c = ma.db.getAllTeamsCursor();

        teamList = new ArrayList<TeamDb>();
        teamList = ma.db.getAllTeamsList();

        teamList.remove(ma.teamRowList);

        Collections.sort(teamList, new TeamDb.OrderByTeamName());

        teamList.add(0, ma.teamRowList);

        ma.listAdapter = new ArrayAdapter<TeamDb>(getActivity(),
                R.layout.custom_player_list, teamList);

        currentTeams.setAdapter(ma.listAdapter);
        currentTeams.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mTeamSelected = (TeamDb) currentTeams.getItemAtPosition(arg2);
                if (mTeamSelected.getId().equals("aaa-aaa-aaa")) {
                    showNewTeamDialog();
                    return;
                }
                // create an options pop up to record new game or edit team or
                // see old games
                LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
                final View newGameView = dialogFactory.inflate(
                        R.layout.selected_team_popup, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                alert.setView(newGameView);

                final EditText oppo =
                        (EditText) newGameView.findViewById(R.id.selected_team_popup_opponent);


                Button createButton =
                        (Button) newGameView.findViewById(R.id.selected_team_popup_create_button);
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String opponentName = oppo.getText().toString();
                        String mod = UUID.randomUUID().toString();
                        mod = mod.substring(0, 4);
                        if (opponentName.length() == 0) {
                            opponentName = "Unnamed Opponent " + "(" + mod + ")";
                        }
                        String gId = ma.db.createNewGame(mTeamSelected.getId(), opponentName);
                        Intent i = new Intent(getActivity(),
                                GameActivity.class);
                        i.putExtra("gameId", gId);
                        startActivity(i);
                    }
                });

                TextView editTeamTextView =
                        (TextView) newGameView.findViewById(R.id.selected_team_popup_edit_button);
                editTeamTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = mTeamSelected.getId();
                        Intent i = new Intent(getActivity(),
                                EditTeam.class);
                        i.putExtra("teamId", id);
                        startActivity(i);
                    }
                });

                TextView oldGamesTextView =
                        (TextView) newGameView.findViewById(R.id.selected_team_popup_resume_game);
                oldGamesTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Move this
                        LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
                        final View oldGamesView = dialogFactory.inflate(
                                R.layout.old_games_layout, null);
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                        alert.setView(oldGamesView);
                        ListView oldGames = (ListView) oldGamesView
                                .findViewById(R.id.old_games_list);
                        List<GameDb> oldGamesList = new ArrayList<>();
                        oldGamesList = ma.db.getAllGamesForTeam(mTeamSelected.getId());
                        final ArrayAdapter<GameDb> listAdapter = new ArrayAdapter<>(getActivity(),
                                R.layout.custom_player_list, oldGamesList);
                        oldGames.setAdapter(listAdapter);
                        oldGames.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                GameDb gameClicked = (GameDb) listAdapter
                                        .getItem(position);
                                Intent i = new Intent(getActivity(),
                                        GameActivity.class);
                                i.putExtra("gameId", gameClicked.getId());
                                startActivity(i);
                                // TODO Close dialog
                            }

                        });

                        //Long click, for game deletion
                        oldGames.setOnItemLongClickListener(new OnItemLongClickListener() {

                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent,
                                                           View view, int position, long id) {
                                final GameDb gameClicked = (GameDb) listAdapter
                                        .getItem(position);
                                //now, launch "do you wish to delete" dialog
                                AlertDialog.Builder delBuilder = new AlertDialog.Builder(getActivity());
                                delBuilder.setTitle("Delete game?");
                                delBuilder.setNegativeButton("Cancel", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //Do nothing
                                        dialog.dismiss();
                                    }
                                });
                                delBuilder.setPositiveButton("Yes", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        listAdapter.remove(gameClicked);
                                        ma.db.deleteGame(gameClicked.getId());
                                        listAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                                delBuilder.show();
                                return true;
                            }

                        });
                        alert.show();
                    }
                });

                TextView teamStats =
                        (TextView) newGameView.findViewById(R.id.selected_team_popup_stats);
                teamStats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), AdvancedStats.class);
                        i.putExtra("teamId", mTeamSelected.getId());
                        startActivity(i);
                    }
                });

                TextView watchVids =
                        (TextView) newGameView.findViewById(R.id.selected_team_videos);
                watchVids.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showVideos(true);
                    }
                });

                TextView downloadStats =
                        (TextView) newGameView.findViewById(R.id.selected_team_download_stats);
                downloadStats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showVideos(false);
                    }
                });


                TextView vidStats = (TextView) newGameView.findViewById(R.id.selected_team_vid_stats);
                vidStats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), VideoStatsActivity.class);
                        i.putExtra("teamId", mTeamSelected.getId());
                        startActivity(i);
                    }
                });

                alert.show();
            }

        });

    }

    public void showNewTeamDialog() {
        if (newTeamDialog != null && newTeamDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

        alertBuilder.setTitle("Create New Team");
        alertBuilder.setMessage("Enter Name of Team:");

        final EditText input = new EditText(getActivity());
        alertBuilder.setView(input);

        alertBuilder.setPositiveButton("Ok",
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newTeamId = UUID.randomUUID().toString();
                        ma.db.addTeam(newTeamId, input.getText().toString());
                        Intent i = new Intent(getActivity(), EditTeam.class);
                        i.putExtra("teamId", newTeamId);
                        startActivity(i);
                    }
                });

        alertBuilder.setNegativeButton("Cancel",
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
        newTeamDialog = alertBuilder.create();
        newTeamDialog.show();
    }

    @Override
    public void onClick(View v) {
        String id = mTeamSelected.getId();
        switch (v.getId()) {
            case R.id.selected_team_popup_create_button:
                break;
            case R.id.selected_team_popup_edit_button:
                Intent i = new Intent(getActivity(),
                        EditTeam.class);
                i.putExtra("teamId", id);
                startActivity(i);
                break;
            case R.id.selected_team_popup_resume_game:
                // It's somewhere else
                break;
            case R.id.selected_team_popup_stats:
                break;
            case R.id.selected_team_videos:
                showVideos(true);
                break;
        }
    }

    private void showVideos(final boolean watchVid) {
        LayoutInflater dialogFactory = LayoutInflater.from(getActivity());
        final View videosListView = dialogFactory.inflate
                (R.layout.old_games_layout, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setView(videosListView);
        ListView oldGames = (ListView) videosListView
                .findViewById(R.id.old_games_list);
        List<Video> videosList = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Videos");
        query.setLimit(1000);
        query.whereEqualTo("team_id", mTeamSelected.getId());
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
            videosList.add(new Video(gameName, gameId));
        }
        final ArrayAdapter<Video> listAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, videosList);
        oldGames.setAdapter(listAdapter);

        oldGames.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final Video videoClicked = listAdapter.getItem(position);
                if (watchVid) {
                    Intent i = new Intent(getActivity(), VideoPlayerActivity.class);
                    i.putExtra("videoId", videoClicked.id);
                    i.putExtra("teamId", mTeamSelected.getId());
                    i.putExtra("opponent", videoClicked.description);
                    startActivity(i);
                }
                else {
                    AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Videos");
                            query.setLimit(1000);
                            query.whereEqualTo("vid_id", videoClicked.id);
                            List<ParseObject> objects = new ArrayList<ParseObject>();
                            try {
                                objects = query.find();
                            } catch (com.parse.ParseException e) {
                                e.printStackTrace();
                            }
                            List<NewActionDb> events1 = null;
                            for (int i = 0; i < objects.size(); i++) {
                                if (objects.get(i).getString("team_id").equals(mTeamSelected.getId())) {
                                    String str = objects.get(i).getString("events_json");
                                    events1 = NewActionDb.convertJSONToActions(str);
                                }
                            }
                            if (events1 != null && !events1.isEmpty()) {
                                ma.db.addAllActions(events1);
                            }
                            return null;
                        }
                    }.execute();

                }
            }

        });

        alert.show();
    }

    public static class Video {
        public String description;
        public String id;

        public Video(String desc_, String id_) {
            description = desc_;
            id = id_;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}