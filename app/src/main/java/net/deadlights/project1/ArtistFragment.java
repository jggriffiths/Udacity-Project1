package net.deadlights.project1;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private SearchResult _artist;
    private OnTrackSelectedListener _trackSelected;
    public static final String ARTIST = "ARTIST";
    public static final String TRACKS = "TRACKS";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARTIST, _artist.toJsonString());
        ArrayList<CharSequence> tracks = new ArrayList<CharSequence>(_resultAdapter.getCount());
        for(int x = 0; x < _resultAdapter.getCount(); x++)
        {
            tracks.add(_resultAdapter.getItem(x).toJsonString());
        }
        outState.putCharSequenceArrayList(TRACKS, tracks);
    }

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
        _lvTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (_trackSelected != null)
                {
                    _trackSelected.onTrackSelected(_resultAdapter.getItem(position));
                }
            }
        });
        Bundle extras = getArguments();
        if (extras != null)
        {
            _artist = SearchResult.fromJsonString(extras.getString(ARTIST));

        }
        else if (savedInstanceState != null)
        {
            _artist = SearchResult.fromJsonString(savedInstanceState.getString(ARTIST));

            ArrayList<CharSequence> tracks = savedInstanceState.getCharSequenceArrayList(TRACKS);
            ArrayList<ArtistTrack> artistTracks = new ArrayList<>();
            for(int x = 0; x < tracks.size(); x++)
            {
                artistTracks.add(ArtistTrack.fromJsonString(tracks.get(x).toString()));
            }
            _resultAdapter.setItemList(artistTracks);
            _resultAdapter.notifyDataSetChanged();
        }

        if (_artist != null) {
            if (_resultAdapter.getCount() == 0) {
                getArtistTracks();
            }
            if (_artist.getArtistName().length() > 0) {
                getActivity().getActionBar().setTitle(R.string.top_ten);
                getActivity().getActionBar().setSubtitle(_artist.getArtistName());
            }
        }
        return v;
    }

    public void updateArtist(SearchResult artist)
    {
        _artist = artist;
        getActivity().getActionBar().setSubtitle(_artist.getArtistName());
        getArtistTracks();
    }

    private void getArtistTracks() {
        if (_artist != null && _artist.getParsedArtistID().length() > 0) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Map options = new HashMap<String, String>();
            options.put("country", this.getResources().getConfiguration().locale.getCountry());
            final List<ArtistTrack> results = new ArrayList<ArtistTrack>();
            spotify.getArtistTopTrack(_artist.getParsedArtistID(), options, new Callback<Tracks>() {
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