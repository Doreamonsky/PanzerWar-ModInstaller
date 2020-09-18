package com.ShanghaiWindy.ModInstaller.DownloadLink;

import android.app.Activity;
import android.os.Bundle;

import com.ShanghaiWindy.ModInstaller.R;
import com.ShanghaiWindy.ModInstaller.Util;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ShanghaiWindy.ModInstaller.Dummy.ModLinkContent;

/**
 * A fragment representing a single DownloadLink detail screen.
 * This fragment is either contained in a {@link DownloadLinkListActivity}
 * in two-pane mode (on tablets) or a {@link DownloadLinkDetailActivity}
 * on handsets.
 */
public class DownloadLinkDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy displayName this fragment is presenting.
     */
    private ModLinkContent.ModLinkItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DownloadLinkDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy displayName specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load displayName from a displayName provider.
            mItem = ModLinkContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.displayName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.downloadlink_detail, container, false);

        // Show the dummy displayName as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.author)).setText(mItem.author);
            ((TextView) rootView.findViewById(R.id.editTime)).setText(mItem.editTime);
            ((TextView) rootView.findViewById(R.id.description)).setText(mItem.description);
            ((TextView) rootView.findViewById(R.id.size)).setText(mItem.size);

            if (mItem.fileState == Util.FileState.NoFile) {
                rootView.findViewById(R.id.download_guide).setVisibility(View.VISIBLE);
            } else {
                rootView.findViewById(R.id.download_guide).setVisibility(View.GONE);
            }
        }

        return rootView;
    }
}
