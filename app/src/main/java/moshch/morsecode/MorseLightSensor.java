package moshch.morsecode;

public class MorseLightSensor {
    private String decodedString;
    private Boolean isFirstValue = true;
    private Boolean isFlashOn = false;
    private float lastLightValue;
    private long flashOnTimerValue, flashOffTimerValue;
    private long unit;
    private long luxValueChangesOnFlash;

    public MorseLightSensor(long oneUnitValue, long luxValueChangesOnFlash) {
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

    public String getDecodedString () {
        return decodedString;
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
