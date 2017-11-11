package com.vn3dart.lavita;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.vn3dart.lavita.dialog.AlertDialogCustom;
import com.vn3dart.lavita.util.DecompressZip;
import com.vn3dart.lavita.util.DownloadFile;
import com.vn3dart.lavita.util.ExternalStorage;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends Activity {

    private String urlDownloadData = "http://media24h.net/data_app/lavita-data.zip";
    private Handler mHandler;
    private static final int PERMISSIONS_REQUEST = 100;
    protected ProgressDialog mProgressDialog;
    private File zipDir, zipFile;
    private String urlView = "";
    private long sizeFile = 0;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("path_url", urlView);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler = new Handler();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            setPermissionsRequest();
        } else {
            startDownloadData();
        }
    }

    /**
     * check permission doi voi android 6.0 tro len
     */
    private void setPermissionsRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startDownloadData();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(SplashActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void startDownloadData() {
        zipDir = ExternalStorage.getSDCacheDir(this, "tmp");
        // File path to store .zip file before unzipping
        zipFile = new File(zipDir.getPath() + "/lavita-data.zip");
        //check ket noi internet
        if (isNetworkConnected()) {
            if (zipFile.exists()) {
                //check size file
                new GetSizeFileTask(zipFile).execute(urlDownloadData);
            } else {
                //tai file
                new DownloadTask().execute(urlDownloadData);
            }
        } else {
            if (zipFile.exists()) {
                new UnzipDataTask().execute();
            } else {
                Toast.makeText(this, "Xin vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * check connect internet
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * Background task to get size file and unpack .zip file in background.
     */
    private class GetSizeFileTask extends AsyncTask<String, Long, Long> {

        private File fileZip;

        public GetSizeFileTask(File fileZip) {
            this.fileZip = fileZip;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected Long doInBackground(String... params) {
            String url = (String) params[0];
            try {
                final URL uri = new URL(url);
                URLConnection ucon = uri.openConnection();
                ucon.connect();
                final String contentLengthStr = ucon.getHeaderField("content-length");
                sizeFile = Long.parseLong(contentLengthStr);
                return sizeFile;
            } catch (Exception e) {
                sizeFile = 0;
            }
            return sizeFile;
        }

        @Override
        protected void onPostExecute(Long result) {
            dismissProgress();
            if (result == fileZip.length()) {
                new UnzipDataTask().execute();
            } else {
                final AlertDialogCustom alertDialogCustom = new AlertDialogCustom(SplashActivity.this);
                alertDialogCustom.show();
                alertDialogCustom.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (alertDialogCustom.isDismiss) {
                            fileZip.delete();
                            new DownloadTask().execute(urlDownloadData);
                        } else {
                            new UnzipDataTask().execute();
                        }
                    }
                });
            }
        }
    }

    /**
     * Background task to download and unpack .zip file in background.
     */
    private class DownloadTask extends AsyncTask<String, Long, Exception> {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected Exception doInBackground(String... params) {
            String url = (String) params[0];
            try {
                DownloadFile.download(url, zipFile, zipDir);
            } catch (Exception e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            dismissProgress();
            new UnzipDataTask().execute();
        }
    }

    //////////////////////////////////////////////////////////////////////////
    // Progress Dialog
    //////////////////////////////////////////////////////////////////////////
    protected void showProgress() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Vui lòng chờ");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Đang tải ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void dismissProgress() {
        // You can't be too careful.
        if (mProgressDialog != null && mProgressDialog.isShowing() && mProgressDialog.getWindow() != null) {
            try {
                mProgressDialog.dismiss();
            } catch (IllegalArgumentException ignore) {
                ;
            }
        }
        mProgressDialog = null;
    }

    /**
     * Unpack .zip file.
     */
    private class UnzipDataTask extends AsyncTask<String, Long, File> {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected File doInBackground(String... params) {
            File outputDir = ExternalStorage.getSDCacheDir(SplashActivity.this, "unzipped");
            File file = new File(outputDir.getAbsolutePath() + "/www/index.html");
            if (file.exists()) {
                return file;
            } else {
                DecompressZip decomp = new DecompressZip(zipFile.getPath(),
                        outputDir.getPath() + File.separator);
                decomp.unzip();
                return file;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            dismissProgress();
            urlView = result.getAbsolutePath();
            mHandler.postDelayed(mRunnable, 100);
        }
    }
}
