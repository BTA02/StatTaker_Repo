package local.quidstats.util;


import local.quidstats.main.GameActivity;
import local.quidstats.main.RecordStatsFragment;
import local.quidstats.R;
import local.quidstats.database.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CursorAdapterOnFieldList extends CursorAdapter
{
	private LayoutInflater mInflater;
	private Context context;

	// ---------------------------------------------------------------------------
	// ---------------------------------------------------------------------------

	public CursorAdapterOnFieldList(Context context_, Cursor c, int flags)
	{
		super(context_, c, flags);
		context = context_;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}// ctor

	// ---------------------------------------------------------------------------

	@Override
	public void bindView(View view, Context context, final Cursor cursor)
	{
		TextView numberTV = (TextView) view.findViewById(R.id.onfield_player_num);
		TextView fnameTV = (TextView) view.findViewById(R.id.onfield_player_fname);
		TextView lnameTV = (TextView) view.findViewById(R.id.onfield_player_lname);
		Button subButton = (Button) view.findViewById(R.id.onfield_player_sub_button);
		subButton.setTag(new Integer(cursor.getPosition()));
		numberTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_NUMBER)));
		fnameTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FNAME)));
		lnameTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_LNAME)));
		
		final GameActivity fragmentAct = (GameActivity) context;
		final RecordStatsFragment pFrag = (RecordStatsFragment) fragmentAct.playerFrag;
		subButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Integer posInt = (Integer)v.getTag();
				int pos = posInt.intValue();
				pFrag.showSubDialog(pos);
			}
			
		});
		
		
		

	}// bindView

	// ---------------------------------------------------------------------------

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup vgroup)
	{
		View view = mInflater.inflate(R.layout.onfield_player_list, null);
		bindView(view, context, cursor);
		return view;
	}// newView
	
	// ---------------------------------------------------------------------------
}// FileListAdapter