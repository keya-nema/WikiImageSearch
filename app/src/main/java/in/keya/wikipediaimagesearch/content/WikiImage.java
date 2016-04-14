package in.keya.wikipediaimagesearch.content;

/**
 * Created by keya on 14/04/16.
 */
public class WikiImage {

    String thumbnailURL;
    int width, height;
    String key;
    private String title;

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
}
