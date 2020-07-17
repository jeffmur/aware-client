package com.aware;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class MultiDependencies {

    private static final String NOLESHNS="http://nolesh.com";

    private Preference host;
    private HashMap<String, Boolean> dependencies = new HashMap<>();

    //We have to get access to the 'findPreferenceInHierarchy' function
    //from the extended preference, because this function is protected
    protected abstract Preference findPreferenceInHierarchy(String key);

    public MultiDependencies(Preference host, AttributeSet attrs){

        this.host = host;

        final String dependencyString = getAttributeStringValue(attrs, NOLESHNS, "dependencies", null);

        if (dependencyString != null) {
            String[] dependencies = dependencyString.split(",");
            for (String dependency: dependencies) {
                this.dependencies.put(dependency.trim(), false);
            }
        }
    }

    void onAttachedToActivity(){
        if(isEnabled()) registerDependencies();
    }

    void onDependencyChanged(Preference dependency, boolean disableDependent){
        setDependencyState(dependency.getKey(), !disableDependent);
        setHostState();
    }

    private void setDependencyState(String key, boolean enabled){
        for (Map.Entry<String, Boolean> entry: dependencies.entrySet()) {
            if (entry.getKey().equals(key)) entry.setValue(enabled);
        }
    }

    private String getAttributeStringValue(AttributeSet attrs, String namespace, String name, String defaultValue) {
        String value = attrs.getAttributeValue(namespace, name);
        if(value == null) value = defaultValue;
        return value;
    }

    private void registerDependencies() {
        for (final Map.Entry<String, Boolean> entry: dependencies.entrySet()) {
            final Preference preference = findPreferenceInHierarchy(entry.getKey());

            if (preference != null) {

                try {
                    final Class<Preference> prefClass = Preference.class;
                    final Method registerMethod = prefClass.getDeclaredMethod("registerDependent", Preference.class);
                    registerMethod.setAccessible(true);
                    registerMethod.invoke(preference, host);
                } catch (final Exception e) {
                    e.printStackTrace();
                }

                boolean enabled = preference.isEnabled();
                if(preference instanceof CheckBoxPreference){
                    enabled &= ((CheckBoxPreference) preference).isChecked();
                }

                setDependencyState(preference.getKey(), enabled);
            }
        }
        setHostState();
    }

    private void setHostState(){
        boolean enabled = true;
        for (Map.Entry<String, Boolean> entry: dependencies.entrySet()) {
            if (!entry.getValue()){
                enabled = false;
                break;
            }
        }
        host.setEnabled(enabled);
    }

    public boolean isEnabled(){
        return dependencies.size()>0;
    }

}