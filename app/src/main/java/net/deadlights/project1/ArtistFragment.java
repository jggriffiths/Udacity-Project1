package net.deadlights.project1;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ArtistFragment extends Fragment
{
    private ListView _lvTracks;
    private ArtistTrackAdapter _resultAdapter;
    private String _artistID;
    private OnTrackSelectedListener _trackSelected;

    public void setOnTrackSelectedListener(OnTrackSelectedListener l)
    {
        _trackSelected = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_artist_tracks, container, false);

        _lvTracks = (ListView) v.findViewById(R.id.lvArtistTracks);
        _resultAdapter = new ArtistTrackAdapter(new ArrayList<ArtistTrack>(), getActivity());
        _lvTracks.setAdapter(_resultAdapter);
        Bundle extras = getArguments();
        if (extras != null)
        {
            _artistID = extras.getString(SearchResult.ARTIST_ID);
            getArtistTracks();
            String name = extras.getString(SearchResult.ARTIST_NAME);
            if (name != null && name.length() > 0)
            {
                getActivity().getActionBar().setTitle(R.string.top_ten);
                getActivity().getActionBar().setSubtitle(name);
            }
        }
        return v;
    }

    private void getArtistTracks() {
        if (_artistID != null && _artistID.length() > 0) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Map options = new HashMap<String, String>();
            options.put("country", this.getResources().getConfiguration().locale.getCountry());
            final List<ArtistTrack> results = new ArrayList<ArtistTrack>();
            spotify.getArtistTopTrack(_artistID, options, new Callback<Tracks>() {
                @Override
                public void success(Tracks tracks, Response response) {

                    for (int x = 0; x < tracks.tracks.size(); x++) {
                        Track t = tracks.tracks.get(x);
                        ArtistTrack r = new ArtistTrack(t.album.name, t.name, t.uri, t.duration_ms, t.preview_url);
                        r.addImages(t.album.images);
                        results.add(r);
                    }
                    setAdapter(results);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private void setAdapter(final List<ArtistTrack> results)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _resultAdapter.setItemList(results);
                _resultAdapter.notifyDataSetChanged();
            }
        });
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