package com.ShanghaiWindy.ModInstaller;

import android.content.Intent;
import android.os.Bundle;

import com.ShanghaiWindy.ModInstaller.dummy.LinkContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import androidx.appcompat.widget.Toolbar;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * An activity representing a single DownloadLink detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item description are presented side-by-side with a list of items
 * in a {@link DownloadLinkListActivity}.
 */
public class DownloadLinkDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlink_detail);

        final String itemId = getIntent().getStringExtra(DownloadLinkDetailFragment.ARG_ITEM_ID);
        final LinkContent.LinkItem mItem = LinkContent.ITEM_MAP.get(itemId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (mItem.fileState != Util.FileState.NoFile) {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String gamePath = Util.getGamePath() + mItem.fileName + ".modpack";
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
                final TextView progressText = (TextView) findViewById(R.id.progress);

                Snackbar.make(view, mItem.link, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);

                FileDownloader.getImpl().create(mItem.link).setPath(gamePath).setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                        float progress = (float) soFarBytes / (float) totalBytes;

                        progressText.setText(String.format("%s / %s | %f ", soFarBytes, totalBytes, progress));
                        progressBar.setProgress(Math.round(progressBar.getMax() * progress));
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        progressText.setText(getResources().getText(R.string.DownloadComplete));
                        progressBar.setProgress(progressBar.getMax());

                        Snackbar.make(view, mItem.displayName + getResources().getText(R.string.DownloadComplete), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        progressBar.setProgress(0);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                }).start();
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
