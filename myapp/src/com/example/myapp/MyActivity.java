package com.example.myapp;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.widget.TextView;


/**
 * @author Alexandria Spradlin and <>
 * Created by Epicsprads on 10/22/14.
 */
public class MyActivity extends Activity {


    // TAG is used to debug in Android logcat console
    private static final String TAG = "ArduinoAccessory";

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending;

    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    FileInputStream inputStream;
    FileOutputStream outputStream;

    //to print out errors to the screen
    public TextView errorText;

    //components in the view
    private Button eStopButton, rideStopButton, initButton, halfSpeed, fullSpeed, justStop;

    //active when an accessory is open, monitors the locations of vehicles
    BufferCheckThread thread;


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {

                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                    } else {
                        Log.d(TAG, "permission denied for accessory "
                                + accessory);
                    }
                    mPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {

                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null && accessory.equals(mAccessory)) {
                    closeAccessory();
                }
            }
        }
    };


    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    /* I modified these but also took them from the website example */
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);

        if (getLastNonConfigurationInstance() != null) {

            mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
            openAccessory(mAccessory);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //get all the buttons
        eStopButton = (Button) findViewById(R.id.eStopButton);
        eStopButton.setEnabled(true);

        //rideStopButton = (Button) findViewById(R.id.rideStopButton);
        //initButton = (Button) findViewById(R.id.initButton);



        halfSpeed = (Button) findViewById(R.id.halfSpeed);
        halfSpeed.setEnabled(true);

        fullSpeed = (Button) findViewById(R.id.fullSpeed);
        fullSpeed.setEnabled(true);


        justStop = (Button) findViewById(R.id.justStop);
        justStop.setEnabled(true);


        errorText = (TextView) findViewById(R.id.errorText);

        //make a new Project object
        Project.newProject();
    }


    public void stop(View v) {

        if (outputStream != null && inputStream != null) {

            //monitors the changes in vehicle location
            if (thread == null) {
                thread = new BufferCheckThread(inputStream, outputStream);
                thread.setTextView(errorText);
                thread.start();
            }

            thread.speedTest(2);
        }
    }



    public void halfSpeed(View v) {

        if (outputStream != null && inputStream != null) {

            if (thread == null) {
                //monitors the changes in vehicle location
                thread = new BufferCheckThread(inputStream, outputStream);
                thread.setTextView(errorText);
                thread.start();
            }

            thread.speedTest(1);
        }
    }



    public void fullSpeed(View v) {

        if (outputStream != null && inputStream != null) {

            if (thread == null) {
                //monitors the changes in vehicle location
                thread = new BufferCheckThread(inputStream, outputStream);
                thread.setTextView(errorText);
                thread.start();
            }

            thread.speedTest(0);
        }
    }

    public void eStop(View v) {

        if (outputStream != null && inputStream != null) {

            if (thread == null) {

                //monitors the changes in vehicle location
                thread = new BufferCheckThread(inputStream, outputStream);
                thread.setTextView(errorText);
                thread.start();
            }

            thread.speedTest(3);
        }
    }


    //called when the button with id = eStopButton is pressed
    //tells the Arduino Mega ADK to e-stop
    /** */ /*
    public void eStop(View v){

        //stop checking the sensors and writing to the buffer power information
        if (thread != null) thread.end();

        //disable the e-stop and ride stop buttons and enable init ride
        eStopButton.setEnabled(false);
        rideStopButton.setEnabled(false);
        initButton.setEnabled(true);

        //fill second byte in the array with 1s to signal an e-stop
        byte[] buffer = new byte[Project.MSG_SIZE];

        //TODO test this size

        buffer[0] = Project.synchronizationCounter;

        for (int i = 1; i < Project.MSG_SIZE - 1; i++) {
            buffer[i] = (byte)0xff;
        }

        buffer[Project.MSG_SIZE - 1] = (byte) 0xff; //TODO tester
        //buffer[Project.MSG_SIZE - 1] = (byte) 0x00;
        //TODO check size

        //then write the message to the Mega
        if (outputStream != null) {
            try {
                outputStream.write(buffer);
                Project.synchronizationCounter = 0; //TODO reset the synch counter on the mega too
            } catch (IOException e) {
                Log.e(TAG, "write failed", e);
            }
        }
    } */

    //called when the button with id = initButton is pressed
    //tells the Arduino Mega ADK to initialize the ride
    /** */ /*
    public void initRide(View v){

        //enable ride stop, e-stop, and disable init buttons
        rideStopButton.setEnabled(true);
        eStopButton.setEnabled(true);
        initButton.setEnabled(false);

        byte[] buffer = new byte[Project.MSG_SIZE];
        buffer[0] = Project.synchronizationCounter; // button says off, light is on

        //set the last bit to 0 to indicate the Mega sent it
        buffer[Project.MSG_SIZE - 1] = 0x00;

        //TODO test this size

        //init is message[1] = 0xf0
        buffer[1] = (byte)0xf0;

        if (outputStream != null) {
            try {
                outputStream.write(buffer);
                Project.synchronizationCounter++;

                if (thread != null) thread.end();

                //monitors the changes in vehicle location
                thread = new BufferCheckThread(inputStream, outputStream);
                thread.setTextView(errorText);
                thread.start();

            } catch (IOException e) {
                Log.e(TAG, "write failed", e);
            }
        }
    } */

    //called when the button with id = rideStopButton is pressed
    //tells the Arduino Mega ADK to ride-stop
    /** */ /*
    public void rideStop(View v){

        //disable the e-stop and ride stop buttons and enable init ride
        eStopButton.setEnabled(true);
        rideStopButton.setEnabled(false);
        initButton.setEnabled(true);

        //TODO
        byte[] buffer = new byte[Project.MSG_SIZE];
        buffer[0] = Project.megaSyncCounter;

        //msg[1] = 0xee calls a ride stop, cutting all block power
        buffer[1] = (byte)0xee;
        buffer[Project.MSG_SIZE - 1] = 0x00;

        if (outputStream != null) {
            try {
                outputStream.write(buffer);
                Project.synchronizationCounter++;
            } catch (IOException e) {
                Log.e(TAG, "write failed", e);
            }
        }
    } */

    /** */




    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /* Took this from a website. Handles general Android application things. */
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (mAccessory != null) {
            return mAccessory;
        } else {
            return super.onRetainNonConfigurationInstance();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (inputStream != null && outputStream != null) {
            return;
        }

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (mUsbManager.hasPermission(accessory)) {
                openAccessory(accessory);
            } else {
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        mUsbManager.requestPermission(accessory,mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.d(TAG, "mAccessory is null");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closeAccessory();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    private void openAccessory(UsbAccessory accessory) {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
            Log.d(TAG, "accessory opened");
           // message.setText("accessory opened");
        } else {
            Log.d(TAG, "accessory open fail");
           // message.setText("accessory open fail");
        }
    }

    private void closeAccessory() {
        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }

//          thread.end(); //end the thread
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }
}