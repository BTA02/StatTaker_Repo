package local.stattaker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentStats extends Fragment
{
	View rootView;
	
	@Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
  {
		rootView = inflater.inflate(R.layout.fragment_stats, container, false);
		
		
		
		
		
		
		return rootView;
  }
	
	
}
