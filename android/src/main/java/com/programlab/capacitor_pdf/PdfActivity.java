package com.programlab.capacitor_pdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.programlab.capacitor_pdf.models.PdfAnnotations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PdfActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE = 1;
    private static final int READ_EXTERNAL_STORAGE = 2;
    private static final String TAG = "Download Task";

    private String downloadUrl = null, downloadFileName = "elpais_20200415.pdf";
    private ArrayList<PdfAnnotations> annotations = new ArrayList<PdfAnnotations>();
    private ProgressDialog progressDialog;

    private static final String FRAGMENT_INFO = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        downloadUrl = getIntent().getStringExtra("linkPdf");
        String annotation = getIntent().getStringExtra("annotations");

        JSONArray annotationJson = null;
        try {
            annotationJson = new JSONArray(annotation);
            for (int i = 0; i < annotationJson.length(); i++) {
                JSONObject o = annotationJson.getJSONObject(i);
                PdfAnnotations object = new PdfAnnotations();
                object.setPoint_link(o.getString("point_link"));
                object.setPoint_x(o.getInt("point_x"));
                object.setPoint_y(o.getInt("point_y"));
                object.setPoint_icon(o.getString("point_icon"));
                object.setPoint_color_icon(o.getString("point_color_icon"));
                object.setPoint_icon(o.getString("point_background_icon"));
                annotations.add(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Boolean writeStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
        Boolean readStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;

        if (writeStorage && readStorage) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE);
        } else {
            new DownloadPDf().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new DownloadPDf().execute();
            } else {
                Toast toast1 = Toast.makeText(getApplicationContext(), "Debes aceptar permisos para que pueda funcionar el pdf", Toast.LENGTH_LONG);
                toast1.show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info) {
            new InfoDialogFragment().show(getSupportFragmentManager(), FRAGMENT_INFO);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class InfoDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            return new AlertDialog.Builder(requireContext())
                    .setMessage(R.string.intro_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }

    }


    /**
     * Clase para descargar el archivo
     */

    public class DownloadPDf extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PdfActivity.this);
            progressDialog.setMessage("Downloading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    progressDialog.dismiss();
                    File pdfFile = new File(Environment.getExternalStorageDirectory() + "/jhonocampo/" + downloadFileName);  // -> filename = maven.pdf
                    Uri path = Uri.fromFile(pdfFile);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new PdfRendererBasic(annotations))
                            .commitNow();
                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.setUseCaches(false); //no cache
                c.setRequestProperty("Cache-Control", "no-cache");
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                apkStorage = new File(Environment.getExternalStorageDirectory() + "/jhonocampo/");

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }
                System.out.println(Environment.getExternalStorageDirectory() + "/jhonocampo/");
                outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }
}
