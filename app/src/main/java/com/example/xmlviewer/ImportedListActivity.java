package com.example.xmlviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmlviewer.adapter.ImportedXmlFileAdapter;
import com.example.xmlviewer.adapter.XmlFileAdapter;
import com.example.xmlviewer.model.XmlFile;
import com.example.xmlviewer.myAsyncTask.LoadDbTask;
import com.example.xmlviewer.myAsyncTask.ReadTask;
import com.example.xmlviewer.my_interface.IClickItemImportedFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ImportedListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imported_list);


        new LoadDbTask(this).execute();
    }

//    class LoadDbTask extends AsyncTask<Void, Void, ArrayList<XmlFile>> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mListFiles.clear();
//        }
//
//        @Override
//        protected ArrayList<XmlFile> doInBackground(Void... voids) {
//            return fileDbHelper.getAllFiles();
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<XmlFile> xmlFiles) {
//            super.onPostExecute(xmlFiles);
//            mListFiles.addAll(xmlFiles);
//            Log.d("Files", xmlFiles.size() + "");
//
//            if (mListFiles.isEmpty()) {
//                lvXmlFile.setVisibility(View.GONE);
//                emptyView.setVisibility(View.VISIBLE);
//                tvFileContent.setVisibility(View.GONE);
//            }
//            else {
//                lvXmlFile.setVisibility(View.VISIBLE);
//                mAdapter = new ImportedXmlFileAdapter(mListFiles, ImportedListActivity.this::onClickShowContent);
//                lvXmlFile.setAdapter(mAdapter);
//                emptyView.setVisibility(View.GONE);
//                tvFileContent.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//
//    public void onClickShowContent(XmlFile file) {
//        new ReadTask(this).execute(file.getName());
//    }

}