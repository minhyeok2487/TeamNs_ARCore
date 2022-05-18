package com.example.teamns_arcore.SelectLevel;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.teamns_arcore.R;

import com.example.teamns_arcore.game.TimerActivity;
import com.example.teamns_arcore.game.GameActivity;


import java.io.File;


public class SelectLevelMain extends AppCompatActivity {
    TextView count_view;
    RelativeLayout count_view_layout;
    // 레벨버튼 intent
    //Intent leveltwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectlevel);


        // 버튼 리스너
        findViewById(R.id.level1Btn).setOnClickListener(onClickListener);
        findViewById(R.id.level2Btn).setOnClickListener(onClickListener);
        findViewById(R.id.level3Btn).setOnClickListener(onClickListener);
        findViewById(R.id.level4Btn).setOnClickListener(onClickListener);
        findViewById(R.id.resetdataBtn).setOnClickListener(onClickListener);

        findViewById(R.id.level1Btn).setOnLongClickListener(onLongClickListener);
        findViewById(R.id.level2Btn).setOnLongClickListener(onLongClickListener);
        findViewById(R.id.level3Btn).setOnLongClickListener(onLongClickListener);
        findViewById(R.id.level4Btn).setOnLongClickListener(onLongClickListener);
    }

    //버튼 클릭 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //leveltwo = new Intent(SelectLevelMain.this, SelectLevelActivity.class);
            switch (v.getId()){
                case R.id.level1Btn:
                    setCount_view(GameActivity.class);
                    break;
                case R.id.level2Btn:
                    setCount_view(GameActivity.class);
                    break;
                case R.id.level3Btn:
                    setCount_view(GameActivity.class);
                    break;
                case R.id.level4Btn:
                    setCount_view(GameActivity.class);
                    break;
                case R.id.resetdataBtn:
                    //데이터 초기화 - 타이머 액티비티 박아둠
                    myStartActivity(TimerActivity.class);
                    break;
            }
        }
    };
    // 레벨 버튼 intent
    private void levelActivity(Class c, int i) {
        Intent levelintent = new Intent(this, c);
        levelintent.putExtra("choice", (int)i);
        startActivity(levelintent);
    }

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.level1Btn:
                    myStartActivity(SelectLevelActivity.class);
                    break;
                case R.id.level2Btn:
                    myStartActivity(SelectLevelActivity.class);
                    break;
                case R.id.level3Btn:
                    myStartActivity(SelectLevelActivity.class);
                    break;
                case R.id.level4Btn:
                    myStartActivity(SelectLevelActivity.class);
                    break;
            }
            return true;
        }
    };

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);

        startActivity(intent);
    }

    //카운트 다운 후 실행할 액티비티 변수
    private void setCount_view(Class c){
        // 화면에 보일 TextView
        count_view = (TextView)findViewById(R.id.count_view);
        count_view_layout = (RelativeLayout)findViewById(R.id.count_view_layout);
        //3초 타이머
        String conversionTime = "4";

        // 카운트 다운 시작
        count_view_layout.setVisibility(View.VISIBLE);
        long conversionTime2 = 0;

        // 변환시간
        conversionTime2 = Long.valueOf(conversionTime) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        CountDownTimer countDownTimer = null;
        final boolean[] mrunning = {true};
        countDownTimer = new CountDownTimer(conversionTime2, 1000) {
            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {
                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지
                count_view.setText(second);
                if((getMin % (60 * 1000)) / 1000<3){
                    myStartActivity(c);
                } else if((getMin % (60 * 1000)) / 1000<2){
                    mrunning[0] = false;
                    count_view.setText("1");
                }
            }
            public void onFinish() {
                //끝날때 실행
                count_view_layout.setVisibility(View.GONE);
                //myStartActivity(SelectLevelActivity.class); --> levelintent 사용해서 막음
                levelActivity(SelectLevelActivity.class,1);
            }
        };
        if(mrunning[0]){
            countDownTimer.start();
        }else{
            countDownTimer.cancel();
        }
    }

}
