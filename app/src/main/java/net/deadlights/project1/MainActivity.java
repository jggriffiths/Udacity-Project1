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
    SearchFragment _fragSearch;
    ArtistFragment _fragArtist;
    PlayTrackFragment _fragPlay;
    Boolean _isPhoneLayout = false;
    private SearchResult _currentSearchResult;
    private ArtistTrack _currentArtistTrack;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        _fragManager = getFragmentManager();
        //Phone UI
        FrameLayout fl = (FrameLayout)findViewById(R.id.flFragmentContainer);
        if (fl != null)
        {
            _fragSearch = new SearchFragment();
            _fragSearch.setOnArtistSelectedListener(this);
            _fragManager.beginTransaction().add(R.id.flFragmentContainer, _fragSearch).commit();
            _isPhoneLayout = true;
        }
        // Tablet UI
        else
        {
            _fragSearch = (SearchFragment)_fragManager.findFragmentById(R.id.fragSearch);
            _fragArtist = (ArtistFragment)_fragManager.findFragmentById(R.id.fragArtist);
            _fragSearch.setOnArtistSelectedListener(this);
            _fragArtist.setOnTrackSelectedListener(this);
            _isPhoneLayout = false;
        }
        this.getActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onArtistSelected(SearchResult r)
    {
        _currentSearchResult = r;
        if (_isPhoneLayout) {
            _fragArtist = new ArtistFragment();
            _fragArtist.setOnTrackSelectedListener(this);

            Bundle args = new Bundle();
            args.putString(ArtistFragment.ARTIST, _currentSearchResult.toJsonString());
            _fragArtist.setArguments(args);

            FragmentTransaction transaction = _fragManager.beginTransaction();
            transaction.replace(R.id.flFragmentContainer, _fragArtist);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else
        {
            _fragArtist.updateArtist(r);
        }
    }

    @Override
    public void onTrackSelected(ArtistTrack t)
    {
        _currentArtistTrack = t;
        _fragPlay = new PlayTrackFragment();
        Bundle args = new Bundle();
        args.putString(PlayTrackFragment.TRACK, _currentArtistTrack.toJsonString());
        args.putString(PlayTrackFragment.ARTIST, _currentSearchResult.toJsonString());
        _fragPlay.setArguments(args);

        FragmentTransaction transaction = _fragManager.beginTransaction();
        transaction.replace(R.id.flFragmentContainer, _fragPlay);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
