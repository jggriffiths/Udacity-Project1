package net.deadlights.project1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by JGG on 5/31/15.
 */
public class SearchTask extends AsyncTask<String, Void, List<SearchResult>>
{
        private final String SEARCH_URL = "https://api.spotify.com/v1/search?q={0}&type=artist";
        private SearchAdapter _adapter;
        private TaskResponse _response;
        public SearchTask(SearchAdapter adapter, TaskResponse response)

        {
            _adapter = adapter;
            _response = response;
        }

        @Override
        protected void onPostExecute(List<SearchResult> result) {
            super.onPostExecute(result);
            _adapter.setItemList(result);
            _adapter.notifyDataSetChanged();
            _response.taskFinished();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<SearchResult> doInBackground(String... params)
        {
            List<SearchResult> results = new ArrayList<SearchResult>();
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager pager = spotify.searchArtists((String) params[0]);

            for(int x = 0; x < pager.artists.items.size(); x++)
            {
                Artist a = pager.artists.items.get(x);
                SearchResult r = new SearchResult(a.name, a.uri);
                r.addImages(a.images);
                results.add(r);
            }

            return results;
        }
}
