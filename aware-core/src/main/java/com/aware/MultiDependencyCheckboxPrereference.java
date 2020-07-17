package com.aware;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;

public class MultiDependencyCheckboxPrereference extends CheckBoxPreference {

    MultiDependencies multiDependencies;

    public MultiDependencyCheckboxPrereference(Context context, AttributeSet attrs) {
        super(context, attrs);

        multiDependencies = new MultiDependencies(this, attrs) {
            @Override
            protected Preference findPreferenceInHierarchy(String key) {
                //Getting access to the protected function
                return MultiDependencyCheckboxPrereference.this.findPreferenceInHierarchy(key);
            }
        };
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        multiDependencies.onAttachedToActivity();
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        if(multiDependencies.isEnabled())
            multiDependencies.onDependencyChanged(dependency, disableDependent);
        else super.onDependencyChanged(dependency, disableDependent);
    }
}