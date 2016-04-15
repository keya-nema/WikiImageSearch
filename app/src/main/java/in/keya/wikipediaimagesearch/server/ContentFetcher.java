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
    private final ResultCallback callback;
    private final MainActivity context;
    private HttpURLConnection connection;
    private BufferedReader in;
    private int code;

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
            connection.setRequestMethod(Constants.METHOD_TYPE_GET);

            if (connection != null) {
                connection.setReadTimeout(Constants.HTTP_TIMEOUT);
                connection.connect();

                // read the output from the server
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                try {
                    if (!isCancelled()) {
                        while ((line = in.readLine()) != null) {
                            result.append(line);
                        }
                    }
                } catch (IOException readerException) {
                    readerException.printStackTrace();
                }

                if (connection != null) {
                    code = connection.getResponseCode();
                } else {
                    code = Constants.NETWORK_ERROR_CODE;
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
            //e.printStackTrace();
            code = Constants.NETWORK_ERROR_CODE;
            Log.d(context.getPackageName(), "Error fetching content: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        callback.onResult(result, code);
        Log.d(context.getPackageName(), "Task cancelled");
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(context.getPackageName(), "Task is cancelled");
    }
}
