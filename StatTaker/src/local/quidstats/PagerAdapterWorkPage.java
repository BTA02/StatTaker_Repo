package local.quidstats;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapterWorkPage extends FragmentPagerAdapter
{

	public PagerAdapterWorkPage(FragmentManager fm)
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
				return new FragmentWorkPlayers();
			case 1:
				return new FragmentWorkStats();
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
    			return "Player List";
    		case 1:
    			return "Stats";
    			
    	}
    	return "";
    }


}
