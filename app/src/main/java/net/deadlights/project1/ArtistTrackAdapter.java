package net.deadlights.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by JGG on 5/31/15.
 */
public class ArtistTrackAdapter extends ArrayAdapter<ArtistTrack> {

    private List<ArtistTrack> itemList;
    private Context context;

    public ArtistTrackAdapter(List<ArtistTrack> itemList, Context context) {
        super(context, R.layout.artist_track_list_item, itemList);
        this.itemList = itemList;
        this.context = context;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public ArtistTrack getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolderItem viewHolder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.artist_track_list_item, null);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder._txtAlbumTitle = (TextView) v.findViewById(R.id.textAlbumTitle);
            viewHolder._txtTrackTitle = (TextView) v.findViewById(R.id.textTrackTitle);
            viewHolder._imageResult = (ImageView) v.findViewById(R.id.imageResult);

            // store the holder with the view.
            v.setTag(viewHolder);
        }
        else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) v.getTag();
        }

        ArtistTrack result = itemList.get(position);
        viewHolder._txtAlbumTitle.setText(result.getAlbumName());
        viewHolder._txtTrackTitle.setText(result.getTrackName());
        String imageUrl = result.getSmallest();
        if (imageUrl != null && imageUrl.length() > 0) {
            Picasso.with(context).load(result.getSmallest()).into(viewHolder._imageResult);
        }
        return v;

    }

    public List<ArtistTrack> getItemList() {
        return itemList;
    }

    public void setItemList(List<ArtistTrack> itemList) {
        this.itemList = itemList;
    }

    static class ViewHolderItem {
        TextView _txtAlbumTitle;
        TextView _txtTrackTitle;
        ImageView _imageResult;
    }
}
