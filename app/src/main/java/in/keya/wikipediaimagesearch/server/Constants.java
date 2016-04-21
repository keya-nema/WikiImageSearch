package in.keya.wikipediaimagesearch.server;

/**
 * Created by keya on 15/04/16.
 */
public class Constants {
    public static final String METHOD_TYPE_GET = "GET";
    public static final int NETWORK_ERROR_CODE = 999;
    public static final int HTTP_TIMEOUT = 15 * 1000; // milliseconds
    public static String URL = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=200&pilimit=50&generator=prefixsearch&gpssearch=";

    public static int ANIMATION_DURATION = 500;
    public static String BACK_STACK_NAME = "details";
}
