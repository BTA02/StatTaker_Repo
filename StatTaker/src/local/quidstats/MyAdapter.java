package local.quidstats;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter
{
	public MyAdapter(FragmentManager fm)
	{
		super(fm);
	}

	@Override
	public Fragment getItem(int position) 
	{
		//return ArrayListFragment.newInstance(position);
		
		switch (position)
		{
			case 0:
				return new RecordStatsFragment();
			case 1:
				return new ViewStatsFragment();
		}
		return null;
		
	}

	@Override
	public int getCount() 
	{
		return 2; //is this the number of fragments? looks like it
	}
	
	
}
