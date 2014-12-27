package local.quidstats.util;


import local.quidstats.helper.DatabaseHelper;
import local.stattaker.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CursorAdapterPlayerList extends CursorAdapter
{
	private LayoutInflater mInflater;
	private Context context;

	// ---------------------------------------------------------------------------
	// ---------------------------------------------------------------------------

	public CursorAdapterPlayerList(Context context_, Cursor c, int flags)
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
		TextView numberTV = (TextView) view.findViewById(R.id.cursor_adapter_player_num);
		TextView fnameTV = (TextView) view.findViewById(R.id.cursor_adapter_player_fname);
		TextView lnameTV = (TextView) view.findViewById(R.id.cursor_adapter_player_lname);
		numberTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NUMBER)));
		fnameTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FNAME)));
		lnameTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_LNAME)));

	}// bindView

	// ---------------------------------------------------------------------------

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup vgroup)
	{
		View view = mInflater.inflate(R.layout.player_list_cursor_adapter, null);
		bindView(view, context, cursor);
		return view;
	}// newView
	
	// ---------------------------------------------------------------------------
}// FileListAdapter