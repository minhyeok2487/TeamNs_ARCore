package com.example.teamns_arcore;

import static android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamns_arcore.SelectLevel.SelectLevelMain;

public class MainActivity extends AppCompatActivity {

    public static String pname = "";
    RelativeLayout editnicknameview;
    EditText editnicknametext;
    //TextView currentnickname;
    //Button StartBtn, EndBtn;
    ContentValues row;
    
    // 로그인 구현으로 add한 부분
    Button LogInButton, RegisterButton ;
    //EditText Email, Password ;
    //String EmailHolder, PasswordHolder;
    EditText Name, Email, Password ;
    String NameHolder,EmailHolder, PasswordHolder;
    Boolean EditTextEmptyHolder;
    SQLiteDatabase sqLiteDatabaseObj;
    SQLiteHelper sqLiteHelper;
    Cursor cursor;
    String TempPassword = "찾지 못했습니다." ;
    public static final String UserEmail = "";
    public static final String UserId = "";
    //
    MediaPlayer mediaPlayer;
    int currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insert("테스트", "test", "1111");
        //currentnickname = findViewById(R.id.currentnickname);
        //runMusic();

        // 로그인 add
        LogInButton = (Button)findViewById(R.id.buttonLogin);
        RegisterButton = (Button)findViewById(R.id.buttonRegister);

        Email = (EditText)findViewById(R.id.editEmail);
        Password = (EditText)findViewById(R.id.editPassword);

        sqLiteHelper = new SQLiteHelper(this);

        //Adding click listener to log in button.
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calling EditText is empty or no method.
                CheckEditTextStatus();

                // Calling login method.
                LoginFunction();
            }
        });

        // 회원가입
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION);
                startActivity(intent);
            }
        });
        //
    }
    // 로그인 add
    // 로그인 시작
    @SuppressLint("Range") // -> 추가됨
    public void LoginFunction(){

        if(EditTextEmptyHolder) {
            // Opening SQLite database write permission.
            sqLiteDatabaseObj = sqLiteHelper.getWritableDatabase();

            cursor = sqLiteDatabaseObj.query(SQLiteHelper.TABLE_NAME, null, " " + SQLiteHelper.Table_Column_2_Email + "=?", new String[]{EmailHolder}, null, null, null);

            while (cursor.moveToNext()) {
                if (cursor.isFirst()) {
                    cursor.moveToFirst();
                    // 입력한 이메일과 관련된 비밀번호를 저장
                    TempPassword = cursor.getString(cursor.getColumnIndex(SQLiteHelper.Table_Column_3_Password));

                    cursor.close();
                }
            }
            // 저장 전에 체크!
            CheckFinalResult();
            // 로그인 정보 레벨 선택 페이지로 넘기기
            // 환영합니다 뒤에 붙을 거
        }
        else {
            // 로그인 EditText 중 하나라도 비어 있으면 실행!
            Toast.makeText(MainActivity.this,"빈칸이 있습니다.",Toast.LENGTH_SHORT).show();

        }

    }
    // 미리 데이터 저장 insert
    public void insert(String name, String email, String password){
        // 미리 데이터 넣어놓기
        sqLiteHelper = new SQLiteHelper(this);
        //sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        sqLiteDatabaseObj = sqLiteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤
        // 데이터의 삽입은 put을 이용한다.
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        sqLiteDatabaseObj.insert("UserTable", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)
        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.
    }

    // EditText를 비어있는지 확인하는 용도
    public void CheckEditTextStatus(){
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();
        //NameHolder = Name.getText().toString();
        //System.out.println("메인의 NameHolder : "+ NameHolder);
        // String값이 비어있는지를 체크하고싶은 경우는 TextUtils
        if( TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)){
            EditTextEmptyHolder = false ;
        }
        else {EditTextEmptyHolder = true;}
    }

    // Checking entered password from SQLite database email associated password.
    // 비밀번호 확인
    public void CheckFinalResult(){
        if(TempPassword.equals(PasswordHolder)){
            Toast.makeText(MainActivity.this,"로그인 성공",Toast.LENGTH_SHORT).show();

            // 로그인되면 로그인된 창으로 가기
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra(UserEmail, EmailHolder);
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this,"ID나 비밀번호가 틀렸습니다. 다시 시도하세요.",Toast.LENGTH_SHORT).show();
        }
        TempPassword = "찾지 못했습니다." ;
    }



}