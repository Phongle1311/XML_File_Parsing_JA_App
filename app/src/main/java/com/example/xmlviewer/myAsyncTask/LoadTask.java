package com.example.xmlviewer.myAsyncTask;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xmlviewer.FileDbHelper;
import com.example.xmlviewer.R;
import com.example.xmlviewer.adapter.XmlFileAdapter;
import com.example.xmlviewer.model.XmlFile;
import com.example.xmlviewer.my_interface.IUpdateAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoadTask extends AsyncTask<Void, Void, ArrayList<XmlFile>> {
    WeakReference<Activity> activityWeakReference;
    FileDbHelper fileDbHelper;
    IUpdateAdapter handler;

    public LoadTask(Activity activity, IUpdateAdapter handler) {
        activityWeakReference = new WeakReference<>(activity);
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return;

        fileDbHelper = new FileDbHelper(activity.getBaseContext());
    }

    @Override
    protected ArrayList<XmlFile> doInBackground(Void... voids) {
        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return null;

        ArrayList<XmlFile> answer = new ArrayList<>();
        String[] files;

        AssetManager assetManager = activity.getAssets();
        try {
            files = assetManager.list("");

            for (String file : files) {
                if (file.endsWith(".xml"))
                    answer.add(new XmlFile(file));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return answer;
    }


    @Override
    protected void onPostExecute(ArrayList<XmlFile> files) {
        super.onPostExecute(files);
        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return;

        ListView lvXmlFile = activity.findViewById(R.id.lvXmlFile);
        TextView emptyView = activity.findViewById(R.id.empty_view);
        XmlFileAdapter adapter = new XmlFileAdapter(files);
        handler.updateAdapter(adapter);

        if (files.isEmpty()) {
            lvXmlFile.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            lvXmlFile.setVisibility(View.VISIBLE);
            lvXmlFile.setAdapter(adapter);
            emptyView.setVisibility(View.GONE);
        }
    }
}

