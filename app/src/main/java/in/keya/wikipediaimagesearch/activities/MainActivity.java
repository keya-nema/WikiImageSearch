package in.keya.wikipediaimagesearch.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;

import in.keya.wikipediaimagesearch.R;
import in.keya.wikipediaimagesearch.content.ContentParser;
import in.keya.wikipediaimagesearch.content.IResultReceiver;
import in.keya.wikipediaimagesearch.content.WikiImage;
import in.keya.wikipediaimagesearch.content.WikiImageAdapter;
import in.keya.wikipediaimagesearch.server.ContentFetcher;
import in.keya.wikipediaimagesearch.server.ResultCallback;

public class MainActivity extends Activity implements ResultCallback, IResultReceiver {

    private ContentFetcher task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attachTextWatcher();
    }

    private void attachTextWatcher() {
        EditText homeSearchView = (EditText) findViewById(R.id.home_search);
        homeSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (task != null) {
                    task.cancel(true);
                }
                task = new ContentFetcher(MainActivity.this, MainActivity.this);
                task.execute(ContentFetcher.URL + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onResult(ArrayList<WikiImage> wikiImages) {
        WikiImageAdapter adapter = new WikiImageAdapter(this, wikiImages);
        ((GridView) findViewById(R.id.gridview)).setAdapter(adapter);
    }

    @Override
    public void onResult(Object result) {
        if (result instanceof String) {
            Log.d(getPackageName(), "Result fetched: " + result);
            ContentParser parser = new ContentParser((String) result, this);
            parser.parseResult();
        }
    }
}
