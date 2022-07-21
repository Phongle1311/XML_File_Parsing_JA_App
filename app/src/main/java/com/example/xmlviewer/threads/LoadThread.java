package com.example.xmlviewer.threads;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;

import com.example.xmlviewer.model.XmlFile;

import java.io.IOException;
import java.util.ArrayList;

public class LoadThread extends Thread{
    Context mContext;
    Handler handler;

    public LoadThread(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        ArrayList<XmlFile> listFiles = new ArrayList<>();
        String[] files;

        AssetManager assetManager = mContext.getAssets();
        try {
            files = assetManager.list("");

            for (String file : files) {
                if (file.endsWith(".xml"))
                    listFiles.add(new XmlFile(file));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Message msg = handler.obtainMessage();
        msg.what = 0;
        msg.obj = listFiles;
        handler.sendMessage(msg);
    }
}
