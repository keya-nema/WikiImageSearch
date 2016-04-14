package in.keya.wikipediaimagesearch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;

import in.keya.wikipediaimagesearch.R;
import in.keya.wikipediaimagesearch.content.ContentParser;
import in.keya.wikipediaimagesearch.content.IResultReceiver;
import in.keya.wikipediaimagesearch.content.WikiImage;
import in.keya.wikipediaimagesearch.content.WikiImageAdapter;
import in.keya.wikipediaimagesearch.server.ContentFetcher;
import in.keya.wikipediaimagesearch.server.ResultCallback;

public class MainActivity extends AppCompatActivity implements ResultCallback, IResultReceiver {

    private ContentFetcher task;
    private EditText homeSearchView;
    private WikiImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeSearchView = (EditText) findViewById(R.id.home_search);
        attachTextWatcher();
        attachClearText();
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
    }

    private void attachClearText() {
        ImageButton clearTextButton = (ImageButton) findViewById(R.id.clear_text);
        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeSearchView.setText("");
                clearAdapter();
            }
        });
    }

    private void clearAdapter() {
        if (adapter != null) {
            adapter.setImages(null);
            adapter.notifyDataSetChanged();
        }
    }

    private void attachTextWatcher() {
        homeSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (task != null) {
                    task.cancel(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) {
                    clearAdapter();
                    task = new ContentFetcher(MainActivity.this, MainActivity.this);
                    task.execute(ContentFetcher.URL + s);
                }
            }
        });
    }

    @Override
    public void onResult(ArrayList<WikiImage> wikiImages) {
        adapter = new WikiImageAdapter(this, wikiImages);
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
