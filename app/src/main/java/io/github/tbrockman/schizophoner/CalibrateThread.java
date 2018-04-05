package io.github.tbrockman.schizophoner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

public class CalibrateThread implements Runnable {

    private Context context;
    private AudioRecord recorder;
    private SharedData sd;
    private ArrayList<Double> rmsValues;

    public CalibrateThread(Context context, SharedData sd) {
        this.context = context;
        this.sd = sd;
        this.rmsValues = new ArrayList<>();
    }

    @Override
    public void run() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            android.os.Process.setThreadPriority
                    (android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            int buffersize = AudioRecord.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    MediaRecorder.AudioEncoder.AMR_NB,
                    buffersize);

            recorder.startRecording();

            byte[] buffer = new byte[buffersize];

            while (true) {

                if (Thread.currentThread().isInterrupted()) {
                    sd.setMeanRms(calculateMean(rmsValues));
                    recorder.stop();
                    recorder.release();
                    break;
                } else {

                    if (recorder.read(buffer, 0, buffersize) < 0) {
                        Log.e(Utilities.LOG_TAG, "recorder write error");
                    }
                    double rms = Utilities.rms(buffer);
                    System.out.println(rms);
                    rmsValues.add(rms);
                }
            }
        }
    }

    public static double calculateMean(ArrayList<Double> values) {
        double acc = 0;
        for (int i = 0; i < values.size(); i++) {
            acc += values.get(i);
        }
        return acc / values.size();
    }
}
