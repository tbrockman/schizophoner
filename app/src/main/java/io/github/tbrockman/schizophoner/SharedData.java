package io.github.tbrockman.schizophoner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SharedData {

    private volatile double meanRms;
    private volatile double threshold;
    private boolean randomReplace;
    private volatile ArrayList<short[]> shortFrames;
    private Queue<short[]> shortFrameQueue;
    private int maxFrames;
    private int fillSize;

    public SharedData() {
        this.meanRms = 0;
        this.threshold = 0;
        this.maxFrames = 1000;
        this.shortFrames = new ArrayList<>();
        this.shortFrameQueue = new LinkedList<short[]>();
        this.randomReplace = false;
        this.fillSize = 10;
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

    public short[] cloneFrame(short[] frame) {
        short[] copy = new short[frame.length];
        System.arraycopy( frame, 0, copy, 0, frame.length );
        return copy;
    }

    public short[] dequeueShort(short[] frame) {
        short[] copy = cloneFrame(frame);
        short[] swapped;

        if (this.shortFrameQueue.size() > 10) {
            swapped = this.shortFrameQueue.poll();
        }
        else {
            swapped = copy;
        }

        if (this.shortFrameQueue.size() > this.maxFrames) {
            this.shortFrameQueue.poll();
        }

        this.shortFrameQueue.offer(copy);
        return swapped;
    }

    public short[] randomReplaceShort(short[] frame) {
        short[] copy = cloneFrame(frame);
        short[] swapped;

        if (this.shortFrames.size() > 10) {
            Random random = new Random();
            int size = this.shortFrames.size();
            int i = random.nextInt(size);
            swapped = this.shortFrames.get(i);
            this.shortFrames.set(i, copy);
        }
        else {
            swapped = copy;
        }

        if (this.shortFrames.size() > this.maxFrames) {
            this.shortFrames.remove(0);
        }

        this.shortFrames.add(copy);
        return swapped;
    }

    public void setRandomReplace(boolean replace) {
        this.randomReplace = replace;
    }

    public boolean isRandomReplace() {
        return this.randomReplace;
    }
}
