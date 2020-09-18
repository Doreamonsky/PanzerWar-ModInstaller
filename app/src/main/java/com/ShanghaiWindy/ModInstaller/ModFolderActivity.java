package com.ShanghaiWindy.ModInstaller;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ShanghaiWindy.ModInstaller.Dummy.ModFolderContent;


public class ModFolderActivity extends AppCompatActivity {
    private static ModFolderItemAdapter modFolderItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_folder);

        final RecyclerView recyclerView = findViewById(R.id.mod_list_view);
        modFolderItemAdapter = new ModFolderItemAdapter(ModFolderContent.ITEMS);
        recyclerView.setAdapter(modFolderItemAdapter);


        UpdateFolderList();
    }

    private static void UpdateFolderList() {
        String modPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.shanghaiwindy.PanzerWarOpenSource/files/mods/";
        File modDic = new File(modPath);

        if (!modDic.exists()) {
            return;
        }
        ModFolderContent.removeAll();

        for (File file : modDic.listFiles()) {
            if (file.getName().contains("Android")) {
                String displayName = file.getName().replace("Android_", "").replace("Vehicle-", "");
                ModFolderContent.addItem(new ModFolderContent.ModFolderItem(UUID.randomUUID().toString(), displayName, file.getAbsolutePath()));
            }
        }

        modFolderItemAdapter.notifyDataSetChanged();
    }

    public static class ModFolderItemAdapter
            extends RecyclerView.Adapter<ModFolderItemAdapter.ModFolderItemViewHolder> {

        private final List<ModFolderContent.ModFolderItem> mValues;

        ModFolderItemAdapter(List<ModFolderContent.ModFolderItem> items) {
            mValues = items;
        }

        @Override
        public ModFolderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mod_list_item, parent, false);
            return new ModFolderItemViewHolder(view);
        }

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModFolderContent.ModFolderItem item = (ModFolderContent.ModFolderItem) view.getTag();

                Log.i("info", item.realFolder);

                File uninstallTarget = new File(item.realFolder);
                File[] files = uninstallTarget.listFiles();

                for (File file : files) {
                    file.delete();
                }
                uninstallTarget.delete();

                UpdateFolderList();

                Log.i("info", item.realFolder);
            }
        };

        @Override
        public void onBindViewHolder(ModFolderItemViewHolder holder, int position) {
            holder.mFolderName.setText(mValues.get(position).displayFolder);
            holder.mUninstallButton.setTag(mValues.get(position));
            holder.mUninstallButton.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ModFolderItemViewHolder extends RecyclerView.ViewHolder {
            final TextView mFolderName;
            final Button mUninstallButton;

            ModFolderItemViewHolder(View view) {
                super(view);

                mFolderName = view.findViewById(R.id.folder_name);
                mUninstallButton = view.findViewById(R.id.uninstall_btn);
            }
        }
    }
}
