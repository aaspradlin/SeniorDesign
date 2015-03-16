package com.example.myapp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Epicsprads on 11/6/14.
 * @author Alexandria Spradlin
 */
public class BufferCheckThread extends Thread {


    //used to read from and write to the Arduino Mega
    private FileInputStream inputStream;
    private FileOutputStream outputStream;

    //should this thread be running
    private boolean running = true;

    //holds the message last received from the Mega
    private byte[] buffer;

    private byte mySyncCounter = 0; //TODO not needed
    private byte megaSyncCounter = 0; //TODO not needed

    //for testing purposes
    private boolean loadingDock = false;

    //for testing purposes
    private TextView errorText;

    /* */
    private boolean eStop = false;

    /* */
    private boolean rideStop = false;

    /* */
    private boolean init = false;


    /* */
    private ArrayList<Sensor> trippedSensors = new ArrayList<Sensor>(); //TODO figure out how to import it


    /*----constructor-------------------------------------------------------------------------------------------------*/


    /** Constructor
     * @param inputStream - used to read from the buffer between the Android and Mega.
     * @param outputStream - used to write to the buffer between the Android and Mega.
     * */
    public BufferCheckThread(FileInputStream inputStream, FileOutputStream outputStream) {

        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }


    /*----testing-------------------------------------------------------------------------------------------------*/


    /** for testing purposes */
    private void checkBufferTest() {

        try {

            if (!errorText.getText().equals("checkingBuffer")) errorText.setText("checkingBuffer");

            //read from the Arduino Mega
            inputStream.read(buffer);

            /* If the message is different from the last one received, process it.
             * The first byte is the synchronization counter and the very last bit indicates
             * if the Arduino wrote it (1) or the Android (0) */
            if ((buffer[Project.MSG_SIZE - 1] % 2 == 1) && (buffer[0] != megaSyncCounter)) {

                //save this sync counter value
                megaSyncCounter = buffer[0];

                //the Mega sends a confirmation that it has been initialized
                if (buffer[1] == (byte)0xff) {

                    //write the first message to the buffer, turning on blocks
                    writeToBufferTest();

                } else {

                    if (((buffer[Project.MSG_SIZE - 1] >> 1) % 2) == 1) {
                        if (!loadingDock) {
                            writeToBufferTest();
                            loadingDock = true;
                        }
                    } else if (((buffer[Project.MSG_SIZE - 1] >> 2) % 2) == 1) {
                        if (loadingDock) {
                            writeToBufferTest();
                            loadingDock = false;
                        }
                    }
                }
            }
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }


    public void speedTest(int i) {

        byte[] message = new byte[2];
        message[0] = mySyncCounter; //add sync counter to front

        switch (i) {
            case 0: //stop
               // errorText.setText("stop");
                message[1] = (byte)0x00;
                break;
            case 1: //half speed
               // errorText.setText("half");
                message[1] = (byte)0x0f;

                break;
            case 2: //full speed
               // errorText.setText("full");
                message[1] = (byte)0xf0;
                break;
            case 3: //e-stop
                //errorText.setText("e-stop");
                message[1] = (byte)0xff;
                break;
        }

        String test = "";

        for (int j = 0; j < message.length; j++) {
            test = test + Byte.toString(message[j]) + " ";
        }

        errorText.setText(test);

        try {
            //write the message to the buffer
            outputStream.write(message);

            //increment the synchronization counter
            mySyncCounter++;

        } catch (IOException e) { }
    }



    /** for testing purposes */
    private void writeToBufferTest() {

        //make the byte array for the message
        byte[] message = new byte[Project.MSG_SIZE];
        for (int i = 0; i < Project.MSG_SIZE; i++) {
            message[i] = (byte)0x00;
        }

        //the first byte is the synchronization counter
        message[0] = mySyncCounter;

        if (loadingDock) message[Project.MSG_SIZE - 1] |= 0x02;

        try {
            //write the message to the buffer
            outputStream.write(message);

            //increment the synchronization counter
            mySyncCounter++;

        } catch (IOException e) { }
    }


    /*----main methods-------------------------------------------------------------------------------------------------*/


    /** */
    private void checkBuffer() {

        try {
            //read from the Arduino Mega
            inputStream.read(buffer);


            /* If the message was sent by the Mega */
            if (buffer[0] == 0xf0) {


                //the bit location within the message (from least to most sig)
                int bitIndex = 0;

                //reset the tripped sensors arraylist
                trippedSensors = new ArrayList<Sensor>();


                //go through the message in buffer to determine the states of all sensors
                for (int i = 0; i < Project.sensorArray.length; i++) {


                    //if the sensor's bit is 1, tell it that it is on
                    if (((buffer[Project.MSG_SIZE - 1 - (bitIndex / 8)] >> (bitIndex % 8)) & 0x01) != 0) {

                        /* find which vehicle is on the sensor. The vehicle ID is the 2 bits in front of the
                         * sensor bit that's a 1. */
                        int vehID = (((buffer[Project.MSG_SIZE - 1 - ((bitIndex + 2) / 8)] >> ((bitIndex + 2) % 8)) & 0x01) << 1) +
                                ((buffer[Project.MSG_SIZE - 1 - ((bitIndex + 1) / 8)] >> ((bitIndex + 1) % 8)) & 0x01);

                        //tell the sensor that it is now on
                        Sensor sensor = Project.sensorArray[i].setTripped(vehID);
                        if (sensor != null) trippedSensors.add(sensor);

                        //go to the next sensor bit in the message
                        bitIndex += 3;


                    //else if the sensor's bit is 0
                    } else {

                        //tell the sensor that it is now off
                        Project.sensorArray[i].setEmpty();

                        //go to the next bit in the message
                        bitIndex++;
                    }
                }

                //if a sensor was tripped
                if (trippedSensors.size() != 0) {
                    schedule();
                    writeToBuffer();
                }
            }
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * When the internal state of the arena changes, this is called on for the Android to write
     * a message to the Mega over the buffer.
     * */
    public void writeToBuffer() {

        //make the byte array for the message
        byte[] message = new byte[Project.MSG_SIZE];

        //the bit location within the message (from least to most sig) the last bit is a 0
        int bitIndex = 0;

        //always
        message[0] = (byte)0xf0;


        //if a ride stop has been triggered, write all 0s to the buffer
        if (!rideStop) {

            //for each block in the arena
            for (int i = 0; i < Project.blocksArray.length; i++) {

                //if the block should be powered, append a 1 to the byte array
                if (Project.blocksArray[i].getPowered())
                    message[Project.MSG_SIZE - 1 - (bitIndex / 8)] += (1 << (bitIndex % 8));

                //go to the next bit
                bitIndex++;
            }


            //specify the vehicle speeds at the front of the byte array
            for (int i = 0; i < 4; i++) {

                //TODO check math on this one
                message[Project.MSG_SIZE - 1 - (bitIndex / 8)] += checkVehSpeed(i);

                //if the vehicle's speed has changed
                if (Project.VEH_ARRAY[i].getFlag()) {

                }
            }
        }


        //if an e_stop has been triggered
        if (eStop) {
            message[0] = (byte)0xf0; //always true
            message[1] = (byte)0xff;
            running = false; //stop this thread

            init = false;
            eStop = false;
            rideStop = false;
        }



        try {
            //write the message to the buffer
            outputStream.write(message);

        } catch (IOException e) { }
    }


    /** This changes the states of Block sections in the program based on the scheduling algorithm.
     * The states of blocks are then used when writing to the buffer. */
    private void schedule() {


        //iterate through the tripped sensors array and turn off prev blocks
        for (Sensor sensor : trippedSensors) {
            for (Block block : sensor.getPrev()) {
                block.setPowered(false); //turn off the preceding block
                block.setVehicle(null); //this no longer has a vehicle
            }
        }


        //iterate through the tripped sensors array and turn on next blocks
        for (Sensor sensor : trippedSensors) {

            //if the sensor only has one block section proceeding it, set it to powered
            if (sensor.getNext().size() == 1) {
                sensor.getNext().get(0).setPowered(true);
                sensor.getVehicle().setBlock(sensor.getNext().get(0));
            }


            //if there's a choice of block sections to turn on
            else {
                Block chosen = new Block(); //TODO placeholder block

                //TODO do the scheduling stuff here

                chosen.setPowered(true);
                sensor.getVehicle().setBlock(chosen);
            }
        }
    }


    /** */
    private int checkVehSpeed(int vehID) {

        //TODO
        if (rideStop) {
            return
        }
        //decide if a vehicle's speed should change
        return 0;
    }


    /*----thread stuff-------------------------------------------------------------------------------------------------*/


    /** Start the thread continuously checking the buffer. */
    @Override
    public void run() {

        while (running) {
            if (init) {
                checkBuffer();
            }
        }
    }

    /*----get/set-------------------------------------------------------------------------------------------------*/


    /**
     * @param errorText - the text field to display error messages in the GUI
     * */
    public void setTextView(TextView errorText) {
        this.errorText = errorText;
    }

    /** Ends the life of this thread. */
    public void end() {

        running = false; //end the run() loop
    }

    /** */
    public void triggerEStop() {

        eStop = true;
    }

    /** */
    public void triggerRideStop() {

        rideStop = true;
    }

    /** */
    public void resetRideStop() {

        rideStop = false;
    }
}
