package in.keya.wikipediaimagesearch.server;

/**
 * Created by keya on 13/04/16.
 */
public interface ResultCallback<T> {

    public void onResult(T result, int code);
}
