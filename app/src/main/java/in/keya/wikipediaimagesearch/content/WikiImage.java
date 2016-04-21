package in.keya.wikipediaimagesearch.content;

import android.graphics.Bitmap;

/**
 * Created by keya on 14/04/16.
 */
public class WikiImage {

    String thumbnailURL;
    int width, height;
    String key;
    private String title;
    private Bitmap bitmap;
    private boolean isDrawn;

    public void setThumbnailURL(String url) { this.thumbnailURL = url; }

    public String getThumbnailURL() { return thumbnailURL; }

    public void setWidth(int width) { this.width = width; }

    public int getWidth() { return width; }

    public void setHeight(int height) { this.height = height; }

    public int getHeight() { return height; }

    public void setKey(String key) { this.key = key; }

    public String getKey() { return key; }

    public void setTitle(String title) { this.title = title; }

    public String getTitle() { return title; }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() { return bitmap; }

    // This is to avoid re-animating the images
    public void setIsDrawn(boolean isDrawn) {
        this.isDrawn = isDrawn;
    }

    public boolean isDrawn() {
        return isDrawn;
    }
}
