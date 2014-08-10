package local.stattaker.util;

import local.stattaker.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.TextView;

public class CursorAdapterTeamList extends ResourceCursorAdapter 
{

	public CursorAdapterTeamList(Context context, int layout, Cursor c, int flags) 
	{
		super(context, layout, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		TextView name = (TextView) view.findViewById(R.id.cursor_adapter_team_name);
		name.setText(cursor.getString(cursor.getColumnIndex("teamName")));

	}


}
