package com.example.xmlviewer.myAsyncTask;

import static android.os.SystemClock.sleep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.example.xmlviewer.FileDbHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ImportTask extends AsyncTask<ArrayList<String>, Void, Pair<Integer, Integer>> {
    WeakReference<Activity> activityWeakReference;
    FileDbHelper fileDbHelper;
    ProgressDialog pDialog;

    public ImportTask(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        pDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return;

        pDialog.setMessage("Importing file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        pDialog.show();

        fileDbHelper = new FileDbHelper(activity);
    }

    @Override
    protected final Pair<Integer, Integer> doInBackground(ArrayList<String>... arrayLists) {
        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return null;

        int count = arrayLists[0].size();
        int success_count = 0;
        pDialog.setMax(count);

        AssetManager assetManager = activity.getAssets();
        InputStream is = null;
        InputStream is2;
        OutputStream os = null;
        File dir = activity.getDir("official_data", Context.MODE_PRIVATE);

        int i = 0;
        for (String file:arrayLists[0]) {
            pDialog.setProgress(i);
            try {
                is = assetManager.open(file);
                is2 = assetManager.open(file);
                File outFile = new File(dir, file);
                os = new FileOutputStream(outFile);

                String instanceID = parseXML(is2);  // take instanceID
                if (instanceID != null) {
                    // if not exists ID


                    Log.d("Files", file + " " + instanceID);
                    if (fileDbHelper.getFileNameById(instanceID)==null) {
                        fileDbHelper.insertFile(instanceID, file);
                    }
                    else {
                        fileDbHelper.updateFileById(instanceID, file);
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

        Activity activity = activityWeakReference.get();
        if (activity == null || activity.isFinishing())
            return;

        if (pDialog.isShowing())
            pDialog.dismiss();

        String importResult = "";
        if (pair.first > 0)
            importResult += pair.first + " file" + ((pair.first > 1) ? "s " : " ")
                    + "successfully";
        if (pair.second > 0)
            importResult += "\n" + pair.second + " file" + ((pair.second > 1) ? "s " : " ")
                    + "unsuccessfully";

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
}
