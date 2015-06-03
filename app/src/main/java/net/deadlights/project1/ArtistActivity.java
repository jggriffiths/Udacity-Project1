package net.deadlights.project1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;


public class ArtistActivity extends Activity {

    ListView _lvTracks;
    ArtistTrackAdapter _resultAdapter;
    String _artistID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_tracks);

        _lvTracks = (ListView) findViewById(R.id.lvArtistTracks);
        _resultAdapter = new ArtistTrackAdapter(new ArrayList<ArtistTrack>(), this);
        _lvTracks.setAdapter(_resultAdapter);
        Toolbar toolBar = (Toolbar)findViewById(R.id.artist_tracks_toolbar);
        setActionBar(toolBar);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            _artistID = extras.getString(SearchResult.ARTIST_ID);
            if (_artistID != null && _artistID.length() > 0)
            {
                ArtistTracksTask task = new ArtistTracksTask(_resultAdapter);
                task.execute(new String[]{_artistID, this.getResources().getConfiguration().locale.getCountry()});
            }
            String name = extras.getString(SearchResult.ARTIST_NAME);
            if (name != null && name.length() > 0)
            {
                toolBar.setSubtitle(name);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
