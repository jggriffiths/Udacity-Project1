package net.deadlights.project1;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Adapter;

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

        public SearchTask(SearchAdapter adapter)
        {
            _adapter = adapter;
        }

        @Override
        protected void onPostExecute(List<SearchResult> result) {
            super.onPostExecute(result);
            _adapter.setItemList(result);
            _adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<SearchResult> doInBackground(String... params)
        {
            List<SearchResult> results = new ArrayList<SearchResult>();

           /* HttpURLConnection urlConnection = null;
            try {
                URL searchURL = new URL(String.format(SEARCH_URL, URLEncoder.encode(params[0], "UTF-8")));
                urlConnection = (HttpURLConnection)searchURL.openConnection();
                urlConnection.setConnectTimeout(60000);
                urlConnection.setReadTimeout(90000);

                int status = urlConnection.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK)
                {
                    // create JSON object from content
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }

                    JSONObject jo = new JSONObject(sb.toString());
                    JSONArray artists = jo.getJSONArray("items");
                    for (int x = 0; x < artists.length(); x++) {
                        results.add(SearchResult.getResultFromJson(artists.getJSONObject(x)));
                    }
                }

            } catch (MalformedURLException e) {
                // URL is invalid
            } catch (SocketTimeoutException e) {
                // data retrieval or connection timed out
            } catch (IOException e) {
                // could not read response body
                // (could not create input stream)
            } catch (JSONException e) {
                // response body is no valid JSON string
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }*/

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
