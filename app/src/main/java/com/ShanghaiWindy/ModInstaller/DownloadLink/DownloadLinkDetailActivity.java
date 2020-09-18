package com.ShanghaiWindy.ModInstaller.DownloadLink;

import android.content.Intent;
import android.os.Bundle;

import com.ShanghaiWindy.ModInstaller.R;
import com.ShanghaiWindy.ModInstaller.Util;
import com.ShanghaiWindy.ModInstaller.Dummy.ModLinkContent;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import androidx.appcompat.widget.Toolbar;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

/**
 * An activity representing a single DownloadLink detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item description are presented side-by-side with a list of items
 * in a {@link DownloadLinkListActivity}.
 */
public class DownloadLinkDetailActivity extends AppCompatActivity {
    protected static Hashtable<String, Integer> downloadTasks = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlink_detail);

        final String itemId = getIntent().getStringExtra(DownloadLinkDetailFragment.ARG_ITEM_ID);
        final ModLinkContent.ModLinkItem mItem = ModLinkContent.ITEM_MAP.get(itemId);

        final String gamePath = Util.getGamePath();

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        final MaterialButton fab = (MaterialButton) findViewById(R.id.fab);

        if (mItem.fileState != Util.FileState.NoFile) {
            fab.setVisibility(View.GONE);
        }

        if (downloadTasks.containsKey(mItem.link)) {
            fab.setVisibility(View.GONE);
            int id = downloadTasks.get(mItem.link);
            long soFar = FileDownloader.getImpl().getSoFar(id);
            long total = FileDownloader.getImpl().getTotal(id);

            if (FileDownloader.getImpl().getStatusIgnoreCompleted(id) != FileDownloadStatus.INVALID_STATUS) {
                Toast.makeText(this, String.format("Resume: %d / %d", soFar, total), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, mItem.displayName + getResources().getText(R.string.DownloadComplete), Toast.LENGTH_SHORT).show();
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
                final TextView progressText = (TextView) findViewById(R.id.progress);

                Snackbar.make(view, mItem.link, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);

                final BaseDownloadTask downloadTask = FileDownloader.getImpl().create(mItem.link).setPath(gamePath, true);

                downloadTask.setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        if (progressText != null) {
                            progressText.setText("Pending...");
                        }
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        float progress = (float) soFarBytes / (float) totalBytes;
                        float speed = (float) downloadTask.getSpeed();

                        if (progressText != null && progressBar != null) {
                            progressText.setText(String.format("%s / %s | %f | %f KB/s", soFarBytes, totalBytes, progress, speed));
                            progressBar.setProgress(Math.round(progressBar.getMax() * progress));
                        }

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        if (progressText != null && progressBar != null) {
                            progressText.setText(getResources().getText(R.string.DownloadComplete));
                            progressBar.setProgress(progressBar.getMax());

                            Snackbar.make(view, mItem.displayName + getResources().getText(R.string.DownloadComplete), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Snackbar.make(view, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                });

                downloadTask.start();

                downloadTasks.put(mItem.link, downloadTask.getId());
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(DownloadLinkDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(DownloadLinkDetailFragment.ARG_ITEM_ID));
            DownloadLinkDetailFragment fragment = new DownloadLinkDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.downloadlink_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more description, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, DownloadLinkListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
