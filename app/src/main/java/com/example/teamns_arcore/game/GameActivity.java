package com.example.teamns_arcore.game;

import static android.speech.tts.TextToSpeech.ERROR;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.teamns_arcore.DashboardActivity;
import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.ChartActivity;
import com.example.teamns_arcore.Record.RecordSQLiteHelper;
import com.example.teamns_arcore.SQLiteHelper;
import com.example.teamns_arcore.SelectLevel.SelectLevelActivity;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    int currentPosition;

    TextView questionTxtView, hintTxtView, correctTxtView_count, incorrectTxtView_count, timeOverTxtView_count;

    ////타이머 관련 변수//////////
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    private int timerValue;
    //////////////////////////

    ///////힌트변수///////
    private TextToSpeech tts;

    String answerString = "";
    ArrayList<String> answerStringArr = new ArrayList<>();

    TextView answerTxtView;

    GLSurfaceView mSurfaceView;
    MainRenderer mRenderer;

    Session mSession;
    Config mConfig;

    boolean mUserRequestedInstall = true, isModelInit = false, mCatched = false, flag = true;

    float mCurrentX, mCurrentY;
    float mCatchX, mCatchY;

    //이동, 회전 이벤트 처리할 객체
    GestureDetector mGestureDetector;

    Button colorBtn, skipBtn, hintBtn, submitBtn, repeatBtn, returnRecordBtn, deleteBtn;

    View hintDialogView, resultDialogView;

    int count = 0;

    int answerCount = 0;
    int incorrectCount = 0;
    int levelNum;

    String[] ranNumEng;
    String[] ranNumKor;

    SQLiteDatabase database;

    LocalDate localDate = LocalDate.now();


    String currentTime = String.format("%s", localDate); // -> data 날짜만 들어가게 변경

    SQLiteHelper sqLiteHelper;
    public static final String UserId = "";
    String EmailHolder;
    //

    float[][] colorCorrections = new float[][]{
            {0.8f, 0.8f, 0.8f, 0.8f},
            {1.0f, 0.0f, 0.0f, 0.8f},
            {0.0f, 0.0f, 0.0f, 0.8f},
            {0.0f, 0.0f, 1.0f, 0.8f}
    };

    String[] alphabetArr = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    float[] colorCorrection = new float[4];

    final int MAX = 26;

    float[][] modelArrayMatrix = new float[MAX][16];

    int[] ranNum = new int[MAX];

    float[][] pixedMatrix2;
    int randomMax, randomMax_x, randomMax_y, randomMax_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBarAndTitleBar();
        setContentView(R.layout.activity_game);

        randomMax_x = 7;
        randomMax_y = 2;
        randomMax_z = 3;
        randomMax = randomMax_x*randomMax_y*randomMax_z;
        pixedMatrix2 = new float[randomMax][3];
        for(int i=0; i<randomMax;i = i+randomMax_x){
            pixedMatrix2[i][0] = -3.0f;
            pixedMatrix2[i+1][0] = -2.0f;
            pixedMatrix2[i+2][0] = -1.0f;
            pixedMatrix2[i+3][0] = 0.0f;
            pixedMatrix2[i+4][0] = 1.0f;
            pixedMatrix2[i+5][0] = 2.0f;
            pixedMatrix2[i+6][0] = 3.0f;
        }
        for(int i=0; i<randomMax;i = i+randomMax_y){
            pixedMatrix2[i][1] = 0.0f;
            pixedMatrix2[i+1][1] = 1.0f;
        }
        for(int i=0; i<randomMax;i =i+randomMax_z){
            pixedMatrix2[i][2] = -3.0f;
            pixedMatrix2[i+1][2] = 0.0f;
            pixedMatrix2[i+2][2] = 3.0f;
        }

        //배경음악
        mediaPlayer = MediaPlayer.create(this, R.raw.game);
        mediaPlayer.setVolume(0.2f, 0.2f);
        mediaPlayer.setLooping(true);

        //TTS
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });

        mSurfaceView = findViewById(R.id.gl_surface_view);

        colorBtn = findViewById(R.id.colorBtn);

        skipBtn = findViewById(R.id.skipBtn);

        hintBtn = findViewById(R.id.hintBtn);

        submitBtn = findViewById(R.id.submitBtn);

        deleteBtn = findViewById(R.id.deleteBtn);

        answerTxtView = findViewById(R.id.answerTxtView);

        questionTxtView = findViewById(R.id.questionTxtView);

        //////타이머 관련////////
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");

        if (!running) { //running이 false이면 타이머 돌아감
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
        /////////////
        randomNum();

        for (int i = 0; i < ranNum.length; i++) {
            Log.d("랜덤", String.valueOf(ranNum[i]));
        }

        // 제스처이벤트 콜백함수 객체를 생성자 매개변수로 처리 (이벤트 핸들러)
        // 내꺼에서처리 this
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            // 한번 클릭 처리
            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                mCatched = true;
                mCatchX = event.getX();
                mCatchY = event.getY();

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isModelInit) {
                    for (int i = 0; i < MAX; i++) {
                        Matrix.rotateM(modelArrayMatrix[i], 0, -distanceX / 5, 0f, 1f, 0f);
                        Matrix.rotateM(modelArrayMatrix[i], 0, -distanceY / 5, 1f, 0f, 0f);
                    }
                }
                return true;
            }

        });

        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (displayManager != null) {
            displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
                @Override
                public void onDisplayAdded(int i) {

                }

                @Override
                public void onDisplayRemoved(int i) {

                }

                @Override
                public void onDisplayChanged(int i) {
                    synchronized (this) {
                        mRenderer.mViewportChanged = true;
                    }
                }
            }, null);
        }

        mRenderer = new MainRenderer(this, new MainRenderer.RenderCallback() {
            @Override
            public void preRender() {
                if (mRenderer.mViewportChanged) {
                    Display display = getWindowManager().getDefaultDisplay();
                    int displayRotation = display.getRotation();
                    mRenderer.updateSession(mSession, displayRotation);
                }

                // 카메라에 알려준다
                mSession.setCameraTextureName(mRenderer.getTextureId());

                // 세션으로부터 정보를 받아올 프레임
                Frame frame = null;

                try {
                    frame = mSession.update();
                } catch (CameraNotAvailableException e) {
                    e.printStackTrace();
                }

                // 프레임 변경시
                if (frame.hasDisplayGeometryChanged()) {
                    mRenderer.mCamera.transformDisplayGeometry(frame);
                }


                List<HitResult> results = frame.hitTest(mRenderer.mViewportWidth/2, mRenderer.mViewportHeight/2);
                for (HitResult result : results) {
                    Pose pose = result.getHitPose(); // 증강 공간에서의 좌표

                    if (!isModelInit && flag) {
                        isModelInit = true;
                        flag = false;
                        for (int i = 0; i < MAX; i++) {
//                            pose.toMatrix(modelMatrix, 0); // 좌표를 가지고 matrix 화 함
                            pose.toMatrix(modelArrayMatrix[i], 0);
                            Matrix.translateM(modelArrayMatrix[i], 0,
                                    pixedMatrix2[ranNum[i]][0], pixedMatrix2[ranNum[i]][1], pixedMatrix2[ranNum[i]][2]);
                        }
                    }
                    LightEstimate estimate = frame.getLightEstimate();


                    colorBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setColorCorrection();
                        }
                    });

                    for (int i = 0; i < MAX; i++) {
                        mRenderer.arrayObj.get(i).setModelMatrix(modelArrayMatrix[i]);
                    }
                }
                Collection<Plane> planes = mSession.getAllTrackables(Plane.class);


                boolean isPlaneDetected = false;

                //ARCore 상의 Plane 들을 얻는다.
                for (Plane plane : planes) {

                    // plane 이 정상이라면
                    if (plane.getTrackingState() == TrackingState.TRACKING &&
                            plane.getSubsumedBy() == null) { // 다른 평면이 존재하는지

                        isPlaneDetected = true;
                    }
                }

                if (mCatched) {
                    mCatched = false;
                    results = frame.hitTest(mCatchX, mCatchY);

                    for (HitResult result : results) {
                        Pose pose = result.getHitPose(); // 증강 공간에서의 좌표
                        if (catchCheck(pose.tx(), pose.ty(), pose.tz())) {

                            answerStringArr.add(alphabetArr[catchIDX]);
                            answerString = "";
                            for (String word: answerStringArr) {
                                answerString += word;
                            }
                            answerTxtView.setText(answerString);
                            break;
                        }
                    }
                }


                // 카메라 세팅
                Camera camera = frame.getCamera();
                float[] projMatrix = new float[16];
                camera.getProjectionMatrix(projMatrix, 0, 0.5f, 50f);
                float[] viewMatrix = new float[16];
                camera.getViewMatrix(viewMatrix, 0);

                mRenderer.setProjectionMatrix(projMatrix);
                mRenderer.updateViewMatrix(viewMatrix);
            }
        });

        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mSurfaceView.setRenderer(mRenderer);

        Intent intent = getIntent();
        ranNumEng = intent.getStringArrayExtra("RandomEng");
        ranNumKor = intent.getStringArrayExtra("RandomKor");
        levelNum = intent.getIntExtra("Level", 0);
        // user정보
        sqLiteHelper = new SQLiteHelper(GameActivity.this);
        EmailHolder = intent.getStringExtra(SelectLevelActivity.UserId);
        System.out.println("GameActivity EmailHolder : " + EmailHolder);

        questionTxtView.setText(String.format("[ %s ]", ranNumKor[0]));


        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;

                if (count < ranNumKor.length) {
                    questionTxtView.setText(String.format("[ %s ]", ranNumKor[count]));
                    incorrectCount++;
                    answerStringArr.clear();
                    answerString = "";
                    answerTxtView.setText(answerString);
                    Log.d("답틀림 : ", incorrectCount + "");
                    //                    Log.d("ranNumKor[count-1] : ", ranNumKor[count-1] + "");
                } else if (count == 10) {
                    incorrectCount++;
                    answerStringArr.clear();
                    answerString = "";
                    answerTxtView.setText(answerString);
                    gameResultDialog();
                }
            }
        });


        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.setVolume(0.1f, 0.1f);
                hintDialogView = View.inflate(GameActivity.this, R.layout.activity_hint_dialog, null);
                AlertDialog.Builder hintDialogBuilder = new AlertDialog.Builder(GameActivity.this);
                AlertDialog hintDialog = hintDialogBuilder.create();
                hintDialog.setView(hintDialogView);
                if (hintDialog.getWindow() != null) {
                    hintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    hintDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    hintDialog.setCancelable(false);
                }
                hintDialog.show();

                String word = ranNumEng[count];

                int num = (int) (Math.random()*word.length())+2;
                int [] numaaa = new int[num];
                for (int i = 0; i < num; i++) {
                    numaaa[i] = (int) (Math.random()*word.length());
                }

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(word);
                for(int i=0;i<numaaa.length;i++){
                    stringBuffer.replace(numaaa[i], numaaa[i]+1, "*");
                }



                hintTxtView = hintDialogView.findViewById(R.id.hintTxtView);
                hintTxtView.setText(String.format("[ %s ]", stringBuffer));
                tts.speak(ranNumEng[count], TextToSpeech.QUEUE_FLUSH, null);

                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // n초가 지나면 다이얼로그를 닫도록 타이머를 줌
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                hintDialog.dismiss();
                            }
                        };

                        Timer timer = new Timer();
                        timer.schedule(timerTask, 1000);
                    }
                });
                th.start();


            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answerStringArr.size() >0) {
                    answerString = "";
                    answerStringArr.remove(answerStringArr.size() - 1);
                    for (String word : answerStringArr) {
                        answerString += word;
                    }
                    answerTxtView.setText(answerString);
                }else{
                    answerStringArr.clear();
                }
            }
        });

        answerTxtView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                switch (keyCode){
                    case KeyEvent.KEYCODE_ENTER:
                        submit();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        RecordSQLiteHelper recordSQLite = new RecordSQLiteHelper(getApplicationContext());
        database = recordSQLite.getWritableDatabase();


    }

    // 버튼에 의한 조명 색상 변경
    public void btnClick(View view) {
        int color = ((ColorDrawable) view.getBackground()).getColor();

        float[] colorCorrection = {
                Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f,
                0.5f
        };

        for (int i = 0; i < MAX; i++) {
            mRenderer.arrayObj.get(i).setColorCorrection(colorCorrection);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (DashboardActivity.ismute) {
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
        requestCameraPermission();
        try {
            // 세션이 없다면 세션 생성
            if (mSession == null) {
                switch (ArCoreApk.getInstance().requestInstall(this, true)) {
                    case INSTALLED:
                        mSession = new Session(this);
                        Log.d("메인", " ARCore session 생성");
                        break;
                    //설치가 안되는 기계
                    case INSTALL_REQUESTED:
                        mUserRequestedInstall = false;
                        Log.d("메인", " ARCore 설치가 필요함");
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        mConfig = new Config(mSession);

        mSession.configure(mConfig);

        try {
            mSession.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }

        mSurfaceView.onResume();
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }

    @Override
    protected void onPause() {
        super.onPause();

        mSurfaceView.onPause();
        mSession.pause();
    }

    void hideStatusBarAndTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    0
            );
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 위임해서 정보를 받아옴
        mGestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mCurrentX = event.getX();
            mCurrentY = event.getY();
        }
        return true;
    }

    int catchIDX;


    boolean catchCheck(float x, float y, float z) {

        for (catchIDX = 0; catchIDX < MAX; catchIDX++) {

            float[][] resAll = mRenderer.arrayObj.get(catchIDX).getMinMaxPoint();
            float[] minPoint = resAll[0];
            float[] maxPoint = resAll[1];

            // 범위가 좁으므로 범위를 강제로 넓혀준다(민감도를 떨어뜨린다)
            if (x >= minPoint[0]-0.3f && x <= maxPoint[0]+0.3f  &&
                    y >= minPoint[1]-0.2f && y <= maxPoint[1]+0.8f  &&
                    z >= minPoint[2]-0.8f  && z <= maxPoint[2]+0.8f ) {
                return true;
            }
        }
        return false;
    }


    void randomNum() {
        int random;
        for (int i = 0; i < MAX; i++) {
            random = (int) (Math.random() * randomMax);
            ranNum[i] = random;
            for (int j = 0; j < i; j++) {
                if (ranNum[j] == random) {
                    i--;
                    break;
                }
            }
        }
    }

    int i = 0;

    void setColorCorrection() {
        i++;
        i %= 4;
        colorCorrection = colorCorrections[i];

        mRenderer.setColorCorrection(colorCorrection);
    }

    //타이머 변환 메서드
    public static int getSecondsFromDurationString(String value) {

        String[] parts = value.split(":");
        int seconds = 0, minutes = 0, hours = 0;

        if (parts.length == 2) {
            seconds = Integer.parseInt(parts[1]);
            minutes = Integer.parseInt(parts[0]);
        } else if (parts.length == 3) {
            seconds = Integer.parseInt(parts[2]);
            minutes = Integer.parseInt(parts[1]);
            hours = Integer.parseInt(parts[0]);
        }

        return seconds + (minutes * 60) + (hours * 3600);
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();

        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;

    @Override
    public void onBackPressed() {
        out();

    }

    public void out() {
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 1.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 1.5초가 지났으면 Toast 출력
        // 1500 milliseconds = 1.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "버튼을 한 번 더 누르시면 게임이 종료됩니다", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 1.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 1.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast.cancel();
            toast = Toast.makeText(this, "게임이 종료되었습니다", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    void gameResultDialog() {
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        timerValue = getSecondsFromDurationString(chronometer.getText().toString());

        Toast.makeText(getApplicationContext(), "퀴즈를 모두 풀었어요.", Toast.LENGTH_SHORT).show();
        Log.d("답갯수 : ", (answerCount + incorrectCount) + "");
        resultDialogView = View.inflate(GameActivity.this, R.layout.activity_result_dialog, null);
        AlertDialog.Builder resultDialogBuilder = new AlertDialog.Builder(GameActivity.this);
        AlertDialog resultDialog = resultDialogBuilder.create();
        resultDialog.setView(resultDialogView);
        if (resultDialog.getWindow() != null) {
            resultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            resultDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            resultDialog.setCancelable(false);
        }
        resultDialog.show();
        resultDialog.setCancelable(false);
        correctTxtView_count = resultDialogView.findViewById(R.id.correctTxtView_count);
        incorrectTxtView_count = resultDialogView.findViewById(R.id.incorrectTxtView_count);
        correctTxtView_count.setText(String.format("%s개", answerCount));
        incorrectTxtView_count.setText(String.format("%s개", incorrectCount));
        repeatBtn = resultDialogView.findViewById(R.id.repeatBtn);
        returnRecordBtn = resultDialogView.findViewById(R.id.returnRecordBtn);
        timeOverTxtView_count = resultDialogView.findViewById(R.id.timeOverTxtView_count);

        timeOverTxtView_count.setText(String.format("%s초", timerValue));
        float score = ((float) answerCount / (float) timerValue) * 10000;
        insertData(RecordSQLiteHelper.Table_Column_ID, currentTime, answerCount, timerValue, (int) score, levelNum);
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        returnRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, ChartActivity.class);
                startActivity(intent);


            }
        });

    }

    void insertData(String ID, String Date, int CorrectNum, int Timer, int Score, int Level) {

        ContentValues values = new ContentValues();
        values.put(RecordSQLiteHelper.Table_Column_ID, ID);
        values.put(RecordSQLiteHelper.Table_Column_1_Date, Date);
        values.put(RecordSQLiteHelper.Table_Column_2_CorrectNum, CorrectNum);
        values.put(RecordSQLiteHelper.Table_Column_3_Timer, Timer);
        values.put(RecordSQLiteHelper.Table_Column_4_Score, Score);
        values.put(RecordSQLiteHelper.Table_Column_5_Level, Level);

        database.insert(RecordSQLiteHelper.TABLE_NAME, null, values);

    }

    void submit() {
        if (ranNumEng[count].equals(answerTxtView.getText().toString())) {
                count++;
                if (count < ranNumEng.length) {
                    tts.speak(ranNumEng[count-1], TextToSpeech.QUEUE_FLUSH, null);
                    Toast.makeText(getApplicationContext(), "정답입니다!!!", Toast.LENGTH_SHORT).show();
                    questionTxtView.setText(String.format("[ %s ]", ranNumKor[count]));
                    answerTxtView.setText("");
                    answerCount++;
                    answerStringArr.clear();
                    answerString = "";

                    Log.d("답맞힘 : ", answerCount + "");
                } else {
                    tts.speak(ranNumEng[count-1], TextToSpeech.QUEUE_FLUSH, null);
                    answerCount++;
                    gameResultDialog();
                }
            } else {
                Toast.makeText(getApplicationContext(), "틀렸어요 ㅠㅠ", Toast.LENGTH_SHORT).show();
            }
        }
    }

