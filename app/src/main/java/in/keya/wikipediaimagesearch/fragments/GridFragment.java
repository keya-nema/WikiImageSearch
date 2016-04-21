package in.keya.wikipediaimagesearch.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;

import java.util.ArrayList;

import in.keya.wikipediaimagesearch.R;
import in.keya.wikipediaimagesearch.activities.ImageDetailsFragment;
import in.keya.wikipediaimagesearch.activities.MainActivity;
import in.keya.wikipediaimagesearch.content.WikiImage;
import in.keya.wikipediaimagesearch.content.WikiImageAdapter;
import in.keya.wikipediaimagesearch.server.Constants;

/**
 * Created by keya on 15/04/16.
 */
public class GridFragment extends Fragment implements View.OnClickListener {

    private WikiImageAdapter adapter;
    private ArrayList<WikiImage> wikiImages;
    private GridView gridView;

    public GridFragment() {}

    public void setWikiImages(ArrayList<WikiImage> wikiImages) {
        this.wikiImages = wikiImages;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_grid, container, false);
        gridView = (GridView) fragmentView.findViewById(R.id.gridview);
        gridView.setVisibility(View.VISIBLE);
        adapter = new WikiImageAdapter(getActivity(), wikiImages, this);
        gridView.setAdapter(adapter);
        return fragmentView;
    }

    public void setWikiImages(ArrayList<WikiImage> wikiImages, boolean update) {
        if (update) {
            setWikiImages(wikiImages);
            adapter = new WikiImageAdapter(getActivity(), wikiImages, this);
            gridView.setAdapter(adapter);
            gridView.setVisibility(View.VISIBLE);
        }
    }

    public void clearAdapter() {
        if (adapter != null) {
            adapter.setImages(null);
            adapter.notifyDataSetChanged();
            gridView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        ImageDetailsFragment fragment = new ImageDetailsFragment();

        WikiImage wikiImage = (WikiImage) v.getTag();
        fragment.setWikiImage(wikiImage);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transition));
            fragment.setEnterTransition(new Fade().setDuration(Constants.ANIMATION_DURATION));
            //setExitTransition(new Fade().setDuration(Constants.ANIMATION_DURATION));
            setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transition)/*new Fade().setDuration(Constants.ANIMATION_DURATION)*/);

            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container_frame_layout, fragment)
                    .addToBackStack(Constants.BACK_STACK_NAME)
                    .addSharedElement(v, wikiImage.getKey())
                    .commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.container_frame_layout, fragment).addToBackStack(Constants.BACK_STACK_NAME).commit();
        }
        ((MainActivity) getActivity()).toggleToolbar(false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
