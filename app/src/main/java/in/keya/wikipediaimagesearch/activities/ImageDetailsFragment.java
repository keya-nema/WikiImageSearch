package in.keya.wikipediaimagesearch.activities;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.keya.wikipediaimagesearch.R;
import in.keya.wikipediaimagesearch.content.WikiImage;

/**
 * Created by keya on 14/04/16.
 */
public class ImageDetailsFragment extends Fragment {

    private WikiImage image;
    private WikiImage wikiImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_image_view, container, false);
        if (wikiImage != null) {
            Bitmap bitmap = wikiImage.getBitmap();
            ImageView imageView = (ImageView) fragmentView.findViewById(R.id.fragment_image_container).findViewById(R.id.picture);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);

            TextView textView = (TextView) fragmentView.findViewById(R.id.text);
            textView.setText(wikiImage.getTitle());
        }
        return fragmentView;
    }

    public void setWikiImage(WikiImage wikiImage) {
        this.wikiImage = wikiImage;
    }
}