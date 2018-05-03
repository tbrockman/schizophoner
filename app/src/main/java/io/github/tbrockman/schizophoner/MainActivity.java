package io.github.tbrockman.schizophoner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;

public class MainActivity extends Activity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private Button startButton;
    private Button calibrateButton;
    private ProgressBar pb;
    private SeekBar thresholdBar;
    private Switch noiseMode;

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

        pb = findViewById(R.id.progressBar);

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

        thresholdBar = findViewById(R.id.thresholdBar);
        thresholdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                sd.setThreshold(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        noiseMode = findViewById(R.id.noiseMode);
        noiseMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sd.setRandomReplace(isChecked);
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
        calibrateButton.setEnabled(true);
        recorderThread.interrupt();
    }

    private void startRecording() {
        recording = true;
        calibrateButton.setEnabled(false);
        recorderThread = new Thread(new RecorderThread(this, sd, pb));
        recorderThread.start();
    }

    private void startCalibrating() {
        calibrating = true;
        startButton.setEnabled(false);
        calibrateThread = new Thread(new CalibrateThread(this, sd));
        calibrateThread.start();
    }

    private void stopCalibrating() {
        calibrating = false;
        startButton.setEnabled(true);
        calibrateThread.interrupt();
    }
}
