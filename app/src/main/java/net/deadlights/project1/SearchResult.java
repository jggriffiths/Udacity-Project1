package net.deadlights.project1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
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

    public void addImage(int size, String url)
    {
        _images.put(size, url);
    }


    public static SearchResult fromJsonString(String json)
    {
        try
        {
            JSONObject o = new JSONObject(json);
            String artistName = o.getString("artistName");
            String spotifyID = o.getString("spotifyID");

            SearchResult retVal = new SearchResult(artistName, spotifyID);

            JSONArray images = o.getJSONArray("images");
            for(int x = 0; x < images.length(); x++)
            {
                JSONObject image = images.getJSONObject(x);
                retVal.addImage(image.getInt("size"), image.getString("url"));
            }

            return retVal;
        }
        catch(JSONException e)
        {
            return null;
        }
    }

    public String toJsonString()
    {
        try
        {
            JSONObject retVal = new JSONObject();
            retVal.put("artistName", _artistName);
            retVal.put("spotifyID", _spotifyID);
            JSONArray images = new JSONArray();
            for (Map.Entry<Integer, String> entry : _images.entrySet())
            {
                Integer size = entry.getKey();
                String url = entry.getValue();
                JSONObject image = new JSONObject();
                image.put("size", size);
                image.put("url", url);
                images.put(image);
            }
            retVal.put("images", images);
            return retVal.toString();
        }
        catch (JSONException e)
        {
            return "";
        }
    }


}
