package local.stattaker;

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
				//return new AccountFragment();
			case 1:
				//return new MarketFragment();
			case 2:
				//return new HoldingsFragment();
			case 3:
				//return new ResearchFragment();
		}
		return null;
		
	}

	@Override
	public int getCount() 
	{
		return 4; //is this the number of fragments? looks like it
	}
	
	
}
