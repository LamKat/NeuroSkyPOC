package com.test.helloeeg;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Queue;
/*
 * IMPORTANT CODE: used in calling class
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

/**
 * Created by LamKat on 03/02/2016.
 * Singleton class for bluetooth communication with Mindwave mobile (Neurosky)
 * NEEDS <uses-permission android:name="android.permission.BLUETOOTH" /> in manifest
 * @author  Stephen Ellis   PSYSME
 */
public  class TGManager{
    /*
     * TODO: Implement using background task so that it is independent of front end
     */
    /**
     * Class to store queued data along with the unix time it was collected
     */
    class Data{
        public String time;
        public int highAlpha;
        public int lowAlpha;
        public int highBeta;
        public int lowBeta;
        public int midGamma;
        public int lowGamma;
        public int delta;
        public int theta;
    }

    /**
     * A queue is used to maintain continuity without making consistent IO calls
     * @see com.test.helloeeg.TGManager.Data
     */
    Queue<Data> dataque = new LinkedList<Data>();

    private static TGManager Instance = new TGManager();
    BluetoothAdapter bluetoothAdapter;
    TGDevice tgDevice;
    boolean BTEnabled;
    Queue<String> que = new LinkedList<String>(); //REMOVE

    public static TGManager getInstance() {
        return Instance;
    }
    private TGManager() { }


    /**
     * Initialises bluetooth device
     * @return Is bluetooth device available; True on available
     */
    public boolean bluetoothSetup(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            return false;
        }else {
            tgDevice = new TGDevice(bluetoothAdapter, handler);
            return true;
        }
    }

    /**
     * Handles data from Mindwave device </br>
     * see reference for other message types to be monitored
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:
                    changeState(msg);
                    break;
                case TGDevice.MSG_EEG_POWER:
                    TGEegPower eegPower = (TGEegPower) msg.obj;
                    Data data = new Data();
                    Long time = System.currentTimeMillis()/1000;
                    data.time = time.toString();
                    data.highAlpha = eegPower.highAlpha;
                    data.lowAlpha = eegPower.lowAlpha;
                    data.highBeta = eegPower.highBeta;
                    data.lowBeta = eegPower.lowBeta;
                    data.midGamma = eegPower.midGamma;
                    data.lowGamma = eegPower.lowGamma;
                    data.delta = eegPower.delta;
                    data.theta = eegPower.theta;
                    dataque.add(data);

                    //REMOVE
                    que.add("HighAlpha = " + eegPower.highAlpha);
                    que.add("LowAlpha = " + eegPower.lowAlpha);
                    que.add("HighBeta = " + eegPower.highBeta);
                    que.add("LowBata = " + eegPower.lowBeta);
                    que.add("MidGamma = " + eegPower.midGamma);
                    que.add("LowGama = " + eegPower.lowGamma);
                    que.add("Delta = " + eegPower.delta);
                    que.add("theta = " + eegPower.theta);
                    //END REMOVE

                    return;
                /*case TGDevice.MSG_ATTENTION:
                    //msg.arg1 carries data for attention
                */
                default:
                    break;
            }

        }
    };
        /*
        TODO: Handle any other options we decide we need
            public static final int	MSG_ATTENTION	4
            public static final int	MSG_BLINK	22
            public static final int	MSG_EEG_POWER	131
            public static final int	MSG_HEART_RATE	3
            public static final int	MSG_LOW_BATTERY	20
            public static final int	MSG_MEDITATION	5
            public static final int	MSG_POOR_SIGNAL	2
            public static final int	MSG_RAW_COUNT	19
            public static final int	MSG_RAW_DATA	128
            public static final int	MSG_RAW_MULTI	144
            public static final int	MSG_STATE_CHANGE	1
            public static final int	MSG_THINKCAP_RAW	177
            public static final int	STATE_CONNECTED	2
            public static final int	STATE_CONNECTING	1
            public static final int	STATE_DISCONNECTED	3
            public static final int	STATE_IDLE	0
            public static final int	STATE_NOT_FOUND	4
            public static final int	STATE_NOT_PAIRED	5
        */

    private void changeState(Message msg){
        switch (msg.arg1) {
            case TGDevice.STATE_IDLE:
                break;
            case TGDevice.STATE_CONNECTING:
                que.add("Connecting...\n");
                break;
            case TGDevice.STATE_CONNECTED:
                que.add("Connected.\n");
                tgDevice.start();
                break;
            case TGDevice.STATE_NOT_FOUND:
                que.add("Can't find\n");
                break;
            case TGDevice.STATE_NOT_PAIRED:
                que.add("not paired\n");
                break;
            case TGDevice.STATE_DISCONNECTED:
                que.add("Disconnected managed\n");
                tgDevice.stopLog();
        }
    }

    /**
     * Initiates connection to Mindwave device
     */
    public void connect(){
        if(tgDevice.getState() != TGDevice.STATE_CONNECTING &&
                tgDevice.getState() != TGDevice.STATE_CONNECTED){
            tgDevice.connect(true); //If raw data is no longer wanted, param should be false
        }
    }

    /**
     * Closes connection to Mindwave device
     * @param con   Context for calling view.
     */
    public void destroy(Context con){
        writer(con);
        tgDevice.close();
    }

    /**
     * @deprecated Should implement using writer() or other data handling & storage event
     * @return queue containing strings enqueued to que
     */
    public Queue<String> poll() {
        Queue<String> queT = new LinkedList<String>();
        while (!que.isEmpty())
            queT . add(que.remove());
        return queT;
    }

    /**
     * Appends raw data to file EEG.csv from dataque
     * @param con   Context for calling view.
     */
    public void writer(Context con){
        try {
            FileOutputStream fos = con.openFileOutput("EEG.csv", con.MODE_APPEND);
            Data data;
            String line;
            while(!dataque.isEmpty()){
                data = dataque.remove();
                line = data.time + data.highAlpha + data.lowAlpha +
                        data.highBeta + data.lowBeta + data.midGamma +
                        data.lowGamma + data.theta + data.delta;
                fos.write(line.getBytes());
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
