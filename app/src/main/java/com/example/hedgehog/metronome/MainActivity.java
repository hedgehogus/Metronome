package com.example.hedgehog.metronome;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import android.hardware.Camera.Parameters;
import android.widget.NumberPicker;

import java.util.Arrays;

import javax.xml.datatype.Duration;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButton;
    NumberPicker numberPicker;
    final int minValue = 200;
    int maxValue = 2000;
    final int step = 50;
    static MainActivity mainActivity;

    static AsyncTask<Void,Void,Void> at;
    static int delay = 1000;
    static int duration = 100;
    static boolean isRunningNow;
    static Vibrator vibrator;

    static private Camera camera;
    static private boolean isFlashOn;
    static private boolean hasFlash;
    static Parameters params;
    static MediaPlayer mp;
    final String START = "Start";
    final String STOP = "Stop";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.mButton);
        mButton.setOnClickListener(this);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);

        String[] valueSet = new String[(maxValue - minValue) / step + 1];

        for (int i = 0; i < valueSet.length; i ++) {
            valueSet[i] = String.valueOf(i*step + minValue);
        }

        Log.d ("asdf", Arrays.toString(valueSet));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(valueSet.length - 1);
        numberPicker.setValue(20);
        numberPicker.setDisplayedValues(valueSet);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                delay = newVal*step + minValue;
              //  Log.d("asdf", " " + newVal);
            }
        });


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        getCamera();
    }

    @Override
    public void onClick(View v) {
        if (!isRunningNow){
            isRunningNow = true;
            at = new MyAsyncTask();
            at.execute();
            mButton.setText(STOP);
        } else {
            isRunningNow = false;
            mButton.setText(START);
        }

    }

    private void playSound(){
        mp = MediaPlayer.create(MainActivity.this, R.raw.metronome);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    private static void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
    }

    private static void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
        }
    }

    private static void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
        }
    }

    static public class MyAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            do {

                publishProgress();
                turnOnFlash();
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                turnOffFlash();

                try {
                    Thread.sleep(delay-duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (isRunningNow);


            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            vibrator.vibrate(duration);
            mainActivity.playSound();
        }
    }
}
