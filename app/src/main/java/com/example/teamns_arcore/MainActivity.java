package com.example.teamns_arcore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.teamns_arcore.SelectLevel.SelectLevelMain;

public class MainActivity extends AppCompatActivity {

    public static String pname ="";
    RelativeLayout editnicknameview;
    EditText editnicknametext;
    TextView currentnickname;
//1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentnickname = (TextView)findViewById(R.id.currentnickname);

        // 버튼 리스너
        findViewById(R.id.regnicknameBtn).setOnClickListener(onClickListener);
        findViewById(R.id.StartBtn).setOnClickListener(onClickListener);
        findViewById(R.id.EndBtn).setOnClickListener(onClickListener);
    }

    //버튼 클릭 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.regnicknameBtn:
                    RegNickname();
                    currentnickname.setText("현재 닉네임 : "+ pname);
                    break;
                case R.id.StartBtn:
                    //난이도 선택
                    myStartActivity(SelectLevelMain.class);
                    break;
                case R.id.EndBtn:
                    //게임종료메서드
                    break;
            }
        }
    };

    //초기 닉네임 등록 메소드
    //닉네임이 있으면 안보이고 없으면 보임
    public void RegNickname(){
        editnicknameview = findViewById(R.id.editnicknameview);
        editnicknametext = findViewById(R.id.editnicknametext);
        if(pname.length()<1){ //초기 닉네임이 등록되지 않았으면
            pname = editnicknametext.getText().toString();
            editnicknameview.setVisibility(View.GONE);//등록되면 닉네임 등록창 사라짐
        } else {
            editnicknameview.setVisibility(View.VISIBLE);
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}