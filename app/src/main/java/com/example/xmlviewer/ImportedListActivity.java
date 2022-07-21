package com.example.xmlviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.xmlviewer.adapters.ImportedXmlFileAdapter;
import com.example.xmlviewer.model.XmlFile;
import com.example.xmlviewer.threads.LoadDbThread;
import com.example.xmlviewer.threads.ReadThread;

import java.util.ArrayList;

public class ImportedListActivity extends AppCompatActivity {
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                ArrayList<XmlFile> xmlFiles = (ArrayList<XmlFile>) msg.obj;
                if (xmlFiles.isEmpty()) {
                    findViewById(R.id.rcv_xml_file).setVisibility(View.GONE);
                    findViewById(R.id.file_content).setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                }
                else {
                    RecyclerView rcvXmlFile = findViewById(R.id.rcv_xml_file);
                    TextView tvFileContent  = findViewById(R.id.file_content);

                    rcvXmlFile.setVisibility(View.VISIBLE);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ImportedListActivity.this);
                    rcvXmlFile.setLayoutManager(linearLayoutManager);
                    rcvXmlFile.setAdapter(new ImportedXmlFileAdapter(xmlFiles, ImportedListActivity.this::onClickShowContent));

                    tvFileContent.setMovementMethod(ScrollingMovementMethod.getInstance());
                    tvFileContent.setVisibility(View.VISIBLE);
                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                }
            }
            else if (msg.what == 1) {
                ((TextView)findViewById(R.id.file_content)).setText((String) msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imported_list);

        LoadDbThread t = new LoadDbThread(this, handler);
        t.start();
    }

    private void onClickShowContent(XmlFile file) {
        ((TextView)findViewById(R.id.file_content)).setText(R.string.loading_file);
        ReadThread t = new ReadThread(this, file.getName(), handler);
        t.start();
    }
}