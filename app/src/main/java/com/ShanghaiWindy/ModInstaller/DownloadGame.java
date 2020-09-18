package com.ShanghaiWindy.ModInstaller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class DownloadGame extends AppCompatActivity {
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_game);

        Button updateLogBtn = findViewById(R.id.update_log_btn);
        updateLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webPage = new Intent(DownloadGame.this, BlogWeb.class);
                startActivity(webPage);
            }
        });
        Log.i("info", String.valueOf(getFilesDir()));

        TextView localVersion = findViewById(R.id.local_version);

        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo("com.shanghaiwindy.PanzerWarOpenSource", 0);
            localVersion.setText(packageInfo.versionName);
        } catch (Exception e) {
            localVersion.setText("Null");
            Log.e("VersionInfo", "Exception", e);
        }

        queue = Volley.newRequestQueue(DownloadGame.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://game.waroftanks.cn/backend/GameStatus/", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final String displayVersion = response.getJSONObject("content").getString("displayVersion");
                    final String updateUri = response.getJSONObject("content").getString("updateUri");

                    TextView serverVersion = findViewById(R.id.server_version);
                    serverVersion.setText(displayVersion);

                    final ProgressBar progressBar = findViewById(R.id.download_progress);
                    final TextView progressText = findViewById(R.id.download_progress_text);

                    Button downloadBtn = findViewById(R.id.download_game_btn);
                    downloadBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            final BaseDownloadTask downloadTask = FileDownloader.getImpl().create(updateUri).setPath(String.valueOf(getExternalCacheDir()), true);
                            downloadTask.setListener(new FileDownloadLargeFileListener() {
                                @Override
                                protected void completed(BaseDownloadTask task) {
                                    progressBar.setProgress(progressBar.getMax());

                                    File apkFile = new File(getExternalCacheDir() + "/" + downloadTask.getFilename());
                                    Log.i("info", String.valueOf(apkFile.exists()));

                                    if (apkFile.exists()) {
                                        installAPK(apkFile);
                                    }
                                }

                                @Override
                                protected void error(BaseDownloadTask task, Throwable e) {
                                    Snackbar.make(view, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }

                                @Override
                                protected void warn(BaseDownloadTask task) {

                                }

                                @Override
                                protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                                    Snackbar.make(view, "Pending...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }

                                @Override
                                protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                                    float progress = (float) soFarBytes / (float) totalBytes;
                                    progressBar.setProgress(Math.round(progress * progressBar.getMax()));

                                }

                                @Override
                                protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                                }
                            });
                            downloadTask.start();

                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress(0);

                            progressText.setVisibility(View.VISIBLE);

                        }
                    });

                    findViewById(R.id.download_info_request).setVisibility(View.INVISIBLE);
                    findViewById(R.id.download_info_layout).setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("发生了一个错误！");
                error.printStackTrace();
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void installAPK(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (null != apkFile) {
            try {
                //兼容7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", apkFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    //兼容8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        boolean hasInstallPermission = getPackageManager().canRequestPackageInstalls();
                        if (!hasInstallPermission) {
                            startInstallPermissionSettingActivity();
                            return;
                        }
                    }
                } else {
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    startActivity(intent);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        // 传入目前包名，防止用户列表选择
        Uri packageURI = Uri.parse("package:" + getPackageName());
        intent.setData(packageURI);
        startActivity(intent);

        Snackbar.make(findViewById(R.id.activity_download_game), getResources().getText(R.string.install_permission), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
