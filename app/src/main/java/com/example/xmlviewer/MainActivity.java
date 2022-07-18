package com.example.xmlviewer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.xmlviewer.adapter.XmlFileAdapter;
import com.example.xmlviewer.model.XmlFile;
import com.example.xmlviewer.myAsyncTask.ImportTask;
import com.example.xmlviewer.myAsyncTask.LoadTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    XmlFileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        new ImportTask(this).execute(selectedFiles);
    }
}