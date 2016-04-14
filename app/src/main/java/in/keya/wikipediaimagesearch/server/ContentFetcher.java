package in.keya.wikipediaimagesearch.server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import in.keya.wikipediaimagesearch.activities.MainActivity;

/**
 * Created by keya on 13/04/16.
 */
public class ContentFetcher extends AsyncTask<String, Void, String> {
    private static final String METHOD_TYPE_GET = "GET";
    private final ResultCallback callback;
    private final MainActivity context;
    private HttpURLConnection connection;
    private BufferedReader in;
    public static final int HTTP_TIMEOUT = 15 * 1000; // milliseconds
    public static String URL = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=200&pilimit=50&generator=prefixsearch&gpssearch=";

    public ContentFetcher(MainActivity context, ResultCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(METHOD_TYPE_GET);

            if (connection != null) {
                connection.setReadTimeout(HTTP_TIMEOUT);
                connection.connect();

                // read the output from the server
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                } catch (IOException readerException) {
                    readerException.printStackTrace();
                }
                return result.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (InterruptedIOException iioe) {
            Log.d(context.getPackageName(), "Interrupted");
            iioe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onResult(result);
        Log.d(context.getPackageName(), "Task cancelled");
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(context.getPackageName(), "Task is cancelled");
    }
}
