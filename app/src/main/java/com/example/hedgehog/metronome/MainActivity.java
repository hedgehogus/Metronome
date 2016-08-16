package com.example.hedgehog.metronome;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButton;
    static AsyncTask<Void,Void,Void> at;
    static int delay = 1000;
    static boolean isRunningNow;
    static Vibrator vibrator;

    final String START = "Start";
    final String STOP = "Stop";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.mButton);
        mButton.setOnClickListener(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
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

    static public class MyAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            do {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress();
            }while (isRunningNow);


            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            vibrator.vibrate(100);
        }
    }
}
