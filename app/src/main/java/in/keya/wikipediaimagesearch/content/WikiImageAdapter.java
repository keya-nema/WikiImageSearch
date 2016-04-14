package in.keya.wikipediaimagesearch.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.keya.wikipediaimagesearch.R;
import in.keya.wikipediaimagesearch.server.ImageFetcher;
import in.keya.wikipediaimagesearch.server.ResultCallback;

/**
 * Created by keya on 14/04/16.
 */

public class WikiImageAdapter extends BaseAdapter {
    private final Context context;
    private List<WikiImage> wikiImages = new ArrayList<WikiImage>();
    private LayoutInflater inflater;

    public WikiImageAdapter(Context context, ArrayList<WikiImage> images) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.wikiImages = images;
    }

    @Override
    public int getCount() {
        return wikiImages.size();
    }

    @Override
    public Object getItem(int i) {
        return wikiImages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder{
        ImageView imageHolder;
        //TextView textHolder;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView picture;
        //TextView name;
        ViewHolder viewHolder = new ViewHolder();
        if (view == null) {
            view = inflater.inflate(R.layout.grid_item, viewGroup, false);
            viewHolder.imageHolder= (ImageView) view.findViewById(R.id.picture);
            //viewHolder.textHolder = (TextView) view.findViewById(R.id.text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        picture = viewHolder.imageHolder;
//        name = viewHolder.textHolder;

        WikiImage image = (WikiImage) getItem(i);

        ImageFetcher imageFetcher = new ImageFetcher(imageResultCallback(picture), context);
        imageFetcher.execute(image.getThumbnailURL());
//        name.setText(image.getTitle());

        return view;
    }

    private ResultCallback<Bitmap> imageResultCallback(final ImageView imageView) {
        return new ResultCallback<Bitmap>() {
            @Override
            public void onResult(Bitmap result) {
                imageView.setImageBitmap(result);
            }
        };
    }
}

