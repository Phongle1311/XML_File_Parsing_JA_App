package com.example.xmlviewer;

import static android.os.SystemClock.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmlviewer.adapter.XmlFileAdapter;
import com.example.xmlviewer.model.XmlFile;
import com.example.xmlviewer.myAsyncTask.LoadTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ListView lvXmlFile;
    TextView emptyView;
    ArrayList<XmlFile> mListFiles;
    XmlFileAdapter mAdapter;
    FileDbHelper fileDbHelper;
    ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileDbHelper = new FileDbHelper(this);

        emptyView = findViewById(R.id.empty_view);
        lvXmlFile = findViewById(R.id.lvXmlFile);

        mListFiles = new ArrayList<>();
        new LoadTask(this, this::updateAdapter).execute();

        Button btnImport = findViewById(R.id.btn_import);
        btnImport.setOnClickListener(view -> importHandler());

        ImageButton btnOpen = findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ImportedListActivity.class);
            startActivity(intent);
        });
    }

    private void updateAdapter(XmlFileAdapter adapter) {
        mAdapter = adapter;
        mAdapter.notifyDataSetChanged();
        Log.d("Files", mAdapter.getCount() + "");
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id == progress_bar_type) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Importing file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
            return pDialog;
        }
        return null;
    }


    private void importHandler() {
         ArrayList<String> selectedFiles = new ArrayList<>();
        for(int i = 0; i<mAdapter.getCount(); i++) {
            XmlFile file = mAdapter.getFile(i);
            if (file.getSelected()) {
                selectedFiles.add(file.getName());
                file.setSelected(false);
            }
            mAdapter.setFile(i, file);
        }

        new ImportTask().execute(selectedFiles);
    }

    private String parseXML(InputStream is) {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            return processParsing(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String processParsing(XmlPullParser parser) {
        int eventType=-1;
        String nodeName;
        String data = null;

        boolean stop = false;
        try {
            while (eventType != XmlPullParser.END_DOCUMENT && !stop) {
                eventType = parser.next();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        nodeName = parser.getName();
                        if (nodeName.equalsIgnoreCase("instanceID")) {
                            data = parser.nextText();
                            stop = true;
                        }
                        break;
                }
            }
        }
        catch(XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void copyFile(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
    }

    class ImportTask extends AsyncTask<ArrayList<String>, Void, Pair<Integer, Integer>> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @SafeVarargs
        @Override
        protected final Pair<Integer, Integer> doInBackground(ArrayList<String>... arrayLists) {
            int count = arrayLists[0].size();
            int success_count = 0;
            pDialog.setMax(count);

            AssetManager assetManager = getAssets();
            InputStream is = null;
            InputStream is2;
            OutputStream os = null;
            File dir = getApplicationContext().getDir("official_data", Context.MODE_PRIVATE);
            int i = 0;
            for (String file:arrayLists[0]) {
                pDialog.setProgress(i);
                try {
                    is = assetManager.open(file);
                    is2 = assetManager.open(file);
                    File outFile = new File(dir, file);
                    os = new FileOutputStream(outFile);

                    String instanceID = parseXML(is2);  // lấy ra instanceID trong file
                    if (instanceID != null) {
                        // nếu tìm được instance ID


                        Log.d("Files", file + " " + instanceID);
                        if (fileDbHelper.getFileNameById(instanceID)==null) {
                            // nếu chưa tồn tại trong db thì insert
                            fileDbHelper.insertFile(instanceID, file);
                            Log.d("Files", "chưa tồn tại");
                        }
                        else {
                            // nếu đã tồn tại trong db thì update
                            fileDbHelper.updateFileById(instanceID, file);
                            Log.d("Files", "tồn tại");
                        }

                        copyFile(is, os);
                        success_count++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                sleep(200); // ...
                i++;
            }
            pDialog.setProgress(i);
            sleep(200); // ...

            return new Pair<>(success_count, count - success_count);
        }

        @Override
        protected void onPostExecute(Pair<Integer, Integer> pair) {
            super.onPostExecute(pair);
            if (pDialog.isShowing())
                dismissDialog(progress_bar_type);

            String importResult = "";
            if (pair.first > 0)
                importResult += pair.first + " file" + ((pair.first > 1) ? "s " : " ")
                    + "successfully";
            if (pair.second > 0)
                importResult += "\n" + pair.second + " file" + ((pair.second > 1) ? "s " : " ")
                        + "unsuccessfully";

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Importing Result");

            builder.setMessage(importResult);
            builder.setCancelable(true);

            final AlertDialog dlg = builder.create();

            if (dlg != null)
                dlg.show();

            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    if (dlg != null)
                        dlg.dismiss();
                    t.cancel();
                }
            }, 2500);
        }
    }
}