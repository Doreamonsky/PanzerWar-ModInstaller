package com.ShanghaiWindy.ModInstaller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ModList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_list);

        String modPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.shanghaiwindy.PanzerWarOpenSource/files/mods/Installs/";
        File modDic = new File(modPath);

        List<String> modFileNames = new ArrayList<String>();

        for (File file : modDic.listFiles()) {
            modFileNames.add(file.getName());
        }
        ListView listView = findViewById(R.id.modListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.mod_list_view, R.id.modFileText, modFileNames);
        listView.setAdapter(arrayAdapter);

    }
}
