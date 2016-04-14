package in.keya.wikipediaimagesearch.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

import in.keya.wikipediaimagesearch.R;

public class ImageFetcher extends AsyncTask<String, Void, Bitmap> {
  private ResultCallback<Bitmap> bitmapCallback;
  private Context context;

  public ImageFetcher(ResultCallback<Bitmap> bitmapResultCallback, Context context) {
    this.bitmapCallback = bitmapResultCallback;
    this.context = context;
  }

  @Override
  protected Bitmap doInBackground(String... urls) {
    String imageUrl = urls[0];
    if (imageUrl == null) return BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
    Bitmap imageBitmap = downloadImage(imageUrl);
    return imageBitmap;
  }

  private Bitmap downloadImage(String imageUrl) {
    Bitmap imageBitmap = null;

    try {
      URL url = new URL(imageUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      if (connection != null) {
        connection.setReadTimeout(ContentFetcher.HTTP_TIMEOUT);
        connection.connect();

        // read the output from the server
        imageBitmap = BitmapFactory.decodeStream(connection.getInputStream());
        return imageBitmap;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return imageBitmap;
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    super.onPostExecute(bitmap);
    bitmapCallback.onResult(bitmap);
  }
}