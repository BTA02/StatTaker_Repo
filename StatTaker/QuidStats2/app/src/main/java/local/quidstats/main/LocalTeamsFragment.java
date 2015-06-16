package local.quidstats.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import local.quidstats.R;
import local.quidstats.database.GameDb;
import local.quidstats.database.TeamDb;

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

        ma.listAdapter = new ArrayAdapter<TeamDb>(ma.context,
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
                        Intent i = new Intent(ma.context,
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
                        Intent i = new Intent(ma.context,
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
                        // Do something
                    }
                });

                TextView teamStats =
                        (TextView) newGameView.findViewById(R.id.selected_team_popup_stats);
                teamStats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ma.context, AdvancedStats.class);
                        i.putExtra("teamId", mTeamSelected.getId());
                        startActivity(i);
                    }
                });
                /*
                ListView oldGames = (ListView) newGameView
                        .findViewById(R.id.new_game_list);
                List<GameDb> gameList = new ArrayList<GameDb>();
                gameList = ma.db.getAllGamesForTeam(team.getId());
                final ArrayAdapter<GameDb> listAdapter = new ArrayAdapter<GameDb>(ma.context,
                        R.layout.custom_player_list, gameList);
                oldGames.setAdapter(listAdapter);
                oldGames.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        GameDb gameClicked = (GameDb) listAdapter
                                .getItem(position);
                        Intent i = new Intent(ma.context,
                                GameActivity.class);
                        i.putExtra("gameId", gameClicked.getId());
                        startActivity(i);
                        //I want to close the dialog here...

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
                        AlertDialog.Builder delBuilder = new AlertDialog.Builder(ma.context);
                        delBuilder.setTitle("Delete game?");
                        delBuilder.setNegativeButton("Cancel", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do nothing
                                dialog.dismiss();
                            }
                        });
                        delBuilder.setNeutralButton("Advanced Stats", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(ma.context, AdvancedStats.class);
                                i.putExtra("gameId", gameClicked.getId());
                                startActivity(i);
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
                */
                alert.show();
            }

        });

    }

    public void showNewTeamDialog() {
        if (newTeamDialog != null && newTeamDialog.isShowing()) {
            return;
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ma.context);

        alertBuilder.setTitle("Create New Team");
        alertBuilder.setMessage("Enter Name of Team:");

        final EditText input = new EditText(ma.context);
        alertBuilder.setView(input);

        alertBuilder.setPositiveButton("Ok",
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newTeamId = UUID.randomUUID().toString();
                        ma.db.addTeam(newTeamId, input.getText().toString());
                        Intent i = new Intent(ma.context, EditTeam.class);
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
        switch (v.getId()) {
            case R.id.selected_team_popup_create_button:
                break;
            case R.id.selected_team_popup_edit_button:
                String id = mTeamSelected.getId();
                Intent i = new Intent(ma.context,
                        EditTeam.class);
                i.putExtra("teamId", id);
                startActivity(i);
                break;
            case R.id.selected_team_popup_resume_game:
                break;
            case R.id.selected_team_popup_stats:
                break;
        }
    }
}