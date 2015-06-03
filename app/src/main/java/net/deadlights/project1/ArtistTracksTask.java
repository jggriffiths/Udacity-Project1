package net.deadlights.project1;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by JGG on 5/31/15.
 */
public class ArtistTracksTask extends AsyncTask<String, Void, List<ArtistTrack>>
{
        private final String SEARCH_URL = "https://api.spotify.com/v1/artists/{0}/top-tracks?country={1}";

        private ArtistTrackAdapter _adapter;

        public ArtistTracksTask(ArtistTrackAdapter adapter)
        {
            _adapter = adapter;
        }

        @Override
        protected void onPostExecute(List<ArtistTrack> result) {
            super.onPostExecute(result);
            _adapter.setItemList(result);
            _adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ArtistTrack> doInBackground(String... params)
        {
            List<ArtistTrack> results = new ArrayList<ArtistTrack>();


            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Map options = new HashMap<String, String>();
            // FIXIT - get location from device
            options.put("country", Locale.US.getCountry());

            Tracks tracks = spotify.getArtistTopTrack(params[0], options);
            for(int x = 0; x < tracks.tracks.size(); x++)
            {
                Track t = tracks.tracks.get(x);
                ArtistTrack r = new ArtistTrack(t.album.name, t.name, t.preview_url);
                r.addImages(t.album.images);
                results.add(r);
            }

            return results;
        }
}
