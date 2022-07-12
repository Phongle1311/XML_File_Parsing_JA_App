package com.example.xmlviewer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmlviewer.adapter.XmlFileAdapter;
import com.example.xmlviewer.model.XmlFile;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    ListView lvXmlFile;
    TextView emptyView;
    ArrayList<XmlFile> mListFiles;
    XmlFileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyView = findViewById(R.id.empty_view);
        lvXmlFile = findViewById(R.id.lvXmlFile);

        // ... xin quyền (external)
        mListFiles = new ArrayList<>();
        new LoadTask().execute();

        Button btnImport = findViewById(R.id.btn_import);
        btnImport.setOnClickListener(view -> importHandler());
    }

    private Boolean checkPermission(String permission, int REQUEST_CODE) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED) {
//                Log.v(TAG,"Permission is granted1");
                return true;
            }
            else {
//                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE:
//                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission: " + permissions[0] + "was "
                            + grantResults[0], Toast.LENGTH_SHORT).show();
                    importHandler();
                    break;
                }
        }
    }

    private void importHandler() {
        if (checkPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_CODE)) {
            // lấy danh sách đã chọn để vào copyTask
            ArrayList<String> selectedFiles = new ArrayList<>();
            for(XmlFile file : mListFiles) {
                if (file.getSelected()) {
                    selectedFiles.add(file.getName());
                    file.setSelected(false);
                }
            }
            Log.d("Files", "Granted");

            // lưu vào bộ nhớ trong
            // if files.size > 0
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/official_data";
            Log.d("Files", dirPath);
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            AssetManager assetManager = getAssets();
            InputStream is = null;
            OutputStream os = null;

            for (String file:selectedFiles) {
                try {
                    is = assetManager.open(file);
                    File outFile = new File(dirPath, file);
                    os = new FileOutputStream(outFile);
                    copyFile(is, os);
                    Log.d("Files", "Successful " + file);
                    // thông báo ra
                }
                catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Files", "Unsuccessful " + file);

                    // thông báo ra
                }
                finally {
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
            }


            mAdapter.notifyDataSetChanged();
        }
        else {
            Log.d("Files", "not granted");
        }
    }

    private void parseXML() {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getAssets().open("All_in_One_GEN007_2015-07-06_11-31-03-74.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsing(parser);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    private void processParsing(XmlPullParser parser) {

    }

    private void copyFile(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
    }
    // Lấy tên file tạo thành adapter
    class LoadTask extends AsyncTask<Void, Void, ArrayList<XmlFile>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListFiles.clear();
        }

        @Override
        protected ArrayList<XmlFile> doInBackground(Void... voids) {
            ArrayList<XmlFile> files = new ArrayList<>();

            // sửa lại chỗ này thành directory.listfiles() => lấy ra danh sách tên các file .xml

            files.add(new XmlFile("All_in_One_GEN007_2015-07-06_11-31-03-74.xml"));
            files.add(new XmlFile("All_in_One_GEN007_2015-07-06_11-31-03-714.xml"));
            files.add(new XmlFile("All_in_One_GEN007_2015-07-06_12-38-52-181.xml"));
            files.add(new XmlFile("All_in_One_GEN007_2015-07-06_12-39-25-661.xml"));
            files.add(new XmlFile("All_in_One_GEN007_2015-07-06_12-40-01-991.xml"));

            return files;
        }


        @Override
        protected void onPostExecute(ArrayList<XmlFile> files) {
            super.onPostExecute(files);
            mListFiles.clear();
            mListFiles.addAll(files);

            if (mListFiles.isEmpty()) {
                lvXmlFile.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                lvXmlFile.setVisibility(View.VISIBLE);
                mAdapter = new XmlFileAdapter(mListFiles);
                lvXmlFile.setAdapter(mAdapter);
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    class ImportTask extends AsyncTask<ArrayList<String>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<String>... arrayLists) {
            return null;
        }
    }
}