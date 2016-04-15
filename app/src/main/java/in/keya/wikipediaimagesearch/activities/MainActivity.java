package in.keya.wikipediaimagesearch.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    private Toolbar toolbar;
    private View progressBar;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeSearchView = (EditText) findViewById(R.id.home_search);
        progressBar = findViewById(R.id.content_fetching_progress);
        gridView = (GridView) findViewById(R.id.gridview);

        attachTextWatcher();
        attachClearText();
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    private void attachClearText() {
        ImageButton clearTextButton = (ImageButton) findViewById(R.id.clear_text);
        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeSearchView.setText("");
                progressBar.setVisibility(View.GONE);
                clearAdapter();
            }
        });
    }

    private void clearAdapter() {
        if (adapter != null) {
            adapter.setImages(null);
            adapter.notifyDataSetChanged();
            gridView.setVisibility(View.GONE);
        }
    }

    private void attachTextWatcher() {
        homeSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                synchronized (this) {
                    if (task != null) {
                        task.cancel(true);
                    }
                }
            }

            private Timer timer = new Timer();
            private final long DELAY = 500; // milliseconds

            @Override
            public void afterTextChanged(final Editable s) {
                clearAdapter();
                progressBar.setVisibility(View.VISIBLE);
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                if (s != null && s.length() > 0) {

                                    synchronized (this) {
                                        if (task != null) {
                                            task.cancel(true);
                                        }
                                    }

                                    task = new ContentFetcher(MainActivity.this, MainActivity.this);
                                    Log.d(getPackageName(), "Inside textchanged, calling task.execute()...");
                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ContentFetcher.URL + s);
                                }
                            }
                        }, DELAY);
            }
        });
    }

    @Override
    public void onResult(final ArrayList<WikiImage> wikiImages) {
        adapter = new WikiImageAdapter(this, wikiImages);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager.findFragmentById(R.id.container_frame_layout) == null) {
                    WikiImage wikiImage = wikiImages.get(position);
                    ImageDetailsFragment fragment = new ImageDetailsFragment();
                    fragment.setWikiImage(wikiImage);
                    fragmentManager.beginTransaction().add(R.id.container_frame_layout, fragment).commit();
                    if (toolbar != null) {
                        toolbar.setVisibility(View.GONE);
                    }
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                }
            }
        });
    }

    @Override
    public void onResult(Object result) {
        if (result instanceof String) {
            Log.d(getPackageName(), "Result fetched: " + result);
            ContentParser parser = new ContentParser((String) result, this);
            parser.parseResult();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container_frame_layout);
        if (fragment == null) {
            super.onBackPressed();
        } else {
            fragmentManager.beginTransaction().remove(fragment).commit();
            if (toolbar != null) {
                toolbar.setVisibility(View.VISIBLE);
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
