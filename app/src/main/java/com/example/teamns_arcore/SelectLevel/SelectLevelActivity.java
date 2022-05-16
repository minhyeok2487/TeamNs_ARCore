package com.example.teamns_arcore.SelectLevel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.ChartActivity;
import com.example.teamns_arcore.SelectLevel.Database.DatabaseHelper;
import com.example.teamns_arcore.SelectLevel.Model.StractEn;
import com.example.teamns_arcore.SelectLevel.adapter.EnglishAdapter;
import com.example.teamns_arcore.game.GameActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class SelectLevelActivity extends AppCompatActivity {

    ArrayList<StractEn> arrayList = new ArrayList<StractEn>();
    int[] newrand = new int[10];
    ArrayAdapter adapter;
    ListView englist;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectlevel_list);
        englist = findViewById(R.id.englist);

        DatabaseHelper mDBHELPER = new DatabaseHelper(SelectLevelActivity.this, "lv_one_quiz");
        File database = getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);

        if(database.exists()==false){
            mDBHELPER.getReadableDatabase();
            if(!copydatabase(SelectLevelActivity.this)){
                return;
            }
        }
        arrayList = mDBHELPER.getEnglish(); //  mDBHELPER.getEnglish() == return arrayListEng

        gogogo();

        // 버튼 리스너
        findViewById(R.id.button).setOnClickListener(onClickListener);
        findViewById(R.id.gotogameBtn).setOnClickListener(onClickListener);
        findViewById(R.id.gotoChartBtn).setOnClickListener(onClickListener);
    }

    //버튼 클릭 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button:
                    gogogo();
                    break;
                case R.id.gotogameBtn:
                    myStartActivity(GameActivity.class);
                    break;
                case R.id.gotoChartBtn:
                    myStartActivity(ChartActivity.class);
                    break;
            }
        }
    };

    public void gogogo(){
        ArrayList<StractEn> arrayList2 = new ArrayList<StractEn>();
        rand(arrayList2);
        newarr(arrayList2);
        //G로 하니까 메인엑티비티에서 벗어났을때 출력이안되는 문제발생해서
        //G클래스지우고 Context를 화면에서 받음
        adapter = new EnglishAdapter(this, arrayList2);
        adapter.notifyDataSetChanged();

        englist.setAdapter(adapter);
    }
    public void newarr( ArrayList<StractEn> arrayList2){
        for(int i = 0; i <10; i++){
            int good = Integer.parseInt(arrayList.get(newrand[i]).flagtime);
            good +=1;
            arrayList.get(newrand[i]).setFlagtime(String.valueOf(good));
            arrayList2.add(new StractEn(arrayList.get(newrand[i]).getEnglish(),arrayList.get(newrand[i]).means,arrayList.get(newrand[i]).flagtime));
        }
    }

    public void rand( ArrayList<StractEn> arrayList2){
        int count = 10; // 난수 생성 갯수
        int a[] = new int[count];
        Random r = new Random();

        for(int i=0; i<count; i++){
            a[i] = r.nextInt(20); // 0~20까지의 난수
            for(int j=0; j<i; j++){
                if(a[i] == a[j]){
                    i--;
                }
            }
        }

        for(int i = 0; i<10; i++){
            newrand[i] = a[i];
            System.out.println("랜덤수"+newrand[i]);
        }
    }

    public Boolean copydatabase(Context context){
        try {
            InputStream inputStream = context.getAssets().open(DatabaseHelper.DBNAME);
            String OutFileName = DatabaseHelper.DBLOCAION+DatabaseHelper.DBNAME;
            File f = new File(OutFileName);
            f.getParentFile().mkdirs();
            OutputStream outputStream = new FileOutputStream(OutFileName);

            byte[] buffer = new byte[1024];
            int length = 0;
            while((length = inputStream.read(buffer))>0){
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

}