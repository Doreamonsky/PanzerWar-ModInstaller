package com.ShanghaiWindy.ModInstaller;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ShanghaiWindy.ModInstaller.Dummy.ModFolderContent;


public class ModFolderActivity extends AppCompatActivity {
    private ModFolderItemAdapter simpleItemRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_folder);

        final RecyclerView recyclerView = findViewById(R.id.mod_list_view);
        simpleItemRecyclerViewAdapter = new ModFolderItemAdapter(ModFolderContent.ITEMS);
        recyclerView.setAdapter(simpleItemRecyclerViewAdapter);


        String modPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.shanghaiwindy.PanzerWarOpenSource/files/mods/";
        File modDic = new File(modPath);

        if (!modDic.exists()) {
            return;
        }

        List<String> modFileNames = new ArrayList<String>();

        for (File file : modDic.listFiles()) {
            if (file.getName().contains("Android")) {
                String displayName = file.getName().replace("Android_", "").replace("Vehicle-", "");
                ModFolderContent.addItem(new ModFolderContent.ModFolderItem("0", displayName, file.getAbsolutePath()));
            }
        }
//        ListView listView = findViewById(R.id.modListView);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.mod_list_view, R.id.modFileText, modFileNames);
//        listView.setAdapter(arrayAdapter);

        simpleItemRecyclerViewAdapter.notifyDataSetChanged();

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

        @Override
        public void onBindViewHolder(ModFolderItemViewHolder holder, int position) {
            holder.mFolderName.setText(mValues.get(position).displayFolder);
            holder.mUninstallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("test", "clicked");
                }
            });
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
