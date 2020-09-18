package com.ShanghaiWindy.ModInstaller.DownloadLink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.ShanghaiWindy.ModInstaller.R;
import com.ShanghaiWindy.ModInstaller.Util;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ShanghaiWindy.ModInstaller.Dummy.ModLinkContent;

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
    private SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadlink_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        MaterialButton fab = (MaterialButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RefreshList();
                Snackbar.make(view, getResources().getText(R.string.LoadingList), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        final View recyclerView = findViewById(R.id.downloadlink_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        Snackbar.make(recyclerView, getResources().getText(R.string.LoadingList), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        RefreshList();
    }

    private void RefreshList() {

        ModLinkContent.removeAll();
        simpleItemRecyclerViewAdapter.notifyDataSetChanged();

        // 请求获取json
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        List<String> reporisties = Arrays.asList("https://resv2.waroftanks.cn/", "https://baomao.waroftanks.cn/");


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

                            int id = ModLinkContent.ITEMS.size() + 1;

                            ModLinkContent.addItem(new ModLinkContent.ModLinkItem(Integer.toString(id), fileState, installStateText, displayName, description, link, packName, size, author, editTime));
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
        simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(this, ModLinkContent.ITEMS);
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final DownloadLinkListActivity mParentActivity;
        private final List<ModLinkContent.ModLinkItem> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModLinkContent.ModLinkItem item = (ModLinkContent.ModLinkItem) view.getTag();

                Context context = view.getContext();
                Intent intent = new Intent(context, DownloadLinkDetailActivity.class);
                intent.putExtra(DownloadLinkDetailFragment.ARG_ITEM_ID, item.id);

                context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(DownloadLinkListActivity parent,
                                      List<ModLinkContent.ModLinkItem> items) {
            mValues = items;
            mParentActivity = parent;
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
