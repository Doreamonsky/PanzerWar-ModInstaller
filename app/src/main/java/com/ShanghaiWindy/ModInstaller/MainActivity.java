package com.ShanghaiWindy.ModInstaller;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ShanghaiWindy.ModInstaller.DownloadLink.DownloadLinkListActivity;
import com.google.android.material.button.MaterialButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.liulishuo.filedownloader.FileDownloader;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 2;

    private static final int REQUEST_PERMISSION_CODE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 下载初始化v
        FileDownloader.setup(this);
        FileDownloader.getImpl().setMaxNetworkThreadCount(12);

        MaterialButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                startActivityForResult(intent, 1);
            }
        });

        // 模组检索界面
        Button viewModBtn = findViewById(R.id.viewModBtn);
        viewModBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(MainActivity.this, ModFolderActivity.class);
                startActivity(page);
            }
        });

        // 开发者日志界面
        Button viewBlogBtn = findViewById(R.id.view_blog_btn);
        viewBlogBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent webPage = new Intent(MainActivity.this, BlogWeb.class);
                        startActivity(webPage);
                    }
                }
        );

        //  启动游戏
        Button lanuchGameBtn = findViewById(R.id.lanuchGameBtn);
        lanuchGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getPackageManager();

                if (hasPackage(getApplicationContext(), "com.shanghaiwindy.PanzerWarOpenSource")) {
                    Intent intent = packageManager.getLaunchIntentForPackage("com.shanghaiwindy.PanzerWarOpenSource");
                    startActivity(intent);
                } else if (hasPackage(getApplicationContext(), "com.shanghaiwindy.PanzerWarLFS")) {
                    Intent intent = packageManager.getLaunchIntentForPackage("com.shanghaiwindy.PanzerWarLFS");
                    startActivity(intent);
                } else if (hasPackage(getApplicationContext(), "com.shanghaiwindy.PanzerWarComplete")) {
                    Intent intent = packageManager.getLaunchIntentForPackage("com.shanghaiwindy.PanzerWarComplete");
                    startActivity(intent);
                }
            }
        });

        // 下载游戏页面
        Button downloadGameBtn = findViewById(R.id.download_game_btn);
        downloadGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(MainActivity.this, DownloadGame.class);
                startActivity(page);
            }
        });


        // 下载模组页面
        Button downloadModBtn = findViewById(R.id.downloadModBtn);
        downloadModBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page = new Intent(MainActivity.this, DownloadLinkListActivity.class);
                startActivity(page);
            }
        });

        Button communityBtn = findViewById(R.id.view_community_btn);
        communityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlString = "https://blog.waroftanks.cn";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }
        });

        // 文件管理器传Intent
        Intent intent = getIntent();
        Uri uri = intent.getData();

        if (uri != null) {
            try {
                CopyModPack(uri);
            } catch (IOException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // 申请读写权
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }

    private String GetFileNameFromUri(Uri uri) {
        Cursor mCursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
        int indexed_name = mCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        mCursor.moveToFirst();
        String filename = mCursor.getString(indexed_name);
        mCursor.close();
        return filename;
    }

    protected void CopyModPack(Uri uri) throws IOException {
        String fileName = GetFileNameFromUri(uri);
        String destination = Util.getGamePath() + fileName;

        if (!fileName.contains("modpack")) {
            Snackbar.make(findViewById(R.id.main), fileName + " " + getResources().getText(R.string.InstallFailedInvalid), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        if (fileName.contains("(")) {
            Snackbar.make(findViewById(R.id.main), fileName + " " + getResources().getText(R.string.InstallFailedInvalidName), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }

        if (!fileName.contains("Android")) {
            Snackbar.make(findViewById(R.id.main), fileName + " " + getResources().getText(R.string.InstallFailedInvalidPlatform), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }

        if (fileName.contains("umodpack")) {
            Toast.makeText(this, fileName + " " + getResources().getText(R.string.InstallPaidMod), Toast.LENGTH_SHORT).show();
        }

        InputStream inputStream = getContentResolver().openInputStream(uri);

        byte[] bytes = new byte[1024];
        int index;

        FileOutputStream installedFile = new FileOutputStream(destination);

        while ((index = inputStream.read(bytes)) != -1) {
            installedFile.write(bytes, 0, index);
            installedFile.flush();
        }

        installedFile.close();
        inputStream.close();

        Snackbar.make(findViewById(R.id.main), fileName + " " + getResources().getText(R.string.DownloadComplete), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    protected boolean hasPackage(Context context, String pkgName) {
        if (null == context || null == pkgName) {
            return false;
        }

        boolean val = true;
        try {
            context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_GIDS);
        } catch (PackageManager.NameNotFoundException e) {
            val = false;
        }
        return val;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                CopyModPack(uri);
            } catch (IOException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
