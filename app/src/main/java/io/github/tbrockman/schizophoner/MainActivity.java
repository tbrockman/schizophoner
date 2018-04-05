package io.github.tbrockman.schizophoner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private Button startButton;
    private Button calibrateButton;
    private Thread recorderThread;
    private Thread calibrateThread;
    private SharedData sd;

    private boolean permissionToRecordAccepted = false;
    private boolean recording = false;
    private boolean calibrating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        startButton = findViewById(R.id.start_audio);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recording) {
                    stopRecording();
                    startButton.setText("start");
                }
                else {
                    startRecording();
                    startButton.setText("stop");
                }
            }
        });

        calibrateButton = findViewById(R.id.calibrate_button);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (calibrating) {
                    stopCalibrating();
                    calibrateButton.setText("re-calibrate");
                }
                else {
                    startCalibrating();
                    calibrateButton.setText("done");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }

        if (!permissionToRecordAccepted ) finish();
    }

    private void initialize() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        sd = new SharedData();
    }

    private void stopRecording() {
        recording = false;
        recorderThread.interrupt();
    }

    private void startRecording() {
        recording = true;
        recorderThread = new Thread(new RecorderThread(this, sd));
        recorderThread.start();
    }

    private void startCalibrating() {
        calibrating = true;
        calibrateThread = new Thread(new CalibrateThread(this, sd));
        calibrateThread.start();
    }

    private void stopCalibrating() {
        calibrating = false;
        calibrateThread.interrupt();
    }
}
