package com.example.xmlviewer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.xmlviewer.myAsyncTask.LoadDbTask;

public class ImportedListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imported_list);

        new LoadDbTask(this).execute();
    }
}