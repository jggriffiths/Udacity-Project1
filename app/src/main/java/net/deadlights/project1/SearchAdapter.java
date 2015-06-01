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
 * Created by atropos on 5/31/15.
 */
public class SearchAdapter extends ArrayAdapter<SearchResult> {

    private List<SearchResult> itemList;
    private Context context;

    public SearchAdapter(List<SearchResult> itemList, Context context) {
        super(context, R.layout.search_list_item, itemList);
        this.itemList = itemList;
        this.context = context;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public SearchResult getItem(int position) {
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
            v = inflater.inflate(R.layout.search_list_item, null);

            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder._txtResult = (TextView) v.findViewById(R.id.textResult);
            viewHolder._imageResult = (ImageView) v.findViewById(R.id.imageResult);

            // store the holder with the view.
            v.setTag(viewHolder);
        }
        else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) v.getTag();
        }

        SearchResult result = itemList.get(position);
        viewHolder._txtResult.setText(result.getArtistName());
        Picasso.with(context).load(result.getSmallest()).into(viewHolder._imageResult);

        return v;

    }

    public List<SearchResult> getItemList() {
        return itemList;
    }

    public void setItemList(List<SearchResult> itemList) {
        this.itemList = itemList;
    }

    static class ViewHolderItem {
        TextView _txtResult;
        ImageView _imageResult;
    }
}
