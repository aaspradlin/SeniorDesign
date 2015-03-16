package com.example.myapp;

/**
 * Created by Epicsprads on 11/6/14.
 * @author Alexandria Spradlin
 */
public final class Project {


    //the number of bytes in the messages written between the Android and the Mega
    public static final int MSG_SIZE = 30; //TODO placeholder and do we even need this?

    //the four vehicles
    public static final Vehicle[] VEH_ARRAY = new Vehicle[]{new Vehicle(), new Vehicle(), new Vehicle(), new Vehicle()};


    /* Stores all the sensor objects for the arena
    * [0] sensor is the least significant bit in the messages sent by the
    * Arduino Mega. */
    public static Sensor[] sensorArray;

    /* */
    public static Block[] blocksArray;


    //TODO fill
    /* */
    public static Sensor
            hmm = new Sensor();




    /* */
    public static Block
            SW1A = new Block(),
            SW2A = new Block(),
            SW3A = new Block(),
            SW4A = new Block(),
            PTH1A = new Block(),
            PTH2A = new Block(),
            PTH3A = new Block(),
            PTH4A = new Block(),
            PTH5A = new Block(),
            LD1A = new Block(),
            STA1Z = new Block(),
            SW1B = new Block(),
            SW2B = new Block(),
            SW3B = new Block(),
            SW4B = new Block(),
            LD1B = new Block();
            //TODO path B items aren't in yet
            //TODO maintenance bay items aren't in yet



    /** Populate the Blocks, creating the arena representation. */
    private static void populateBlocks() {

        //TODO set sensors for each

        /* Path A */
        SW1A.setProceeding(new Block[]{SW2A, STA1Z, SW2B});
        SW1A.setPreceding(new Block[]{PTH5A});

        SW2A.setProceeding(new Block[]{LD1A});
        SW2A.setPreceding(new Block[]{SW1A});

        SW3A.setProceeding(new Block[]{SW4A});
        SW3A.setPreceding(new Block[]{LD1A});

        SW4A.setProceeding(new Block[]{PTH1A});
        SW4A.setPreceding(new Block[]{SW3A, STA1Z, SW3B});

        PTH1A.setProceeding(new Block[]{PTH2A});
        PTH1A.setPreceding(new Block[]{SW4A});

        PTH2A.setProceeding(new Block[]{PTH3A});
        PTH2A.setPreceding(new Block[]{PTH1A});

        PTH3A.setProceeding(new Block[]{PTH4A});
        PTH3A.setPreceding(new Block[]{PTH2A});

        PTH4A.setProceeding(new Block[]{PTH5A});
        PTH4A.setPreceding(new Block[]{PTH3A});

        PTH5A.setProceeding(new Block[]{SW1A});
        PTH5A.setPreceding(new Block[]{PTH4A});

        LD1A.setProceeding(new Block[]{SW3A});
        LD1A.setPreceding(new Block[]{SW2A});


        /* Station */
        STA1Z.setProceeding(new Block[]{SW4A});
        STA1Z.setPreceding(new Block[]{SW1A});


        /* Path B */
        //TODO path B items aren't in yet
        LD1B.setProceeding(new Block[]{SW3B});
        LD1B.setPreceding(new Block[]{SW2B});

        SW2B.setProceeding(new Block[]{LD1B});
        SW2B.setPreceding(new Block[]{SW1B});

        SW3B.setProceeding(new Block[]{SW4B});
        SW3B.setPreceding(new Block[]{LD1B});


        /* Maintenance Bay */
        //TODO maintenance bay items aren't in yet




        /* Set up array */
        blocksArray = new Block[] {}; //TODO fill
    }


    /** */
    private static void populateSensors() {

        /* Add to blocks */
        //TODO set the prev and next for each sensor

        /* Set up array */
        sensorArray = new Sensor[] {hmm};
    }


    /** Called to initialize all arrays and create the relationships
     * between sensors and blocks. */
    public static void newProject() {

        //create the arena representation
        //populateBlocks();
        //populateSensors();
    }
}
