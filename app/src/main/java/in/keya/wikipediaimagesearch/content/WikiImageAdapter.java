package in.keya.wikipediaimagesearch.content;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import in.keya.wikipediaimagesearch.R;
import in.keya.wikipediaimagesearch.server.Constants;
import in.keya.wikipediaimagesearch.server.ImageFetcher;
import in.keya.wikipediaimagesearch.server.ResultCallback;

/**
 * Created by keya on 14/04/16.
 */

public class WikiImageAdapter extends BaseAdapter {
    private final Context context;
    private final View.OnClickListener gridListener;
    private List<WikiImage> wikiImages = new ArrayList<>();
    private LayoutInflater inflater;
    private HashMap<String, ImageFetcher> asyncMap = new HashMap<>();
    public WikiImageAdapter(Context context, ArrayList<WikiImage> images, View.OnClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.wikiImages = images;
        this.gridListener = listener;
    }

    @Override
    public int getCount() {
        if (wikiImages != null) return wikiImages.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        if (wikiImages != null) return wikiImages.get(i);
        else return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setImages(ArrayList<WikiImage> images) {
        this.wikiImages = images;
        // If images are set as null, cancel all running async tasks
        if (wikiImages == null && !asyncMap.isEmpty()) {
            Iterator<String> iterator = asyncMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                asyncMap.get(key).cancel(true);
                iterator.remove();
            }
            asyncMap.clear();
        }
    }

    public class ViewHolder{
        ImageView imageHolder;
        ProgressBar progressBar;
        public int position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ImageView picture;
        ProgressBar progressBar;
        ViewHolder viewHolder = new ViewHolder();
        if (view == null) {
            view = inflater.inflate(R.layout.grid_item, viewGroup, false);
            viewHolder.imageHolder= (ImageView) view.findViewById(R.id.picture);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.image_fetching_progress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        picture = viewHolder.imageHolder;
        picture.setImageBitmap(null);
        progressBar = viewHolder.progressBar;
        viewHolder.position = i;

        WikiImage image = (WikiImage) getItem(i);
        Bitmap bitmap = image.getBitmap();
        if (bitmap == null) {
            progressBar.setVisibility(View.VISIBLE);
            String url = image.getThumbnailURL();
            if (url == null) {
                // Use placeholder
                picture.setImageResource(R.drawable.placeholder);
                revealImage(picture, progressBar, true);
            } else {
                boolean isAdded = asyncMap.containsKey(image.getKey());
                if (!isAdded) { // No download of image happening, start now
                    ImageFetcher imageFetcher = new ImageFetcher(imageResultCallback(viewHolder, image), context);
                    imageFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,  new String[]{url});
                    asyncMap.put(image.getKey(), imageFetcher);
                }
            }
        } else {
            picture.setImageBitmap(bitmap);
            revealImage(picture, progressBar, false);
        }

        ViewCompat.setTransitionName(picture, image.getKey());
        picture.setTag(image);
        picture.setOnClickListener(gridListener);

        return view;
    }

    private void revealImage(ImageView picture, ProgressBar progressBar, boolean isAnimation) {
        progressBar.setVisibility(View.GONE);
        if (isAnimation && picture.getVisibility() == View.INVISIBLE)
            revealImageWithAnimation(picture);
        else if (picture.getVisibility() == View.INVISIBLE) {
            picture.setVisibility(View.VISIBLE);
        }
    }

    private ResultCallback<Bitmap> imageResultCallback(final ViewHolder holder, final WikiImage image) {
        return new ResultCallback<Bitmap>() {
            @Override
            public void onResult(Bitmap result, int code) {
                final ImageView imageView = holder.imageHolder;
                holder.progressBar.setVisibility(View.GONE);
                if (asyncMap != null && asyncMap.get(image.getKey()) != null) {
                    asyncMap.remove(image.getKey());
                }
                imageView.setImageBitmap(result);
                image.setBitmap(result);
                if (code == Constants.NETWORK_ERROR_CODE) {
                    Toast.makeText(context, context.getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                }

                revealImageWithAnimation(imageView);
            }
        };
    }

    private void revealImageWithAnimation(final ImageView imageView) {
        imageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                imageView.removeOnLayoutChangeListener(this);
                if (imageView.getVisibility() == View.INVISIBLE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        toggleInformationView(imageView);
                    } else {
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void toggleInformationView(ImageView imageView) {
        // get the center for the clipping circle
        int cx = imageView.getWidth() / 2;
        int cy = imageView.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(imageView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        imageView.setVisibility(View.VISIBLE);
        anim.start();
    }
}

