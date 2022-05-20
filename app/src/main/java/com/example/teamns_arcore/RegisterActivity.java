package com.example.teamns_arcore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText Email, Password, Name ;
    Button Register;
    String NameHolder, EmailHolder, PasswordHolder;
    Boolean EditTextEmptyHolder;
    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder ;
    SQLiteHelper sqLiteHelper;
    Cursor cursor;
    String F_Result = "찾지 못했습니다.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Register = (Button)findViewById(R.id.buttonRegister);

        Email = (EditText)findViewById(R.id.editEmail);
        Password = (EditText)findViewById(R.id.editPassword);
        Name = (EditText)findViewById(R.id.editName);

        sqLiteHelper = new SQLiteHelper(this);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 용량이 없는 경우 SQLite 데이터베이스 생성
                SQLiteDataBaseBuild();

                // 용량이 없으면 SQLite 테이블을 생성합니다.
                SQLiteTableBuild();

                // EditText 비었는지 아닌지 체크
                CheckEditTextStatus();

                // 이메일이 있는지 없는지 확인하기
                CheckEmailAlreadyExistsOrNot();

                // EditText 삽입 프로세스가 완료하고 지우기
                EmptyEditTextAfterDataInsert();
            }
        });

    }

    public void SQLiteDataBaseBuild(){
        //DB생성및 DB오픈
        // MODE_PRIVATE는 자기 앱에서만 사용하도록 설정하는 기본 값
        sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }



    public void SQLiteTableBuild() {
         //  execSQL -> CREATE TABLE, DELETE, INSERT , rawQuery -> SELECT
        //sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + SQLiteHelper.TABLE_NAME + "(" + SQLiteHelper.Table_Column_ID + " PRIMARY KEY AUTOINCREMENT NOT NULL, " + SQLiteHelper.Table_Column_1_Name + " VARCHAR, " + SQLiteHelper.Table_Column_2_Email + " VARCHAR, " + SQLiteHelper.Table_Column_3_Password + " VARCHAR);");
        sqLiteDatabaseObj.execSQL(
                // CREATE TABLE IF NOT EXISTS : 테이블이 존재하지 않을 경우에만 생성
                "CREATE TABLE IF NOT EXISTS " + SQLiteHelper.TABLE_NAME + "(" + SQLiteHelper.Table_Column_ID +" INTEGER, "+ SQLiteHelper.Table_Column_1_Name + " VARCHAR, " + SQLiteHelper.Table_Column_2_Email + " VARCHAR, " + SQLiteHelper.Table_Column_3_Password + " VARCHAR);");
    }

    // SQLite 데이터 삽입
    public void InsertDataIntoSQLiteDatabase(){
        // editText가 비어 있지 않으면 실행
        if(EditTextEmptyHolder == true){
            // 데이터를 삽입
            SQLiteDataBaseQueryHolder = "INSERT INTO "+ SQLiteHelper.TABLE_NAME+" (name,email,password) VALUES('"+NameHolder+"', '"+EmailHolder+"', '"+PasswordHolder+"');";
            // 쿼리 실행
            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
            // 쿼리 닫음
            sqLiteDatabaseObj.close();

            Toast.makeText(RegisterActivity.this,"회원가입되었습니다.", Toast.LENGTH_SHORT).show();

        }
        else {
            // EditText 비어있으면 실행
            Toast.makeText(RegisterActivity.this,"모든 칸을 채워주세요", Toast.LENGTH_SHORT).show();
        }

    }
    // 삽입하면 EditText 지워주기
    public void EmptyEditTextAfterDataInsert(){
        Name.getText().clear();
        Email.getText().clear();
        Password.getText().clear();
    }

    // EditText 확인
    public void CheckEditTextStatus(){
        NameHolder = Name.getText().toString() ;
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();

        if(TextUtils.isEmpty(NameHolder) || TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)){
            EditTextEmptyHolder = false ;
        }
        else {
            EditTextEmptyHolder = true ;
        }
    }

    // 이메일 있는지 없는지 확인
    public void CheckEmailAlreadyExistsOrNot(){
         // 읽고 쓰기 위해 DB 연다
        sqLiteDatabaseObj = sqLiteHelper.getWritableDatabase();
        // 이메일 검색
        cursor = sqLiteDatabaseObj.query(SQLiteHelper.TABLE_NAME, null, " " + SQLiteHelper.Table_Column_2_Email + "=?", new String[]{EmailHolder}, null, null, null);

        while (cursor.moveToNext()) {

            if (cursor.isFirst()) {
                cursor.moveToFirst();
                //이메일이 이미 존재하는 경우
                //F_Result = "Email Found";
                F_Result = "이미 있는 ID입니다.";
                cursor.close();
            }
        }
        // 최종확인
        CheckFinalResult();

    }

    public void CheckFinalResult(){
        // 이메일 확인
        //if(F_Result.equalsIgnoreCase("Email Found")){
        if(F_Result.equals("찾았습니다.")){
            // If email is exists then toast msg will display.
            Toast.makeText(RegisterActivity.this,"이미 있는 ID입니다.",Toast.LENGTH_SHORT).show();

        }else {
            // 이메일이 이미 존재하지 않으면 삽입
            InsertDataIntoSQLiteDatabase();
        }
        F_Result = "찾지 못했습니다" ;

    }
    
}