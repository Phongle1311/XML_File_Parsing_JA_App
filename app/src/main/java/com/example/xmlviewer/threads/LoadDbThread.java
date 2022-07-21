package com.example.xmlviewer.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.example.xmlviewer.FileDbHelper;

public class LoadDbThread extends Thread{
    Handler handler;
    FileDbHelper fileDbHelper;
    Context mContext;

    public LoadDbThread(Context context, Handler handler) {
        mContext = context;
        this.handler = handler;
        fileDbHelper = new FileDbHelper(mContext);
    }

    @Override
    public void run() {
        super.run();

        Message msg = handler.obtainMessage();
        msg.what = 0;
        msg.obj = fileDbHelper.getAllFiles();
        handler.sendMessage(msg);
    }
}
