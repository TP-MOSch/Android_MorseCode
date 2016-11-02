package moshch.morsecode;

import java.util.ArrayList;

public class MorseLightSensor {
    private String decodedString;
    private Boolean isFirstValue;
    private Boolean isFlashOn;
    private Boolean isCalibrating;
    private float lastLightValue;
    private long flashOnTimerValue, flashOffTimerValue;
    private long unit;
    private float flashBrightness;
    private ArrayList<Float> arrayBrightnessValues;
    private ArrayList<Long> arrayUnitValues;

    public MorseLightSensor(long oneUnitValue, float flashBrightness) {
        this.unit = oneUnitValue;
        this.flashBrightness = flashBrightness;

        lastLightValue = 0;
        decodedString = "";
        arrayBrightnessValues = new ArrayList<>();
        arrayUnitValues = new ArrayList<>();
        isFirstValue = true;
        isFlashOn = false;
        isCalibrating = false;
    }

    public void startCalibrating() {
        isCalibrating = true;
        arrayBrightnessValues.clear();
        arrayUnitValues.clear();
        decodedString = "";
        flashBrightness = 1;
        unit = 0;
    }

    public void stopCalibrating() {
        isCalibrating = false;
        isFirstValue = true;
        stopFlashOnTimer();
        if(!arrayUnitValues.isEmpty()) {
            unit = averageLong(arrayUnitValues);
        }
    }

    public void addValue(float currentLightValue) {
        if (isFirstValue) {
            isFirstValue = false;
            lastLightValue = currentLightValue;//?
            return;
        }
        if (!isFlashOn && currentLightValue >= lastLightValue + flashBrightness) {
            isFlashOn = true;
            if (!isCalibrating) {
                flashTurnsOn();
            } else {
                startFlashOnTimer();
                arrayBrightnessValues.add(currentLightValue - lastLightValue);
                flashBrightness = averageFloat(arrayBrightnessValues);
            }
        }
        if (isFlashOn && currentLightValue <= lastLightValue - flashBrightness){
            isFlashOn = false;
            if (!isCalibrating) {
                flashTurnsOff();
            } else {
                stopFlashOnTimer();
                arrayUnitValues.add(flashOnTimerValue);
            }
        }
        lastLightValue = currentLightValue;
    }
    
    private void flashTurnsOn(){
        startFlashOnTimer();
        stopFlashOffTimer();
        if (flashOffTimerValue > unit*2.5 && flashOffTimerValue <= unit*5) {
            decodedString += "/";
        }
        if (flashOffTimerValue > unit*5) {
            decodedString += " ";
        }
    }

    private void flashTurnsOff(){
        startFlashOffTimer();
        stopFlashOnTimer();
        if (flashOnTimerValue >= unit*1.5) {
            decodedString += "-";
        } else {
            decodedString += ".";
        }
    }

    public String getDecodedString() {
        if (!isCalibrating) {
            return decodedString;
        } else {
            return "Calibrating...";
        }
    }

    public long getUnitValue() {
        return unit;
    }

    public float getFlashBrightness() {
        return flashBrightness;
    }

    private void startFlashOnTimer() {
        flashOnTimerValue = System.currentTimeMillis();
    }

    private void stopFlashOnTimer() {
        flashOnTimerValue = System.currentTimeMillis() - flashOnTimerValue;
    }

    private void startFlashOffTimer() {
        flashOffTimerValue = System.currentTimeMillis();
    }

    private void stopFlashOffTimer() {
        flashOffTimerValue = System.currentTimeMillis() - flashOffTimerValue;
    }

    private float averageFloat(ArrayList<Float> array) {
        float average = 0f;
        for(int i = 0; i < array.size(); i++) {
            average += array.get(i);
        }
        average /= array.size();
        return average;
    }

    private long averageLong(ArrayList<Long> array) {
        float average = 0f;
        for(int i = 0; i < array.size(); i++) {
            average += array.get(i);
        }
        average /= array.size();
        return (long) average;
    }
}
