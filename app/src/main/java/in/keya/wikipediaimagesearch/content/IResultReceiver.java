package in.keya.wikipediaimagesearch.content;

import java.util.ArrayList;

/**
 * Created by keya on 14/04/16.
 */
public interface IResultReceiver {

    public void onResult(ArrayList<WikiImage> wikiImages);
}
