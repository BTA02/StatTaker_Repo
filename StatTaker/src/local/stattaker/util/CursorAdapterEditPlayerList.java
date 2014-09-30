package local.stattaker.util;


import local.stattaker.EditTeam;
import local.stattaker.R;
import local.stattaker.helper.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CursorAdapterEditPlayerList extends CursorAdapter
{
	private LayoutInflater mInflater;
	private Context context;
	
	private String playerId;
	private String teamId;

	// ---------------------------------------------------------------------------
	// ---------------------------------------------------------------------------

	public CursorAdapterEditPlayerList(Context context_, Cursor c, int flags)
	{
		super(context_, c, flags);
		context = context_;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}// ctor

	// ---------------------------------------------------------------------------

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		//these checkboxes are just flat wrong for now
		//I need a way to make them flat right
		
		TextView numberTV = (TextView) view.findViewById(R.id.edit_cursor_adapter_player_num);
		TextView fnameTV = (TextView) view.findViewById(R.id.edit_cursor_adapter_player_fname);
		TextView lnameTV = (TextView) view.findViewById(R.id.edit_cursor_adapter_player_lname);
		final CheckBox checkbox = (CheckBox) view.findViewById(R.id.edit_cursor_adapter_checkbox);
		numberTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NUMBER)));
		fnameTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FNAME)));
		lnameTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_LNAME)));
		
		playerId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ID));
		final EditTeam activity = (EditTeam) context;
		teamId = activity.teamId;
		if (activity.db.isPlayerActiveOnTeam(teamId, playerId))
		{
			checkbox.setChecked(true);
		}
		
		else
		{
			checkbox.setChecked(false);
		}
		
		checkbox.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				//from not checked -> checked
				if (checkbox.isChecked())
				{
					//add player to active roster
					activity.db.updateActiveInfo(teamId, playerId, 1);
					//update the "active" count
				}
				else
				{
					//remove player from active roster
					activity.db.updateActiveInfo(teamId, playerId, 0);
					//update the "active" count
				}
			}
		
		});

	}// bindView

	// ---------------------------------------------------------------------------

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup vgroup)
	{
		View view = mInflater.inflate(R.layout.edit_player_list_cursor_adapter, null);
		bindView(view, context, cursor);
		return view;
	}// newView

	// ---------------------------------------------------------------------------
	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row_layout_view = convertView;
		if ((row_layout_view == null)){


			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row_layout_view = inflater.inflate(R.layout.edit_player_list_cursor_adapter, null);
		}   

		TextView numberTV = (TextView) row_layout_view.findViewById(R.id.edit_cursor_adapter_player_num);
		TextView fnameTV = (TextView) row_layout_view.findViewById(R.id.edit_cursor_adapter_player_fname);
		TextView lnameTV = (TextView) row_layout_view.findViewById(R.id.edit_cursor_adapter_player_lname);
		CheckBox checkBox = (CheckBox) row_layout_view.findViewById(R.id.edit_cursor_adapter_checkbox);

		return row_layout_view;
	}
	*/
	// ---------------------------------------------------------------------------

}// FileListAdapter