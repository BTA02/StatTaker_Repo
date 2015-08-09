package local.quidstats.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import local.quidstats.R;
import local.quidstats.database.PlayerDb;

public class VideoOnFieldAdapter extends ArrayAdapter {

    private Context context;
    private List<PlayerDb> items;
    private LayoutInflater vi;


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public VideoOnFieldAdapter(Context context, List<PlayerDb> items) {
        super(context,0, items);
        this.context = context;
        this.items = items;
        vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {
            v = vi.inflate(R.layout.video_on_field_layout, null);
            holder = new ViewHolder();
            if (position == 0) {
                holder.dividerLayout = (LinearLayout) v.findViewById(R.id.video_on_field_divider);
                holder.dividerLayout.setVisibility(View.VISIBLE);
                holder.visibility = View.VISIBLE;
                holder.dividerTitle = (TextView) v.findViewById(R.id.video_on_field_divider_title);
                holder.dividerTitle.setText("CHASERS");
            } else if (position == 3) {
                holder.dividerLayout = (LinearLayout) v.findViewById(R.id.video_on_field_divider);
                holder.dividerLayout.setVisibility(View.VISIBLE);
                holder.visibility = View.VISIBLE;
                holder.dividerTitle = (TextView) v.findViewById(R.id.video_on_field_divider_title);
                holder.dividerTitle.setText("KEEPER");
            } else if (position == 4) {
                holder.dividerLayout = (LinearLayout) v.findViewById(R.id.video_on_field_divider);
                holder.dividerLayout.setVisibility(View.VISIBLE);
                holder.visibility = View.VISIBLE;
                holder.dividerTitle = (TextView) v.findViewById(R.id.video_on_field_divider_title);
                holder.dividerTitle.setText("BEATERS");
            } else if (position == 6) {
                holder.dividerLayout = (LinearLayout) v.findViewById(R.id.video_on_field_divider);
                holder.dividerLayout.setVisibility(View.VISIBLE);
                holder.visibility = View.VISIBLE;
                holder.dividerTitle = (TextView) v.findViewById(R.id.video_on_field_divider_title);
                holder.dividerTitle.setText("SEEKER");
            }
            holder.playerName = (TextView) v.findViewById(R.id.video_on_field_name);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        PlayerDb player = items.get(position);
        if (player != null) {
            holder.playerName.setText(player.toString());
        }
        return v;
    }

    static class ViewHolder {
        int visibility;
        LinearLayout dividerLayout;
        TextView dividerTitle;
        TextView playerName;
    }
}