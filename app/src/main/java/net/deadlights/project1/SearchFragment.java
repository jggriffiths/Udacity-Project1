package net.deadlights.project1;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchFragment extends Fragment
{
    EditText _txtSearch;
    ListView _lvResults;
    SearchAdapter _resultAdapter;
    Boolean _badSearch = false;
    android.os.Handler _handler = new android.os.Handler();
    OnArtistSelectedListener _artistSelectedCallback;
    private String _lastSearchTerm = "";
    private Boolean _ignoreTextChange = false;


    public void setOnArtistSelectedListener(OnArtistSelectedListener l)
    {
        _artistSelectedCallback = l;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<CharSequence> artists = new ArrayList<CharSequence>(_resultAdapter.getCount());
        for(int x = 0; x < _resultAdapter.getCount(); x++)
        {
            artists.add(_resultAdapter.getItem(x).toJsonString());
        }
        outState.putCharSequenceArrayList("ARTISTS", artists);
        outState.putString("SEARCH_TERM", _txtSearch.getText().toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.frag_search, container, false);

        getActivity().getActionBar().setTitle(R.string.app_name);
        getActivity().getActionBar().setSubtitle(R.string.search_artist);

        _lvResults = (ListView) v.findViewById(R.id.lvSearchResults);
        _txtSearch = (EditText) v.findViewById(R.id.txtSearch);
        _resultAdapter = new SearchAdapter(new ArrayList<SearchResult>(), getActivity());
        _lvResults.setAdapter(_resultAdapter);
        _lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SearchResult result = _resultAdapter.getItem(position);
                if (_artistSelectedCallback != null)
                {
                    _artistSelectedCallback.onArtistSelected(result);
                }
            }
        });



        if (savedInstanceState != null)
        {
            ArrayList<CharSequence> artists = savedInstanceState.getCharSequenceArrayList("ARTISTS");
            ArrayList<SearchResult> results = new ArrayList<>();
            for(int x = 0; x < artists.size(); x++)
            {
                results.add(SearchResult.fromJsonString(artists.get(x).toString()));
            }
            _resultAdapter.setItemList(results);
            _resultAdapter.notifyDataSetChanged();
            _lastSearchTerm = savedInstanceState.getString("SEARCH_TERM");
            _ignoreTextChange = true;
            _txtSearch.setText(_lastSearchTerm);
        }

        _txtSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                _handler.removeCallbacksAndMessages(null);
                if (s.length() > 2 && !_lastSearchTerm.equals(_txtSearch.getText().toString()))
                {
                    _handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            doSearch();
                        }
                    }, 1000);
                }
                else if (_resultAdapter.getCount() > 0)
                {
                    if (!_ignoreTextChange) {
                        clearAdapter();
                        _badSearch = false;
                    }
                    else
                    {
                        _ignoreTextChange = false;
                    }
                }
            }
        });

        return v;
    }

    private void doSearch()
    {
        Log.d(this.getClass().getSimpleName(), "Doing search.");
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        spotify.searchArtists(_txtSearch.getText().toString(), new Callback<ArtistsPager>()
        {
            @Override
            public void success(ArtistsPager artistsPager, Response response)
            {
                _lastSearchTerm = _txtSearch.getText().toString();
                Pager<Artist> artists = artistsPager.artists;
                final ArrayList<SearchResult> results = new ArrayList<SearchResult>();
                for (int x = 0;
                     x < artists.items.size();
                     x++)
                {
                    Artist a = artists.items.get(x);
                    SearchResult r = new SearchResult(a.name, a.uri);
                    r.addImages(a.images);
                    results.add(r);
                }
                setAdapter(results);
            }

            @Override
            public void failure(RetrofitError error)
            {
                emptySearch();
            }
        });
    }

    private void clearAdapter()
    {
        final ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        setAdapter(results);
    }

    private void setAdapter(final List<SearchResult> results)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                _resultAdapter.setItemList(results);
                _resultAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void emptySearch()
    {
        if (!_badSearch)
        {
            _badSearch = true;
            Toast.makeText(getActivity(), getString(R.string.no_artists), Toast.LENGTH_SHORT).show();
        }
        else if (_resultAdapter.getCount() > 0)
        {
            _badSearch = false;
        }
    }
}
