package com.example.xmlviewer.threads;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;

import com.example.xmlviewer.database.XmlFileDatabase;
import com.example.xmlviewer.model.XmlFile;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportThread extends Thread {
    Context mContext;
    ArrayList<String> fileNames;
    int count;
    int success_count;
    Handler handler;


    public ImportThread(Context context, ArrayList<String> fileNames, Handler handler) {
        mContext = context;
        this.fileNames = fileNames;
        count = fileNames.size();
        success_count = 0;
        this.handler = handler;
    }

    @Override
    public void run() {
        Message msg = handler.obtainMessage();
        msg.what = 1;
        msg.arg1 = count;
        handler.sendMessage(msg);

        AssetManager assetManager = mContext.getAssets();
        InputStream is = null;
        InputStream is2;
        OutputStream os = null;
        File dir = mContext.getDir("official_data", Context.MODE_PRIVATE);

        int i = 0;
        for (String fileName:fileNames) {
            msg = handler.obtainMessage();
            msg.what = 2;
            msg.arg1 = i;
            handler.sendMessage(msg);
            try {
                is = assetManager.open(fileName);
                is2 = assetManager.open(fileName);
                File outFile = new File(dir, fileName);
                os = new FileOutputStream(outFile);

                String instanceID = parseXML(is2);  // take instanceID
                if (instanceID != null) {
                    List<XmlFile> list =  XmlFileDatabase.getInstance(mContext).xmlFileDAO()
                            .getByInstanceId(instanceID);
                    if (list != null) {
                        if (!list.isEmpty())
                            // if not exists ID
                            XmlFileDatabase.getInstance(mContext).xmlFileDAO()
                                    .updateByInstanceId(instanceID, fileName);
                        else
                            XmlFileDatabase.getInstance(mContext).xmlFileDAO()
                                    .insertFile(new XmlFile(fileName, instanceID));
                    }
                    else
                        XmlFileDatabase.getInstance(mContext).xmlFileDAO()
                                .insertFile(new XmlFile(fileName, instanceID));

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
            try {
                sleep(200); // ...
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        msg = handler.obtainMessage();
        msg.what = 2;
        msg.arg1 = i;
        handler.sendMessage(msg);
        try {
            sleep(200); // ...
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        msg = handler.obtainMessage();
        msg.what = 3;
        msg.arg1 = success_count;
        msg.arg2 = count - success_count;
        handler.sendMessage(msg);
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
