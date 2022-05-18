package com.example.teamns_arcore.SelectLevel;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.TimerActivity;
import com.example.teamns_arcore.SelectLevel.Database.DatabaseHelper;

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
    }

    //버튼 클릭 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //leveltwo = new Intent(SelectLevelMain.this, SelectLevelActivity.class);
            switch (v.getId()){
                case R.id.level1Btn:
                    setCount_view();
                    break;
                case R.id.level2Btn:
                    levelActivity(SelectLevelActivity.class,2);
                    //leveltwo.putExtra("choice", (int)2);
                    //startActivity(leveltwo);
                    break;
                case R.id.level3Btn:
                    levelActivity(SelectLevelActivity.class,3);
                    break;
                case R.id.level4Btn:
                    levelActivity(SelectLevelActivity.class,4);
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

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void setCount_view(){
        // 화면에 보일 TextView
        count_view = (TextView)findViewById(R.id.count_view);
        count_view_layout = (RelativeLayout)findViewById(R.id.count_view_layout);
        //5초 타이머
        String conversionTime = "4";

        // 카운트 다운 시작
        count_view_layout.setVisibility(View.VISIBLE);
        long conversionTime2 = 0;

        // 변환시간
        conversionTime2 = Long.valueOf(conversionTime) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        new CountDownTimer(conversionTime2, 1000) {
            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {
                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지
                count_view.setText(second);
            }
            public void onFinish() {
                //끝날때 실행
                count_view_layout.setVisibility(View.GONE);
                //myStartActivity(SelectLevelActivity.class); --> levelintent 사용해서 막음
                levelActivity(SelectLevelActivity.class,1);
            }
        }.start();
    }

}
