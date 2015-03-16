package com.example.myapp;

import java.util.ArrayList;


/**
 * Created by Epicsprads on 11/8/14.
 */
public class Sensor {


    //
    private boolean state = false;

    //
    private ArrayList<Block> next;

    //
    private ArrayList<Block> prev;

    //
    private Vehicle vehicle;


    /**
     * @param
     * */
    public Sensor setTripped(int vehID) {

        //if the sensor was previously empty
        if (!state) {
            state = true;
            return this;
        }

        //the sensor is now on
        state = true;
        return null;
    }


    /** */
    public void setEmpty() {
        state = false;
        vehicle = null;
    }


    /** */
    public ArrayList<Block> getNext() { return next; }


    /** */
    public void setNext(ArrayList<Block> next) {
        this.next = next;
        for (Block block : next) block.setStartSensor(this);
    }


    /** */
    public ArrayList<Block> getPrev() { return prev; }


    /** */
    public void setPrev(ArrayList<Block> prev) {
        this.prev = prev;
        for (Block block : prev) block.setEndSensor(this);
    }


    /** */
    public Vehicle getVehicle() {
        return vehicle;
    }


    /** */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
