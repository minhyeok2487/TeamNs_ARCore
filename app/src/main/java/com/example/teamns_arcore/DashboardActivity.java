package com.example.teamns_arcore;

import static com.example.teamns_arcore.MainActivity.UserEmail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamns_arcore.SelectLevel.SelectLevelMain;

public class DashboardActivity extends AppCompatActivity {
    SQLiteDatabase sqLiteDatabaseObj; // == private SQLiteDatabase db;
    SQLiteHelper sqLiteHelper;
    String EmailHolder;
    //TextView Name;
    Button LogOUT, NameChg ;

    // inpublic static final String UserEmail = "";
    //다이얼로그
    EditText chgName;  // 대화상자 입력값 저장
    String txtNickName;
    LinearLayout dialogPopUp;
    //
    /* 원래 main에 있던 정보들  */
    Button StartBtn, EndBtn;
    //

    public static Boolean ismute =true;
    Button muteBtn;

    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Name = (TextView)findViewById(R.id.textView1);
        LogOUT = (Button)findViewById(R.id.button1);
        NameChg = (Button)findViewById(R.id.namechg);

        // /* 원래 main에 있던 정보들  */
        // 버튼 리스너
        StartBtn = findViewById(R.id.StartBtn); // -> start버튼이 로그인 후 페이지로 가야한다.
        EndBtn = findViewById(R.id.EndBtn);
        muteBtn = findViewById(R.id.muteBtn);
        //StartBtn.setVisibility(View.GONE);
        //EndBtn.setVisibility(View.GONE);
        //findViewById(R.id.regnicknameBtn).setOnClickListener(onClickListener);
        findViewById(R.id.StartBtn).setOnClickListener(onClickListener);
        findViewById(R.id.EndBtn).setOnClickListener(onClickListener);
        findViewById(R.id.muteBtn).setOnClickListener(onClickListener);
        //

        Intent intent = getIntent();
        sqLiteHelper = new SQLiteHelper(this);

        // MainActivity에서 유저id 받기
        EmailHolder = intent.getStringExtra(MainActivity.UserId);
        // TextView에 이름 넣어주기
        //select();
        //System.out.println("dash 에서 EmailHolder : "+ EmailHolder); /
        //System.out.println("select(); : "+select());
        //Name.setText("어서오세요. "+select()+" 님");

        // 로그아웃 버튼
        LogOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //버튼 클릭 시 현재 DashBoard 활동을 마칩니다.
                //finish();
                //Toast.makeText(DashboardActivity.this,"로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Logout();
            }
        });
        
        // 이름 변경 버튼
        NameChg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPopUp = (LinearLayout) View.inflate(DashboardActivity.this, R.layout.rename_dialog, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(DashboardActivity.this);
                dlg.setTitle("사용자 닉네임 변경");
                dlg.setView(dialogPopUp); // 대화상자에 뷰 넣음
                chgName = (EditText) dialogPopUp.findViewById(R.id.chgNickName); // edit
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        txtNickName = chgName.getText().toString(); // 수정한 글자 저장
                        if(!(txtNickName.isEmpty())){
                            // 여기서 수정해야함
                            sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
                            // UPDATE UserTable SET name='kimjin' WHERE email = 'test';
                            Cursor uCursor = sqLiteDatabaseObj.rawQuery("UPDATE " + SQLiteHelper.TABLE_NAME + " SET "+ SQLiteHelper.Table_Column_1_Name + " = ' " + txtNickName + "' WHERE "+ SQLiteHelper.Table_Column_2_Email +" = '"+ EmailHolder+"';", null);
                            uCursor.moveToFirst();
                            uCursor.close();
                            System.out.println(uCursor);
                            Toast.makeText(DashboardActivity.this, "변경되었습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(DashboardActivity.this, "빈칸이라 변경되지않았습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dlg.setNegativeButton("취소",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Toast.makeText(DashboardActivity.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.setCancelable(false); // 밖에 창 눌러도 안꺼지게
                dlg.show();
            }
        });

    }

    //버튼 클릭 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.regnicknameBtn:
//                    RegNickname();
//                    currentnickname.setText("현재 닉네임 : " + pname);
//                    break;
                case R.id.StartBtn:
                    //난이도 선택
                    //myStartActivity(SelectLevelMain.class);
                    Intent userNameintent = new Intent(DashboardActivity.this, SelectLevelMain.class);
                    userNameintent.putExtra(UserEmail, EmailHolder);
                    //intent.putExtra(UserId, NameHolder);
                    startActivity(userNameintent);
                    break;
                case R.id.EndBtn:
                    //게임종료메서드
                    finish();
                    break;
                case R.id.muteBtn:
                    if(ismute){
                        ismute = false;
                        muteBtn.setText("음소거 해제");
                    } else {
                        ismute = true;
                        muteBtn.setText("음소거");
                    }
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Logout();

    }
    public void Logout(){
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 1.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 1.5초가 지났으면 Toast 출력
        // 1500 milliseconds = 1.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "버튼을 한 번 더 누르시면 로그아웃 됩니다", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 1.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 1.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast.cancel();
            toast = Toast.makeText(this,"로그아웃 되었습니다",Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}