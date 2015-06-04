package net.deadlights.project1;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;


/**
 * Created by JGG on 6/4/15.
 */
public class MainActivity extends Activity implements OnTrackSelectedListener, OnArtistSelectedListener
{
    FragmentManager _fragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        _fragManager = getFragmentManager();
        FrameLayout fl = (FrameLayout)findViewById(R.id.flFragmentContainer);
        if (fl != null)
        {
            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setOnArtistSelectedListener(this);
            _fragManager.beginTransaction().add(R.id.flFragmentContainer, searchFragment).commit();
        }
        this.getActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onArtistSelected(SearchResult r)
    {
        ArtistFragment artistFragment = new ArtistFragment();
        artistFragment.setOnTrackSelectedListener(this);

        Bundle args = new Bundle();
        args.putString(SearchResult.ARTIST_ID, r.getParsedArtistID());
        args.putString(SearchResult.ARTIST_NAME, r.getArtistName());
        artistFragment.setArguments(args);

        FragmentTransaction transaction = _fragManager.beginTransaction();
        transaction.replace(R.id.flFragmentContainer, artistFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onTrackSelected(ArtistTrack t)
    {

    }
}
