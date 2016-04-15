package in.keya.wikipediaimagesearch.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import in.keya.wikipediaimagesearch.R;

/**
 * Created by keya on 14/04/16.
 */

public class ImageFetcher extends AsyncTask<String, Void, Bitmap> {
  private ResultCallback<Bitmap> bitmapCallback;
  private Context context;
  private int code;

  public ImageFetcher(ResultCallback<Bitmap> bitmapResultCallback, Context context) {
    this.bitmapCallback = bitmapResultCallback;
    this.context = context;
  }

  @Override
  protected Bitmap doInBackground(String... urls) {
    String imageUrl = urls[0];
    if (imageUrl == null) return BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
    Log.d(context.getPackageName(), "Downloading file: "+ imageUrl);
    Bitmap imageBitmap = downloadImage(imageUrl);
    if (isCancelled()) return null;
    return imageBitmap;
  }

  private Bitmap downloadImage(String imageUrl) {
    Bitmap imageBitmap = null;

    try {
      URL url = new URL(imageUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(Constants.METHOD_TYPE_GET);

      if (connection != null) {
        connection.setReadTimeout(Constants.HTTP_TIMEOUT);
        connection.connect();

        // read the output from the server
        imageBitmap = BitmapFactory.decodeStream(connection.getInputStream());

        if (connection != null) {
          code = connection.getResponseCode();
        } else {
          code = Constants.NETWORK_ERROR_CODE;
        }
        return imageBitmap;
      }

    } catch (Exception e) {
      //e.printStackTrace();
      Log.d(context.getPackageName(), "Error downloading image: " + e.getMessage());
      code = Constants.NETWORK_ERROR_CODE;
    }
    return imageBitmap;
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    super.onPostExecute(bitmap);
    bitmapCallback.onResult(bitmap, code);
  }
}