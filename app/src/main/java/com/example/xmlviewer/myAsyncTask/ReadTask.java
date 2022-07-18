package com.example.xmlviewer.myAsyncTask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.xmlviewer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class ReadTask extends AsyncTask<String, Void, String> {

    WeakReference<Activity> activityWeakReference;

    public ReadTask(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return;

        TextView tvFileContent = activity.findViewById(R.id.file_content);
        tvFileContent.setText(R.string.reading);
    }

    @Override
    protected String doInBackground(String... strings) {
        return readFile(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPreExecute();
        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return;

        TextView tvFileContent = activity.findViewById(R.id.file_content);
        tvFileContent.setText(s);
    }

    private String readFile(String fileName){
        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return "";

        File dir = activity.getDir("official_data", Context.MODE_PRIVATE);
        File file = new File(dir, fileName);
        FileInputStream is = null;
        byte[] buffer = new byte[(int) file.length()];
        String result = "";
        int totalBytes = -1;

        try {
            is = new FileInputStream(file);
            totalBytes = is.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = "File not found";
        } catch (IOException e) {
            e.printStackTrace();
            result = e.toString();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (totalBytes!=-1)
            result = new String(buffer);
        return result;
    }
}