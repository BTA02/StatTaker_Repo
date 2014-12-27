package local.quidstats.util;


import local.quidstats.EditTeam;
import local.quidstats.R;
import local.quidstats.helper.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
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
	public void bindView(final View view, Context context, final Cursor cursor)
	{
		//these checkboxes are just flat wrong for now
		//I need a way to make them flat right
		
		TextView numberTV = (TextView) view.findViewById(R.id.edit_cursor_adapter_player_num);
		TextView fnameTV = (TextView) view.findViewById(R.id.edit_cursor_adapter_player_fname);
		TextView lnameTV = (TextView) view.findViewById(R.id.edit_cursor_adapter_player_lname);
		final CheckBox checkbox = (CheckBox) view.findViewById(R.id.edit_cursor_adapter_checkbox);
		checkbox.setTag(new Integer(cursor.getPosition()));
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
					Integer posInt = (Integer)v.getTag();
					int pos = posInt.intValue();
					cursor.moveToPosition(pos);
					String pId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ID));
					activity.db.updateActiveInfo(teamId, pId, 1);
					activity.updateActiveCount();
					//update the "active" count
					
				}
				else
				{
					//remove player from active roster
					Integer posInt = (Integer)v.getTag();
					int pos = posInt.intValue();
					cursor.moveToPosition(pos);
					String pId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_ID));
					activity.db.updateActiveInfo(teamId, pId, 0);
					activity.updateActiveCount();
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
	
	// ---------------------------------------------------------------------------

}// FileListAdapter