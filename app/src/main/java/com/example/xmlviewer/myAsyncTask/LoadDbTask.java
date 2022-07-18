package com.example.xmlviewer.myAsyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xmlviewer.FileDbHelper;
import com.example.xmlviewer.R;
import com.example.xmlviewer.adapter.ImportedXmlFileAdapter;
import com.example.xmlviewer.model.XmlFile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoadDbTask extends AsyncTask<Void, Void, ArrayList<XmlFile>> {
    WeakReference<Activity> activityWeakReference;
    FileDbHelper fileDbHelper;

    public LoadDbTask(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return;

        fileDbHelper = new FileDbHelper(activity.getBaseContext()); // ...
    }

    @Override
    protected ArrayList<XmlFile> doInBackground(Void... voids) {
        return fileDbHelper.getAllFiles();
    }

    @Override
    protected void onPostExecute(ArrayList<XmlFile> xmlFiles) {
        super.onPostExecute(xmlFiles);

        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return;

        if (xmlFiles.isEmpty()) {
            activity.findViewById(R.id.lvXmlFile).setVisibility(View.GONE);
            activity.findViewById(R.id.file_content).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
        else {
            ListView lvXmlFile      = activity.findViewById(R.id.lvXmlFile);
            TextView tvFileContent  = activity.findViewById(R.id.file_content);

            lvXmlFile.setVisibility(View.VISIBLE);
            lvXmlFile.setAdapter(new ImportedXmlFileAdapter(xmlFiles, this::onClickShowContent));
            tvFileContent.setMovementMethod(ScrollingMovementMethod.getInstance());
            tvFileContent.setVisibility(View.VISIBLE);
            activity.findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
    }

    public void onClickShowContent(XmlFile file) {
        Activity activity = activityWeakReference.get();
        if (activity != null && !activity.isFinishing())
            new ReadTask(activity).execute(file.getName());
    }
}

