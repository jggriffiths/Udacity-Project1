package net.deadlights.project1;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;


public class SearchActivity extends Activity {

    EditText _txtSearch;
    ListView _lvResults;
    SearchAdapter _resultAdapter;
    Button _btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        _lvResults = (ListView) findViewById(R.id.lvSearchResults);
        _txtSearch = (EditText) findViewById(R.id.txtSearch);
       _btnSearch = (Button)findViewById(R.id.btnSearch);
       _btnSearch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               doSearch();
           }
       });
        _resultAdapter = new SearchAdapter(new ArrayList<SearchResult>(), this);
        _lvResults.setAdapter(_resultAdapter);
    }

    private void doSearch()
    {
        SearchTask task = new SearchTask(_resultAdapter);
        task.execute(new String[] {_txtSearch.getText().toString()});
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
