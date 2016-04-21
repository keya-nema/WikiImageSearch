package in.keya.wikipediaimagesearch.activities;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import in.keya.wikipediaimagesearch.R;
import in.keya.wikipediaimagesearch.content.WikiImage;

import static android.support.v4.view.ViewCompat.setTransitionName;

/**
 * Created by keya on 14/04/16.
 */
public class ImageDetailsFragment extends Fragment {

    private WikiImage wikiImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_image_view, container, false);
        if (wikiImage != null) {
            ImageView imageView = (ImageView) fragmentView.findViewById(R.id.detail_picture);
            setTransitionName(imageView, wikiImage.getKey());
            Bitmap bitmap = wikiImage.getBitmap();
            if (bitmap == null) {
                // Show placeholder image
                imageView.setImageResource(R.drawable.placeholder);
            } else {
                imageView.setImageBitmap(bitmap);
            }
            imageView.setVisibility(View.VISIBLE);

            TextView textView = (TextView) fragmentView.findViewById(R.id.text);
            textView.setText(wikiImage.getTitle());

        }
        return fragmentView;
    }

    public void setWikiImage(WikiImage wikiImage) {
        this.wikiImage = wikiImage;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


}
