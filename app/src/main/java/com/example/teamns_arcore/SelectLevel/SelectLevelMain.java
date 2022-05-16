package com.example.teamns_arcore.SelectLevel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.teamns_arcore.R;

public class SelectLevelMain extends AppCompatActivity {


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
            switch (v.getId()){
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
                case R.id.resetdataBtn:
                    //데이터 초기화
                    break;
            }
        }
    };

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }


}
