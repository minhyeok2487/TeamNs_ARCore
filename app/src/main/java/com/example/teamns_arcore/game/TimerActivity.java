package com.example.teamns_arcore.game;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamns_arcore.R;

public class TimerActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");

        Button startBtn = findViewById(R.id.start_btn);
        Button stopBtn = findViewById(R.id.stop_btn);
        Button resetBtn = findViewById(R.id.reset_btn);

        //시작
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!running){
                    chronometer.setBase(SystemClock.elapsedRealtime()-pauseOffset);
                    chronometer.start();
                    running = true;
                }
            }
        });

        //중지
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(running){
                    chronometer.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    running = false;
                    int data = getSecondsFromDurationString(chronometer.getText().toString());
                    Log.d("뭘까>?",data+"");
                }
            }
        });

        //초기화
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
                chronometer.stop();
                running=false;
            }
        });
    }

    public static int getSecondsFromDurationString(String value){

        String [] parts = value.split(":");

        // Wrong format, no value for you.
//        if(parts.length < 2 || parts.length > 3)
//            return 0;

        int seconds = 0, minutes = 0, hours = 0;

        if(parts.length == 2){
            seconds = Integer.parseInt(parts[1]);
            minutes = Integer.parseInt(parts[0]);
        }
        else if(parts.length == 3){
            seconds = Integer.parseInt(parts[2]);
            minutes = Integer.parseInt(parts[1]);
            hours = Integer.parseInt(parts[0]);
        }

        return seconds + (minutes*60) + (hours*3600);
    }
}
