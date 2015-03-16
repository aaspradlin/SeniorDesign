package com.example.myapp;

/**
 * Created by Epicsprads on 11/6/14.
 * @author Alexandria Spradlin
 */
public class Vehicle {

    //TODO not sure if needed
    //private boolean inLoading = false;
    //private boolean inMaintenance = false;

    /* */
    private int speed;

    /* */
    private boolean speedFlag = false;

    /* */
    public static final int FULL_SPEED = 3, HALF_SPEED = 2, STOP = 1;

    /* */
    private Block block;



    /** */
    public Vehicle() {

        speed = FULL_SPEED;
        //TODO set value of block
    }


    /** ??? */
    public void triggerSensor() {

    }


    /** */
    public void setSpeed(int speed) {
        this.speed = speed;
        speedFlag = true;
    }


    /** */
    public int getSpeed() {
        return speed;
    }


    /** */
    public boolean getFlag() {
        return speedFlag;
    }


    /** */
    public void setBlock(Block next) {

        this.block = next;
        next.setVehicle(this);
    }


    /** */
    public Block getBlock() {
        return block;
    }
}
