package com.example.myapp;


/**
 * Created by Epicsprads on 11/6/14.
 */
public class Block {


    //the sensor at the beginning of the block
    private Sensor startSensor;

    //the sensor at the end of the block
    private Sensor endSensor;

    //the vehicle occupying the block
    private Vehicle vehicle = null;

    //should the block be powered
    private boolean powered = false;

    //the array of blocks proceeding this block in the arena layout
    private Block[] proceeding;

    //the array of blocks preceding this block in the arena layout
    private Block[] preceding;



    /*----get/set-------------------------------------------------------------------------------------------------*/


    /**
     * @param proceeding - the array of blocks proceeding this block in the arena layout
     * */
    public void setProceeding(Block[] proceeding) {
        this.proceeding = proceeding;
    }


    /**
     * @param preceding - the array of blocks preceding this block in the arena layout
     * */
    public void setPreceding(Block[] preceding) {
        this.preceding = preceding;
    }


    /**
     * @return proceeding - the array of blocks proceeding this block in the arena layout
     * */
    public Block[] getProceeding() {
        return proceeding;
    }


    /**
     * @return preceding - the array of blocks preceding this block in the arena layout
     * */
    public Block[] getPreceding() {
        return preceding;
    }


    /**
     * @return powered - the state of the block section
     * */
    public boolean getPowered() {
        return powered;
    }


    /**
     * @param powered - the state of the block section
     * */
    public void setPowered(boolean powered) {
        this.powered = powered;
    }


    /**
     * @return vehicle - the vehicle occupying the block section
     * */
    public Vehicle getVehicle() {
        return vehicle;
    }


    /**
     * @param vehicle - the vehicle occupying the block section
     * */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }


    /**
     * @return startSensor -
     * */
    public Sensor getStartSensor() {
        return startSensor;
    }


    /**
     * @param startSensor -
     * */
    public void setStartSensor(Sensor startSensor) {
        this.startSensor = startSensor;
    }


    /**
     * @return endSensor -
     * */
    public Sensor getEndSensor() {
        return endSensor;
    }


    /**
     * @param endSensor -
     * */
    public void setEndSensor(Sensor endSensor) {
        this.endSensor = endSensor;
    }
}

