package in.keya.wikipediaimagesearch.content;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by keya on 14/04/16.
 */
public class ContentParser {
    private final String result;
    private final IResultReceiver receiver;
    private static final String PAGES = "pages", TITLE = "title", THUMBNAIL = "thumbnail", SOURCE = "source", QUERY = "query", WIDTH = "width", HEIGHT = "height";
    private ArrayList<WikiImage> wikiImages;

    public ContentParser(String result, in.keya.wikipediaimagesearch.content.IResultReceiver receiver) {
        this.result = result;
        this.receiver = receiver;
    }

    public void parseResult() {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject != null) {
                JSONObject queryObject = jsonObject.optJSONObject(QUERY);
                if (queryObject != null) {
                    JSONObject pagesObject = queryObject.optJSONObject(PAGES);
                    wikiImages = new ArrayList<>();
                    for (Iterator iterator = pagesObject.keys(); iterator.hasNext(); ) {
                        String key = (String) iterator.next();
                        Log.d("in.keya", "Key: " + key);
                        WikiImage image = new WikiImage();
                        JSONObject keyObject = pagesObject.getJSONObject(key);
                        // Key
                        image.setKey(key);
                        // Title
                        String title = keyObject.getString(TITLE);
                        image.setTitle(title);
                        // Thumbnail
                        JSONObject thumbnailObject = keyObject.optJSONObject(THUMBNAIL);
                        if (thumbnailObject != null) {
                            // Thumbnail:URL
                            String sourceURL = thumbnailObject.getString(SOURCE);
                            image.setThumbnailURL(sourceURL);
                            // Thumbnail:Width
                            image.setWidth(thumbnailObject.getInt(WIDTH));
                            // Thumbnail:Height
                            image.setHeight(thumbnailObject.getInt(HEIGHT));
                        }
                        wikiImages.add(image);
                    }
                    receiver.onResult(wikiImages);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
