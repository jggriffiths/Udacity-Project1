package net.deadlights.project1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by JGG on 5/31/15.
 */
public class SearchResult
{
    public static final String ARTIST_ID = "ArtistID";
    public static final String ARTIST_NAME = "ArtistName";
    private String _artistName;
    private String _spotifyID;

    // Size to URL mapped values
    private TreeMap<Integer, String> _images;

    public SearchResult(String name, String id)
    {
        _artistName = name;
        _spotifyID = id;
        _images = new TreeMap<Integer, String>();
    }

    public String getArtistName()
    {
        return _artistName;
    }

    public String getSpotifyID()
    {
        return _spotifyID;
    }

    public String getParsedArtistID()
    {
        String[] parts = _spotifyID.split(":");
        return parts[2];
    }

    public String getLargest()
    {
        return _images.get(_images.lastKey());
    }

    public String getSmallest() {
        try
        {
            return _images.get(_images.firstKey());
        }
        catch (NoSuchElementException e)
        {
            return null;
        }
    }

    public void addImages(List<Image> images)
    {
        for(int x = 0; x < images.size(); x++)
        {
            Image i = images.get(x);
            _images.put(i.width, i.url);
        }
    }

    public void addImageFromJson(JSONObject imageObject)
    {
        try
        {
            _images.put(imageObject.getInt("width"), imageObject.getString("url"));
        }
        catch (JSONException e)
        { }
    }

    public static SearchResult getResultFromJson(JSONObject json)
    {
        SearchResult retVal = null;
        try
        {
            retVal = new SearchResult(json.getString("name"), json.getString("uri"));
            JSONArray images = json.getJSONArray("images");
            for(int x = 0; x < images.length(); x++)
            {
                retVal.addImageFromJson(images.getJSONObject(x));
            }
        }
        catch (JSONException e)
        {}
        return retVal;
    }


}
