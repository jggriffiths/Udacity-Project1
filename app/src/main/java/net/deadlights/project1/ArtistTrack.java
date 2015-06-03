package net.deadlights.project1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by JGG on 5/31/15.
 */
public class ArtistTrack {

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
}
