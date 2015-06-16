package local.quidstats.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapterMainPage extends FragmentPagerAdapter
{

	public PagerAdapterMainPage(FragmentManager fm)
	{
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position)
	{
		switch (position)
		{
			case 0:
				return new LocalTeamsFragment();
			case 1:
				return new OnlineTeamsFragment();
		}
		return null;
	}

	@Override
	public int getCount()
	{
		return 2;
	}
	
    @Override
    public CharSequence getPageTitle(int position) 
    {
    	switch (position)
    	{
    		case 0:
    			return "Local Teams";
    		case 1:
    			return "Online Teams";
    			
    	}
    	return "";
    }


}
