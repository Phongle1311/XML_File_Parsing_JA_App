package com.example.xmlviewer.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReadThread extends Thread{
    Context context;
    String fileName;
    Handler handler;

    public ReadThread(Context context, String fileName, Handler handler) {
        this.context = context;
        this.fileName = fileName;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();

        Message msg = handler.obtainMessage();
        msg.what = 1;
        msg.obj = readFile(fileName);
        handler.sendMessage(msg);
    }

    private String readFile(String fileName){
        File dir = context.getDir("official_data", Context.MODE_PRIVATE);
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
