package com.aware.bad_actor;

import android.content.Context;

import com.aware.Aware;
import com.aware.Aware_Preferences;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Bad_Actor {

    private Context context;
    public static final String KEY = "KEY";
    public static final String VALUE = "VALUE";
    public static final String PRESET_VALUE = "PRESET_VALUE";

    private static int axis = 1;
    private static Integer attack_type = 0;
    private static Integer window_size = 0;
    private static Integer attack_direction = 1;
    private static Double mult_dev = 1.;

    private static ArrayList<Double> previous_frog = new ArrayList<>();

    //todo: possibly replace arraylist with SQL tables for Bad Actor.
    /**
     * Index position of stdDev and mean = to axis number 0,1,2 = x,y,z
     */
    private static ArrayList<Double> stdDev = new ArrayList<>();
    private static ArrayList<Double> mean = new ArrayList<>();
    /**
     * saves actual pre-adjusted data to create std dev
     */
    private static ArrayList<ArrayList<Double>> records = new ArrayList<>();

//  todo:
//    private static final int update_interval = 10; //update statistics every 10 seconds or user input.

    /** constructor vars:
        axis_num = # of sensor axis: 1 or 3  */
    public Bad_Actor(int num_axis) {
//        records.clear();
        axis = num_axis;
        records = new ArrayList<>(axis);
    }

    /** updates class in case of new preference changes
     *  Var: Application  context */
    private void updateContext(Context base) {
        context = base;
        attack_type = Integer.parseInt(Aware.getSetting(context, Aware_Preferences.POISON_FROG_MODE));
        window_size = Integer.parseInt(Aware.getSetting(context, Aware_Preferences.POISON_FROG_WINDOW_SIZE));
        mult_dev = Double.parseDouble(Aware.getSetting(context, Aware_Preferences.POISON_FROG_DEV_MULTIPLIER));
        if(Aware.getSetting(context, Aware_Preferences.POISON_FROG_DIRECTION).equals("0")){
            attack_direction = 1;
        }else {
            attack_direction = -1;
        }
    }

    /**
     * refreshes Context and adds to array without calling from SQLLITE
     */
    public void updateMetrics(Context base, Float[] record) {

        updateContext(base);

        if (records.isEmpty()) {
            for (int i = 0; i < axis; i++) {
                ArrayList<Double> temp = new ArrayList<>();
                temp.add(new Double(record[i]));
                previous_frog.add(new Double(record[i]));
                records.add(i, temp);
            }
        } else {
            for (int i = 0; i < axis; i++) {

                if (records.get(i).size() <= window_size) {
                    records.get(i).add(new Double(record[i]));
                }
                if (records.get(i).size() > window_size) {
                    trim();
                }
            }
        }
        //todo:  adjust the mean and StdDev method calls based on interval instead of during each sensor update.
        setMean();
        setStdDev();
    }

    /** method used to reduce Data Arrays larger than window size */
    private void trim() {
        for (int i = 0; i < axis; i++) {
            records.get(i).remove(0);
        }
    }

    //todo: thread this-- Intent? or alternative non-looping calculation.
    private static void setMean() {
        mean.clear();
        for (int i = 0; i < axis; i++) {
            Double subtotal = 0.;
            int size = records.get(i).size();
            for (int j = 0; j < size; j++) {
                subtotal += records.get(i).get(j);
            }
            mean.add(i, subtotal / size);
        }
    }

    //todo: thread this-- Intent? or alternative non-looping calculation.
    private static void setStdDev() {
        stdDev.clear();
        for (int i = 0; i < axis; i++) {
            Double subtotal = 0.;
            int size = records.get(i).size();
            for (int j = 0; j < size; j++) {
                subtotal += pow((records.get(i).get(j) - mean.get(i)), 2);
            }
            if (subtotal > 0) {
                Double calc = sqrt(subtotal * (1. / (size - 1)));
                stdDev.add(i, calc);
            } else {
                stdDev.add(i, 0.);
            }
        }
    }
    /** Randomized attack method */
    public Double[] attack1() {
        Double[] temp = new Double[axis];
        Random r = new Random();
        for (int i = 0; i < axis; i++) {
            temp[i] = mean.get(i) + r.nextGaussian() * (attack_direction * mult_dev * stdDev.get(i));
        }
        return temp;
    }

    /** Randomized attack that adds a random value in direction of attack to the last attack value*/
    public Double[] attack2() {
        Double[] temp = new Double[axis];
        Random r = new Random();
        for (int i = 0; i < axis; i++) {
            temp[i] = previous_frog.get(i) + r.nextGaussian() * (attack_direction * mult_dev * stdDev.get(i));
            previous_frog.set(i, temp[i]);
        }
        return temp;
    }

    public static float poisonData(String mode, float realValue, float presetValue) {

        // the modes are defined defined @arrays/bad_modes_values
        // If you edit the modes, also edit @arrays/bad_modes_names
        if (mode.equals("preset")) return presetValue;

        if (mode.equals("random")) {
            Random r = new Random(System.currentTimeMillis());
            return r.nextFloat();
        }

        // Write  more methods and call them here.

        if (mode.equals("random_1000_2000")) {
            // TODO: read this
            // ybeltagy: You can either leave the choice of limits hardcoded inside the code
            // or you can allow the user to set it.
            // Since the framework is to be used by developers, it is better if things are kept simple
            // and the developer must edit the code to get his desired behaviour.

            Random r = new Random(System.currentTimeMillis());
            float min = 1000;
            float max = 2000;
            return r.nextFloat() * (max - min) + min;
        }

        return 0;
    }

    public static double poisonFrogRandomized() {
        return 0;
    }



}
