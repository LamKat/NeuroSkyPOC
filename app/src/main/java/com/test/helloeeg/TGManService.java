package com.test.helloeeg;

import android.app.IntentService;
import android.content.Intent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LamKat on 04/02/2016.
 */
public class TGManService extends IntentService {

    public TGManService(){
        super("TGManService");


    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String dataString = intent.getDataString();
        try {
            FileOutputStream fos = openFileOutput("EEG.csv", this.MODE_APPEND);
            fos.write("Hello".getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
