package com.example.teamns_arcore.SelectLevel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.ChartActivity;
import com.example.teamns_arcore.SelectLevel.Database.DatabaseHelper;
import com.example.teamns_arcore.SelectLevel.Model.StractEn;
import com.example.teamns_arcore.SelectLevel.adapter.EnglishAdapter;
import com.example.teamns_arcore.game.GameActivity;
import com.example.teamns_arcore.game.HintDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import static android.speech.tts.TextToSpeech.ERROR;
public class SelectLevelActivity extends AppCompatActivity {

    ArrayList<StractEn> arrayList = new ArrayList<StractEn>();
    int[] newrand = new int[10];
    ArrayAdapter adapter;
    ListView englist;
    Button button;
    File database;

    // 버튼 정보 가져오기
    Intent levelintent;
    int lv1,lv2,lv3,lv4;
    DatabaseHelper mDBHELPER;

    private TextToSpeech tts;// TTS 변수 선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectlevel_list);
        englist = findViewById(R.id.englist);
        // SelectLevelMain.java에서 보낸 파이어베이스에 저장된 정보가져오기
        //intent = getIntent();
        levelintent = getIntent();

        // lv에 따른 intent값 다르게 받기
        lv1 = levelintent.getIntExtra("choice", 1);
        lv2 = levelintent.getIntExtra("choice", 2);
        lv3 = levelintent.getIntExtra("choice", 3);
        lv4 = levelintent.getIntExtra("choice", 4);
        int a = lv1; int b = lv2; int c = lv3; int d = lv4;
        if(a==lv1){ // 같으면 자기 db table 가지고 와주라
            mDBHELPER = new DatabaseHelper(SelectLevelActivity.this, "levelone","lv_one_quiz");
        }else if(b==lv2){
            mDBHELPER = new DatabaseHelper(SelectLevelActivity.this, "leveltwo","lv_one_quiz");
        }else if(c==lv3){
            mDBHELPER = new DatabaseHelper(SelectLevelActivity.this, "levelthree","lv_one_quiz");
        }else if(d==lv4){
            mDBHELPER = new DatabaseHelper(SelectLevelActivity.this, "levelfour","lv_one_quiz");
        }else{
            System.out.println("어떤 버튼을 눌렀는지 모릅니다.");
        }

        File database = getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);

        if(database.exists()==false){
            mDBHELPER.getReadableDatabase();
            if(!copydatabase(SelectLevelActivity.this)){
                return;
            }
        }
        arrayList = mDBHELPER.getEnglish(); //  mDBHELPER.getEnglish() == return arrayListEng
        //
        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.ENGLISH);
                    tts.setSpeechRate((float)0.8); // 음성 속도 지정
                }
            }
        });


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
//
    ///
    public void gogogo(){
        ArrayList<StractEn> arrayList2 = new ArrayList<StractEn>();

        //rand(arrayList2);
        newarr(arrayList2);
        //G로 하니까 메인엑티비티에서 벗어났을때 출력이안되는 문제발생해서
        //G클래스지우고 Context를 화면에서 받음
        adapter = new EnglishAdapter(this, arrayList2);
        adapter.notifyDataSetChanged();

        englist.setAdapter(adapter);

        englist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                tts.speak(arrayList2.get(position).english,TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        String[] ranWordsKor = new String[arrayList2.size()];
        String[] ranWordsEng = new String[arrayList2.size()];
        for (int i = 0; i < arrayList2.size(); i++){
            ranWordsKor[i] = arrayList2.get(i).getMeans();
            ranWordsEng[i] = arrayList2.get(i).getEnglish();
//            Log.d("랜덤랜덤임", ranWords[i] + "");
        }
        Intent intentGame = new Intent(this, GameActivity.class);
        intentGame.putExtra("RandomKor", ranWordsKor);
        intentGame.putExtra("RandomEng", ranWordsEng);
        startActivity(intentGame);
        finish();

    }
    public void newarr( ArrayList<StractEn> arrayList2){
        Map<StractEn, Double> w = new HashMap<StractEn, Double>();
        for(int a = 0; a<arrayList.size(); a++){
            w.put(arrayList.get(a),Double.valueOf(arrayList.get(a).getFlagtime()));
        }
        int[] len = new int[10];
        // 중복검사 안함
        Random rand = new Random();
        for(int i =0; i<10;i++) {
            boolean overlap = false;
            int num = 0;

            StractEn res = getWeightedRandom(w, rand);

            for(int k=0;k<arrayList.size();k++){
                if(res.getEnglish().equals(arrayList.get(k).getEnglish())){
                    num = k;
                    break;
                }
            }
            for (int j = 0; j < i; j++) {
                if (res == arrayList2.get(j)) {
                    i--;
                    overlap = true;
                }
            }
            if(!overlap){
                arrayList2.add(res);
                Log.d("가중치 랜덤 뽑기", res.getEnglish());
                Double resFlag = Double.valueOf(res.getFlagtime())/2;
                arrayList.get(num).setFlagtime(String.valueOf(resFlag));
            }
        }


    }
    ///



    public static <E> E getWeightedRandom(Map<E, Double> weights, Random random) {
        E result = null;
        double bestValue = Double.MAX_VALUE;

        for (E element : weights.keySet()) {
            double value = -Math.log(random.nextDouble()) / weights.get(element);
            if (value < bestValue) {
                bestValue = value;
                result = element;
            }
        }
        return result;
    }
    ////
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
        Intent myStartintent = new Intent(this, c);
        startActivity(myStartintent);
    }



}