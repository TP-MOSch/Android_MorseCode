package moshch.morsecode;

public class MorseLightSensor {
    private String decodedString;
    private Boolean isFirstValue = true;
    private Boolean isFlashOn = false;
    private float lastLightValue;
    private long flashOnTimerValue, flashOffTimerValue;
    private long unit;
    private float luxValueChangesOnFlash;

    public MorseLightSensor(long oneUnitValue, float luxValueChangesOnFlash) {
        this.unit = oneUnitValue;
        this.luxValueChangesOnFlash = luxValueChangesOnFlash;
        lastLightValue = 0;
        decodedString = "";
    }

    public void newLuxValue(float currentLightValue){
        if (isFirstValue) {
            isFirstValue = false;
            return;
        }
        if (!isFlashOn && currentLightValue >= lastLightValue + luxValueChangesOnFlash) {
            flashTurnsOn();
            isFlashOn = true;
        }
        if (isFlashOn && currentLightValue <= lastLightValue - luxValueChangesOnFlash){
            flashTurnsOff();
            isFlashOn = false;
        }
        lastLightValue = currentLightValue;
    }

    public void Calibrate() {
        luxValueChangesOnFlash = 0;
    }

    public void newLuxValueCalibrate(float currentLightValue) {
        if (isFirstValue) {
            lastLightValue = currentLightValue;
            isFirstValue = false;
            return;
        }
        if (!isFlashOn && currentLightValue >= lastLightValue + luxValueChangesOnFlash) {
            startFlashOnTimer();
            isFlashOn = true;
            luxValueChangesOnFlash = (luxValueChangesOnFlash + currentLightValue - lastLightValue) / 2;
        }
        if (isFlashOn && currentLightValue <= lastLightValue - luxValueChangesOnFlash){
            stopFlashOnTimer();
            isFlashOn = false;
            unit = flashOnTimerValue;
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
        if (flashOnTimerValue >= unit*2) {
            decodedString += "-";
        } else {
            decodedString += ".";
        }
    }

    public String getDecodedString() {
        return decodedString;
    }

    public long getUnitValue() {
        return unit;
    }

    public float getLuxValueChangesOnFlash() {
        return luxValueChangesOnFlash;
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
}
