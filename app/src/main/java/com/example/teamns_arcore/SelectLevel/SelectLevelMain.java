package com.example.teamns_arcore.SelectLevel;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamns_arcore.DashboardActivity;
import com.example.teamns_arcore.MainActivity;
import com.example.teamns_arcore.R;

import com.example.teamns_arcore.SQLiteHelper;
import com.example.teamns_arcore.game.TimerActivity;
import com.example.teamns_arcore.game.GameActivity;


import java.io.File;


public class SelectLevelMain extends AppCompatActivity {
    TextView count_view;
    RelativeLayout count_view_layout;

    CountDownTimer countDownTimer;
    boolean isrunning = false;
    // 로그인 add
    SQLiteDatabase sqLiteDatabaseObj; // == private SQLiteDatabase db;
    SQLiteHelper sqLiteHelper;
    String EmailHolder;
    TextView Name;
    //
    MediaPlayer mediaPlayer;
    int currentPosition = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectlevel);

        //배경음악
        mediaPlayer = MediaPlayer.create(this, R.raw.openning);
        mediaPlayer.setLooping(true);
        //mediaPlayer.start();


        // 버튼 리스너
        findViewById(R.id.level1Btn).setOnClickListener(onClickListener);
        findViewById(R.id.level2Btn).setOnClickListener(onClickListener);
        findViewById(R.id.level3Btn).setOnClickListener(onClickListener);
        findViewById(R.id.level4Btn).setOnClickListener(onClickListener);

        // 로그인 add
        Name = (TextView) findViewById(R.id.textView1);
        Intent userNameintent = getIntent();
        sqLiteHelper = new SQLiteHelper(SelectLevelMain.this);
        // MainActivity에서 유저id 받기
        EmailHolder = userNameintent.getStringExtra(MainActivity.UserId);
        // TextView에 이름 넣어주기
        select();
        Name.setText("어서오세요. "+select()+" 님");
        //Name.setText("어서오세요. 아무개 님");
        //
    }
    // 이름 가져오기
    public String select() {
        sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        // SELECT name from UserTable WHERE email = 'test';
        Cursor mCursor = sqLiteDatabaseObj.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE "+ SQLiteHelper.Table_Column_2_Email +" = '"+ EmailHolder+"';", null);
        // Android android.database.CursorIndexOutOfBoundsException 에러 방지
        // cursor의 위치가 처음에 위치하고 있지 않았을 때 나는 에러
        // 값을 가지고 있으나 Position이 잘못된 경우 값을 재대로 가지고 올 수 없다.
        // cursor.moveToFirst() 를 사용해서 cursor의 위치를 제일 처음으로 바꿔준다.
        mCursor.moveToFirst();
        String selectName = mCursor.getString(1); // name의 위치가 1번째 (0번째는 index)
        mCursor.close();
        return selectName;
    }

    //버튼 클릭 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //leveltwo = new Intent(SelectLevelMain.this, SelectLevelActivity.class);
            switch (v.getId()) {
                case R.id.level1Btn:
                    setCount_view(SelectLevelActivity.class, 1);
                    break;
                case R.id.level2Btn:
                    setCount_view(SelectLevelActivity.class, 2);
                    break;
                case R.id.level3Btn:
                    setCount_view(SelectLevelActivity.class, 3);
                    break;
                case R.id.level4Btn:
                    setCount_view(SelectLevelActivity.class, 4);
                    break;
            }
        }
    };

    // 레벨 버튼 intent
    private void levelActivity(Class c, int i) {
        Intent levelintent = new Intent(this, c);
        levelintent.putExtra("choice", (int) i);
        startActivity(levelintent);
        finish();
    }
    //


    //카운트 다운 후 실행할 액티비티 변수
    private void setCount_view(Class c, int selectLevel) {
        // 화면에 보일 TextView
        count_view = (TextView) findViewById(R.id.count_view);
        count_view_layout = (RelativeLayout) findViewById(R.id.count_view_layout);

        //3초 타이머
        String conversionTime = "3";

        // 카운트 다운 시작
        count_view_layout.setVisibility(View.VISIBLE);
        long conversionTime2 = 0;

        // 변환시간
        conversionTime2 = Long.valueOf(conversionTime) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        countDownTimer = new CountDownTimer(conversionTime2, 1000) {
            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {
                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000));
                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000 + 1); // 나머지
                count_view.setText(second);
                isrunning = true;
            }

            public void onFinish() {
                levelActivity(SelectLevelActivity.class, selectLevel);
                count_view.setText("시작~!");
                isrunning = true;
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            if (isrunning) {
                countDownTimer.cancel();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.seekTo(currentPosition);
        if (DashboardActivity.ismute) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
            if (isrunning) {
                countDownTimer.cancel();
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            if (isrunning) {
                countDownTimer.cancel();
            }
        }
    }

}
