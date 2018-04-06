package io.github.tbrockman.schizophoner;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

public class SharedData {

    private volatile double meanRms;
    private volatile double threshold;
    private volatile ArrayList<byte[]> pastFrames;
    private Queue<byte[]> frameQueue;
    private int maxFrames;

    public SharedData() {
        this.meanRms = 12000;
        this.threshold = 0;
        this.maxFrames = 5000;
        this.pastFrames = new ArrayList<>();
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

    public void addFrame(byte[] frame) {
        addFrameQueue(frame);
    }

    public void addFrameQueue(byte[] frame) {
        this.frameQueue.offer(frame);
        if (this.frameQueue.size() < this.maxFrames) {
            this.frameQueue.poll();
        }
    }

    public void addFrameList(byte[] frame) {

        if (this.pastFrames.size() < this.maxFrames) {
            this.pastFrames.add(frame);
        }
        else {
            randomSwap(frame);
        }
    }

    public void putFrame(int i, byte[] frame) {
        this.pastFrames.set(i, frame);
    }

    public byte[] getFrame(int i) {
        return this.pastFrames.get(i);
    }

    public byte[] randomSwap(byte[] frame) {
        Random random = new Random();
        int size = this.pastFrames.size();
        int i = random.nextInt(size);
        byte[] swapped = getFrame(i);
        putFrame(i, frame);
        return swapped;
    }

    public byte[] fifoSwap(byte[] frame) {
        byte[] swapped = this.frameQueue.poll();
        this.frameQueue.offer(frame);
        return swapped;
    }
}
