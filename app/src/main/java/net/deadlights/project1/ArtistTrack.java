package net.deadlights.project1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by JGG on 5/31/15.
 */
public class ArtistTrack
{
    private String _trackName;
    private String _albumName;
    private String _url;
    private String _spotifyID;
    private long _duration;

    // Size to URL mapped values
    private TreeMap<Integer, String> _images;

    public ArtistTrack(String trackName, String albumName, String spotifyID, long duration, String url)
    {
        _trackName = trackName;
        _albumName = albumName;
        _spotifyID = spotifyID;
        _duration = duration;
        _url = url;
        _images = new TreeMap<Integer, String>();
    }

    public String getTrackName()
    {
        return _trackName;
    }

    public String getAlbumName()
    {
        return _albumName;
    }

    public String getUrl()
    {
        return _url;
    }

    public String getLargest()
    {
        return _images.get(_images.lastKey());
    }

    public String getSpotifyID()
    {
        return _spotifyID;
    }

    public long getDurationMS()
    {
        return _duration;
    }

    public int getDurationSec()
    {
        return (int)_duration / 1000;
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


    public static ArtistTrack fromJsonString(String json)
    {
        try
        {
            JSONObject o = new JSONObject(json);
            String trackName = o.getString("trackName");
            String albumName = o.getString("albumName");
            String spotifyID = o.getString("spotifyID");
            Long duration = o.getLong("duration");
            String url = o.getString("url");

            ArtistTrack retVal = new ArtistTrack(trackName, albumName, spotifyID, duration, url);

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
            retVal.put("trackName", _trackName);
            retVal.put("albumName", _albumName);
            retVal.put("spotifyID", _spotifyID);
            retVal.put("duration", _duration);
            retVal.put("url", _url);
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
