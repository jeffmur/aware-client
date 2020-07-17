package com.aware.bad_actor;

import android.content.ContentValues;
import android.hardware.SensorEvent;
import android.os.Bundle;

import com.aware.Aware;
import com.aware.Aware_Preferences;

import java.util.Random;

public class Bad_Actor {

    public static final String BAD_ACTOR_MODE = Aware_Preferences.BAD_ACTOR_MODE;
    public static final String KEY = "KEY";
    public static final String VALUE = "VALUE";
    public static final String PRESET_VALUE = "PRESET_VALUE";

    public static float poisonData(String mode, float realValue, float presetValue){

        // the modes are defined defined @arrays/bad_modes_values
        // If you edit the modes, also edit @arrays/bad_modes_names
        if(mode.equals("preset")) return presetValue;

        if(mode.equals("random")) {
            Random r = new Random(System.currentTimeMillis());
            return r.nextFloat();
        }

        // Write  more methods and call them here.

        if(mode.equals("random_1000_2000")){
            // TODO: read this
            // ybeltagy: You can either leave the choice of limits hardcoded inside the code
            // or you can allow the user to set it.
            // Since the framework is to be used by developers, it is better if things are kept simple
            // and the developer must edit the code to get his desired behaviour.

            Random r = new Random(System.currentTimeMillis());
            float min = 1000;
            float max = 2000;
            return r.nextFloat()*(max - min) + min;
        }

        return 0;
    }

}
