package io.github.tbrockman.schizophoner;

public class SharedData {

    private volatile double meanRms;
    private volatile double threshold;

    public SharedData() {
        this.meanRms = 12000;
    }

    public double getMeanRms() {
        return this.meanRms;
    }

    public void setMeanRms(double rms) {
        this.meanRms = rms;
    }

    public double getThreshold() {
        return this.threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
