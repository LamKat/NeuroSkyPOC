package com.test.helloeeg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;




/*
 * IMPORTANT CODE:
 */

/*
 * Creates manager object
        TGManager manager = TGManager.getInstance();
 */

/*
 * Sets up bluetooth device
 * Exits if bluetooth is not available
        if (!manager.bluetoothSetup()) {
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
            finish();
        }
 */

/*
 * Initiates connection to Mindwave
 * MUST BE DONE AFTER bluetoothSetup()
        manager.connect();
 */

/*
 * Kills connection and flushes data
 * Could be used as sole writer call (probably shouldn't be)
        manager.destroy(this);
 */




public class HelloEEGActivity extends Activity {
    TGManager manager = TGManager.getInstance();
    TextView tv;
    Button b;
    Timer t = new Timer();
    boolean tStarted = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText("");
        tv.append("Android version: " + Integer.valueOf(Build.VERSION.SDK) + "\n");
        if (!manager.bluetoothSetup()) {
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
            finish();
        }
        //Intent mServiceIntent = new Intent(this, TGManService.class);
        //this.startService(mServiceIntent);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onDestroy() {
        manager.destroy(this);
        super.onDestroy();
    }

    public void doStuff(View view) {
        if (tStarted)
           /*
            * Apparently i dont know how file stuff works.
            tv.setText("");
            t.cancel();
            manager.writer(this);
            try {
                File file = new File("EEG.csv");
                InputStream inputStream = new FileInputStream(file);
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;

                while ((line = r.readLine()) != null) {
                    total.append(line);
                }

                tv.setText(total.toString());
            /*} catch (FileNotFoundException ex) {
                tv.setText("File Not Found");
            } catch (IOException ex) {
                tv.setText("ERRROR IO");
            * /
            } catch (Exception e){

                tv.setText("ERRROR IO");

            }
            tv.append("Finish");*/
            return;

        tStarted = true;

        manager.connect();
        /*
         * Timer loop is purely for user interface, and doesn't impact data collection at all
         */
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              Queue<String> que = manager.poll();
                                              while (!que.isEmpty())
                                                  tv.append(que.remove() + "\n");
                                          }
                                      });
                                  }
                              },
                0,
                1000);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HelloEEG Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.test.helloeeg/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HelloEEG Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.test.helloeeg/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}