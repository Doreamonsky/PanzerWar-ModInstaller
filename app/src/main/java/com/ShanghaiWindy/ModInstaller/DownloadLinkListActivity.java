package com.ShanghaiWindy.ModInstaller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ShanghaiWindy.ModInstaller.dummy.LinkContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * An activity representing a list of DownloadLinks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DownloadLinkDetailActivity} representing
 * item description. On tablets, the activity presents the list of items and
 * item description side-by-side using two vertical panes.
 */
public class DownloadLinkListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlink_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RefreshList();
                Snackbar.make(view, getResources().getText(R.string.LoadingList), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.downloadlink_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        final View recyclerView = findViewById(R.id.downloadlink_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        Snackbar.make(recyclerView, getResources().getText(R.string.LoadingList), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        RefreshList();
    }

    private void RefreshList() {

        LinkContent.removeAll();
        simpleItemRecyclerViewAdapter.notifyDataSetChanged();

        // 请求获取json
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        List<String> reporisties = Arrays.asList("https://res.waroftanks.cn/", "https://baomage.github.io/BaoMaGe/");


        for (Iterator<String> it = reporisties.iterator(); it.hasNext(); ) {
            final String jsonUrl = it.next();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonUrl + "Source.json", new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray downloadLinks = response.getJSONArray("downloadLinks");

                        for (int i = 0; i < downloadLinks.length(); i++) {
                            JSONObject downloadLink = downloadLinks.getJSONObject(i);
                            String packName = downloadLink.getString("packName");
                            String link = jsonUrl + downloadLink.getString("link");

                            String editTime = downloadLink.getString("editTime");
                            editTime = getResources().getText(R.string.editTime) + editTime;

                            String size = downloadLink.getString("size");
                            size = getResources().getText(R.string.size) + size + "mb";

                            String description = downloadLink.getString("description");
                            description = getResources().getText(R.string.description) + description;

                            String author = downloadLink.getString("author");
                            author = getResources().getText(R.string.author) + author;

                            if (!packName.contains("Android")) {
                                continue;
                            }

                            // 过滤文件夹名字
                            String displayName = packName.replace("Android_", "");
                            displayName = displayName.replace("_modpack", "");
                            displayName = displayName.replace("Vehicle-", "");


                            Util.FileState fileState = Util.getModFileState(packName);
                            int installStateText = R.string.NoFolder;

                            switch (fileState) {
                                case NoFolder:
                                    installStateText = R.string.NoFolder;
                                    break;
                                case NoFile:
                                    installStateText = R.string.NoFile;
                                    break;
                                case Downloaded:
                                    installStateText = R.string.Downloaded;
                                    break;
                                case Installed:
                                    installStateText = R.string.Installed;
                                    break;
                            }

                            int id = LinkContent.ITEMS.size() + 1;

                            LinkContent.addItem(new LinkContent.LinkItem(Integer.toString(id), fileState, installStateText, displayName, description, link, packName, size, author, editTime));
                        }

                        simpleItemRecyclerViewAdapter.notifyDataSetChanged();

                    } catch (JSONException exception) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("发生了一个错误！");

                    error.printStackTrace();
                }
            });

            requestQueue.add(jsonObjectRequest);
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(this, LinkContent.ITEMS, mTwoPane);
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final DownloadLinkListActivity mParentActivity;
        private final List<LinkContent.LinkItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinkContent.LinkItem item = (LinkContent.LinkItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(DownloadLinkDetailFragment.ARG_ITEM_ID, item.id);
                    DownloadLinkDetailFragment fragment = new DownloadLinkDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.downloadlink_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, DownloadLinkDetailActivity.class);
                    intent.putExtra(DownloadLinkDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(DownloadLinkListActivity parent,
                                      List<LinkContent.LinkItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.downloadlink_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mContentInstallState.setText(mValues.get(position).installStateText);
            holder.mContentView.setText(mValues.get(position).displayName);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentInstallState;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mContentView = (TextView) view.findViewById(R.id.content);
                mContentInstallState = (TextView) view.findViewById(R.id.installState);
            }
        }
    }
}
