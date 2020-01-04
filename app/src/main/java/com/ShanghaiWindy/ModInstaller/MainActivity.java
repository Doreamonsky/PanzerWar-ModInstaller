package com.ShanghaiWindy.ModInstaller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final  int PICK_FILE_REQUEST =2;

    private static final int REQUEST_PERMISSION_CODE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 申请读写权
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FileChooser.class);
                intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.MULTIPLE_SELECTION.ordinal());
                intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "umodpack;modpack;zip;;");

                startActivityForResult(intent, PICK_FILE_REQUEST);
            }
        });

        // 模组检索界面
        Button viewModBtn = findViewById(R.id.viewModBtn);
        viewModBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(MainActivity.this, ModList.class);
                startActivity(page);
            }
        });

        //  启动游戏
        Button lanuchGameBtn = findViewById(R.id.lanuchGameBtn);
        lanuchGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage("com.shanghaiwindy.PanzerWarOpenSource");
                startActivity(intent);
            }
        });

        Button downloadModBtn = findViewById(R.id.downloadModBtn);
        downloadModBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse("https://github.com/Doreamonsky/Panzer-War-Lit-Mod/issues/59"));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });

        Button joinChatgroupBtn = findViewById(R.id.joinChatBtn);
        joinChatgroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinQQGroup("6MATKZTKhof7jO8TeesxvP-DpY62czgF");
            }
        });

        // 文件管理器传Intent
        Intent intent = getIntent();
        Uri uri = intent.getData();

        if (uri != null) {
            CopyModPack(uri, true);
        }
    }

    protected void CopyModPack(Uri uri, boolean isRequireParese) {
        Log.e("main", uri.toString());
        String selectPath = isRequireParese ? Util.getPath(this, uri) : uri.getPath();

        TextView pathLabel = findViewById(R.id.pathLabel);

        if (selectPath.indexOf("modpack") == -1 && selectPath.indexOf("zip") == -1) {
            Toast.makeText(getApplicationContext(), R.string.invalidFile, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getApplicationContext(), String.format("%s %s", R.string.select_file_path, selectPath), Toast.LENGTH_LONG).show();

        // 模组文件
        File modPackFile = new File(selectPath);

        // 安装目录路径
        String modPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.shanghaiwindy.PanzerWarOpenSource/files/mods/Installs/";

        if (!new File(modPath).exists()) {
            pathLabel.setText(R.string.folder_not_found);
            return;
        }

        File destFile = new File(modPath + modPackFile.getName());

        if (destFile.exists()) {
            destFile.delete();
        }

        boolean isSuccess = true;

        try {
            Util.copyFile(modPackFile, destFile);
        } catch (IOException e) {
            Log.e("main", "Copy Failed! Orz! Ops!");
            Toast.makeText(this, R.string.copy_failed, Toast.LENGTH_SHORT).show();
            e.printStackTrace();

            isSuccess = false;
        }

        if (isSuccess) {
            pathLabel.setText(String.format("%s %s", R.string.copy_success, destFile.getAbsolutePath())); //文件安装至路径
            Toast.makeText(this, R.string.copy_success, Toast.LENGTH_SHORT).show();
            modPackFile.delete();
        } else {
            pathLabel.setText(R.string.copy_failed);
        }
    }

    protected boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && data!=null) {
            if (resultCode == RESULT_OK) {
                ArrayList<Uri> selectedFiles  = data.getParcelableArrayListExtra(Constants.SELECTED_ITEMS);
                for (Uri file : selectedFiles) {
                    CopyModPack(file, false);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
