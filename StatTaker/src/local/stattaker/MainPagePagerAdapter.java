package local.stattaker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagePagerAdapter extends FragmentPagerAdapter
{

	public MainPagePagerAdapter(FragmentManager fm)
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
				return new FragmentTeamsLocal();
			case 1:
				return new FragmentTeamsOnline();
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
