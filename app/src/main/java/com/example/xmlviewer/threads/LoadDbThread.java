package com.example.xmlviewer.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.xmlviewer.database.XmlFileDatabase;

public class LoadDbThread extends Thread{
    Handler handler;
    Context mContext;

    public LoadDbThread(Context context, Handler handler) {
        mContext = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        Message msg = handler.obtainMessage();
        msg.what = 0;
        msg.obj =  XmlFileDatabase.getInstance(mContext).xmlFileDAO().getAllFiles();
        handler.sendMessage(msg);
    }
}
