package in.keya.wikipediaimagesearch.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import in.keya.wikipediaimagesearch.fragments.GridFragment;
import in.keya.wikipediaimagesearch.server.Constants;
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
                clearFragmentGridAdapter();
            }
        });
    }

    private void clearFragmentGridAdapter() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.container_frame_layout);
        if (fragment instanceof GridFragment) {
            ((GridFragment) fragment).clearAdapter();
        }
    }

    private void attachTextWatcher() {
        homeSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
                clearFragmentGridAdapter();
                if (s != null && s.length() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    synchronized (this) {
                                        if (task != null) {
                                            task.cancel(true);
                                        }
                                    }

                                    task = new ContentFetcher(MainActivity.this, MainActivity.this);
                                    Log.d(getPackageName(), "Inside textchanged, calling task.execute()...");
                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.URL + s);
                                }
                            }, DELAY);
                } else {
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public void onResult(final ArrayList<WikiImage> wikiImages) {
        // Add the grid fragment here
        Log.d(getPackageName(), "Adding grid fragment");
        progressBar.setVisibility(View.GONE);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentById(R.id.container_frame_layout) == null) {
            GridFragment fragment = new GridFragment();
            fragment.setWikiImages(wikiImages);
            fragmentManager.beginTransaction().add(R.id.container_frame_layout, fragment).commit();
        } else {
            Fragment fragment = fragmentManager.findFragmentById(R.id.container_frame_layout);
            if (fragment instanceof GridFragment) {
                ((GridFragment) fragment).setWikiImages(wikiImages, true);
            }
        }
    }

    @Override
    public void onResult(Object result, int code) {
        if (result instanceof String) {
            Log.d(getPackageName(), "Result fetched: " + result);
            ContentParser parser = new ContentParser((String) result, this);
            parser.parseResult();
        } else if (result == null) {
            // Show an error dialog
            String message = "";
            if (code == Constants.NETWORK_ERROR_CODE) {
                message = getString(R.string.network_error);
            } else {
                message = getString(R.string.error_message);
            }
            showErrorDialog(message, code);
            clearFragmentGridAdapter();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showErrorDialog(String message, final int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(getString(R.string.error))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (code == Constants.NETWORK_ERROR_CODE) {
                            finish();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    FragmentManager fragmentManager = getFragmentManager();

    @Override
    public void onBackPressed() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.details_frame_layout);
        if (fragment == null) {
            super.onBackPressed();
        } else {
            fragmentManager.beginTransaction().remove(fragment).commit();
            toggleToolbar(true);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void toggleToolbar(boolean visible) {
        if (toolbar != null && visible) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }
}
