package com.example.xmlviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.xmlviewer.adapters.XmlFileAdapter;
import com.example.xmlviewer.model.XmlFile;
import com.example.xmlviewer.threads.ImportThread;
import com.example.xmlviewer.threads.LoadThread;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    XmlFileAdapter mAdapter;
    ProgressDialog pDialog;

    Handler handler = new Handler(Looper.getMainLooper()) {
        static final int SET_RCV = 0;

        static final int SHOW_DIALOG = 1;
        static final int UPDATE_DIALOG = 2;
        static final int DISMISS_DIALOG = 3;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SET_RCV:
                    ArrayList<XmlFile> files = (ArrayList<XmlFile>) msg.obj;
                    RecyclerView rcvXmlFile = findViewById(R.id.rcv_xml_file);
                    TextView emptyView = findViewById(R.id.empty_view);
                    XmlFileAdapter adapter = new XmlFileAdapter(files);
                    updateAdapter(adapter);

                    if (files.isEmpty()) {
                        rcvXmlFile.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        rcvXmlFile.setVisibility(View.VISIBLE);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                        rcvXmlFile.setLayoutManager(linearLayoutManager);
                        rcvXmlFile.setAdapter(adapter);
                        emptyView.setVisibility(View.GONE);
                    }
                    break;

                case SHOW_DIALOG:
                    pDialog.setMessage("Importing file. Please wait...");
                    pDialog.setIndeterminate(false);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pDialog.setCancelable(true);
                    pDialog.setMax(msg.arg1);
                    pDialog.show();
                    break;

                case UPDATE_DIALOG:
                    int percent = msg.arg1;
                    pDialog.setProgress(percent);
                    break;

                case DISMISS_DIALOG:
                    int successful_count = msg.arg1;
                    int unsuccessful_count = msg.arg2;

                    if (pDialog.isShowing())
                        pDialog.dismiss();

                    String importResult = "";
                    if (successful_count > 0)
                        importResult += successful_count + " file" +
                                ((successful_count > 1) ? "s " : " ") + "successfully";
                    if (unsuccessful_count > 0)
                        importResult += "\n" + unsuccessful_count + " file" +
                                ((unsuccessful_count > 1) ? "s " : " ") + "unsuccessfully";

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
                    break;

                default:
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadThread t = new LoadThread(this, handler);
        t.start();

        pDialog = new ProgressDialog(this);
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
    }

    private void importHandler() {
        ArrayList<String> selectedFiles = new ArrayList<>();
        for(int i = 0; i<mAdapter.getItemCount(); i++) {
            XmlFile file = mAdapter.getFile(i);
            if (file.getSelected()) {
                selectedFiles.add(file.getName());
                file.setSelected(false);
            }
            mAdapter.setFile(i, file);
        }

        ImportThread t = new ImportThread(this, selectedFiles, handler);
        t.start();
    }
}