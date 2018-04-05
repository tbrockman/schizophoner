package io.github.tbrockman.schizophoner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class RecorderThread implements Runnable {

    private Activity context;
    private SharedData sd;
    private AudioRecord recorder;
    private AudioTrack player;

    public RecorderThread(Activity context, SharedData sd) {
        this.context = context;
        this.sd = sd;
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

            player = new AudioTrack(AudioManager.STREAM_MUSIC,
                    44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    MediaRecorder.AudioEncoder.AMR_NB,
                    buffersize,
                    AudioTrack.MODE_STREAM);

            Log.d(Utilities.LOG_TAG, "sample rate = : " + recorder.getSampleRate());

            player.setPlaybackRate(recorder.getSampleRate());
            recorder.startRecording();
            player.play();

            byte[] buffer = new byte[buffersize];

            while (true) {

                if (Thread.currentThread().isInterrupted()) {
                    recorder.stop();
                    recorder.release();
                    player.stop();
                    player.release();
                    break;
                } else {

                    if (recorder.read(buffer, 0, buffersize) < 0) {
                        Log.e(Utilities.LOG_TAG, "recorder write error");
                    }



                    if (player.write(buffer, 0, buffer.length) < 0) {
                        Log.e(Utilities.LOG_TAG, "player read error");
                    }
                }
            }
        }
    }
}
