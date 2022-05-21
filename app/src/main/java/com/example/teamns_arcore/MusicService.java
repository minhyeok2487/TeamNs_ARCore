package com.example.teamns_arcore;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    MediaPlayer mediaPlayer; //음악 재생을 위한 객체
    int current;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Service객체와 Activity사이에서 통신을 할 때 사용되는 메서드
        //데이터 전달이 필요 없으면 null
        return null;
    }

    @Override
    public void onCreate() {
        //서비스에서 가장 먼저 호출(최초한번)
        if(mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(this, R.raw.openning);
            current = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(current);
        } else {

        }



        mediaPlayer.setLooping(true); // 반복재생
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //서비스가 실행될 때 실행
        mediaPlayer.seekTo(current);
        mediaPlayer.start(); //음악 시작
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        //서비스가 종료될 때 실행
        current = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop(); //음악 멈춤
        super.onDestroy();
    }
}
