package local.quidstats.util;

import local.quidstats.R;
import local.quidstats.database.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CursorAdapterTeamList extends CursorAdapter
{
  private LayoutInflater mInflater;
  private Context context;

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------

  public CursorAdapterTeamList(Context context_, Cursor c, int flags)
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
    TextView fileNameTV = (TextView) view.findViewById(R.id.cursor_adapter_team_name);
    fileNameTV.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_TEAM_NAME)));
    
  }// bindView

  // ---------------------------------------------------------------------------

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup vgroup)
  {
    View view = mInflater.inflate(R.layout.team_list_cursor_adapter, null);
    bindView(view, context, cursor);
    return view;
  }// newView
  
}// FileListAdapter