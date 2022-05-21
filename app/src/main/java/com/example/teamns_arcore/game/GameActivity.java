package com.example.teamns_arcore.game;

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
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.teamns_arcore.DashboardActivity;
import com.example.teamns_arcore.MainActivity;
import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.ChartActivity;
import com.example.teamns_arcore.Record.Model.RecordModel;
import com.example.teamns_arcore.Record.RecordSQLiteHelper;
import com.example.teamns_arcore.Record.TableActivity;
import com.example.teamns_arcore.SQLiteHelper;
import com.example.teamns_arcore.SelectLevel.Database.DatabaseHelper;
import com.example.teamns_arcore.SelectLevel.Model.StractEn;
import com.example.teamns_arcore.SelectLevel.SelectLevelActivity;
import com.example.teamns_arcore.SelectLevel.SelectLevelMain;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.speech.tts.TextToSpeech.ERROR;

public class GameActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    int currentPosition;

    TextView myTextView, myCatchView, questionTxtView, hintTxtView, correctTxtView_count, incorrectTxtView_count, timeOverTxtView_count;

    ////타이머 관련 변수//////////
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    private int timerValue;
    //////////////////////////

    ///////힌트변수///////
    private TextToSpeech tts;

    EditText answerTxtView;

    GLSurfaceView mSurfaceView;
    MainRenderer mRenderer;

    Session mSession;
    Config mConfig;

    boolean mUserRequestedInstall = true, isModelInit = false, mCatched = false, flag = true;

    float mCurrentX, mCurrentY;
//    float mCatchX, mCatchY;

    //이동, 회전 이벤트 처리할 객체
    GestureDetector mGestureDetector;

    DatabaseHelper databaseHelper;

    Button colorBtn, skipBtn, hintBtn, submitBtn, repeatBtn, returnRecordBtn;

    ArrayList<StractEn> seArrList;

    View hintDialogView, resultDialogView;

    int count = 0;

    int answerCount = 0;
    int incorrectCount = 0;
    int levelNum;

    SQLiteDatabase database;

    LocalDate localDate = LocalDate.now();
    LocalTime localTime = LocalTime.now();

    int hour = localTime.getHour();
    int min = localTime.getMinute();

    String currentTime = String.format("%s %d:%d", localDate, hour, min);

    float[][] colorCorrections = new float[][]{
            {0.8f, 0.8f, 0.8f, 0.8f},
            {1.0f, 0.0f, 0.0f, 0.8f},
            {0.0f, 1.0f, 0.0f, 0.8f},
            {0.0f, 0.0f, 1.0f, 0.8f}

    };

    String[] alphabetArr = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    float[] colorCorrection = new float[4];

    final int MAX = 26;

//    float [] modelMatrix = new float[16];

    float[][] modelArrayMatrix = new float[MAX][16];


    float[][] pixedMatrix = new float[][]{
            {0.0f, 0.0f, 0.0f},
            {0.1f, 0.1f, -2.4f},
            {0.2f, 0.2f, 2.4f},
            {0.3f, 0.3f, -2.3f},
            {0.4f, 0.4f, 2.3f},
            {0.5f, 0.5f, -2.2f},
            {0.6f, 0.6f, 2.2f},
            {0.7f, 0.7f, -2.1f},
            {0.8f, 0.8f, 2.1f},
            {0.9f, 0.9f, -2.0f},
            {1.0f, -0.0f, 2.0f},
            {1.1f, -0.1f, -1.9f},
            {1.2f, -0.2f, 1.9f},
            {1.3f, -0.3f, -1.8f},
            {1.4f, -0.4f, 1.8f},
            {1.5f, -0.5f, -1.7f},
            {1.6f, -0.6f, 1.7f},
            {1.7f, -0.7f, -1.6f},
            {1.8f, -0.8f, 1.6f},
            {1.9f, -0.9f, -1.5f},
            {2.0f, 0.0f, 1.5f},
            {2.1f, 0.1f, -1.4f},
            {2.2f, 0.2f, 1.4f},
            {2.3f, 0.3f, -1.3f},
            {2.4f, 0.4f, 1.3f},
            {-0.1f, 0.5f, -1.2f},
            {-0.2f, 0.6f, 1.2f},
            {-0.3f, 0.7f, -1.1f},
            {-0.4f, 0.8f, 1.1f},
            {-0.5f, 0.9f, -1.0f},
            {-0.6f, 0.0f, 1.0f},
            {-0.7f, -0.1f, -0.9f},
            {-0.8f, -0.2f, 0.9f},
            {-0.9f, -0.3f, -0.8f},
            {-1.0f, -0.4f, 0.8f},
            {-1.1f, -0.5f, -0.7f},
            {-1.2f, -0.6f, 0.7f},
            {-1.3f, -0.7f, -0.6f},
            {-1.4f, -0.8f, 0.6f},
            {-1.5f, -0.9f, -0.5f},
            {-1.6f, -0.0f, 0.5f},
            {-1.7f, 0.1f, -0.4f},
            {-1.8f, 0.2f, 0.4f},
            {-1.9f, 0.3f, -0.3f},
            {-2.0f, 0.4f, 0.3f},
            {-2.1f, 0.5f, -0.2f},
            {-2.2f, 0.6f, 0.2f},
            {-2.3f, 0.7f, -0.1f},
            {-2.4f, 0.8f, 0.1f},
            {-2.5f, 0.9f, -0.05f}
    };

    //    ArrayList<Integer> ranNum = new ArrayList<>();
    int[] ranNum = new int[MAX];

    // 입력을 넣을 String
    String insertText = "";

    ArrayList<String> englishSplit = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBarAndTitleBar();
        setContentView(R.layout.activity_game);

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
//                mCatchX = event.getX();
//                mCatchY = event.getY();
//
//                Log.d("확인 한번", event.getX() + " , " + event.getY());
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


                List<HitResult> results = frame.hitTest(600.0f, 600.0f);
                for (HitResult result : results) {
                    Pose pose = result.getHitPose(); // 증강 공간에서의 좌표
//                        float [] modelMatrix = new float[16];

                    if (!isModelInit && flag) {
                        isModelInit = true;
                        flag = false;
                        for (int i = 0; i < MAX; i++) {
//                            pose.toMatrix(modelMatrix, 0); // 좌표를 가지고 matrix 화 함
                            pose.toMatrix(modelArrayMatrix[i], 0);
                            Matrix.translateM(modelArrayMatrix[i], 0,
                                    pixedMatrix[ranNum[i]][0], pixedMatrix[ranNum[i]][1], pixedMatrix[ranNum[i]][2]);

                            // 변경된 좌표를 알기 위한 변수
//                            modelTransArrayMatrix[i][0] = pose.tx() + pixedMatrix[ranNum[i]][0];
//                            modelTransArrayMatrix[i][1] = pose.ty() + pixedMatrix[ranNum[i]][1];
//                            modelTransArrayMatrix[i][2] = pose.tz() + pixedMatrix[ranNum[i]][2];
                        }
                    }
                    LightEstimate estimate = frame.getLightEstimate();


                    colorBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setColorCorrection(estimate);
                        }
                    });

                    // 증강 공간의 좌표에 객체 있는지 받아온다(Plane 이 걸려 있는지 확인)
                    Trackable trackable = result.getTrackable();

                    // 크기 변경(비율)
//                        Matrix.scaleM(modelMatrix,0,1f,2f, 1f);
//                    Log.d("모델 매트릭스", Arrays.toString(modelMatrix));

                    // 이동(거리)
//                        Matrix.translateM(modelMatrix, 0,0f,0.1f,0f);

                    // 회전(각도)               옵셋,       각도,   축이 0 혹은 양수, 음수만 중요
                    // 수치조절은 각도로 하기 때문에 축의 숫자는 큰 의미가 없다. 음수, 양수만 중요
//                        Matrix.rotateM(modelMatrix,0,45,1f,0f,1f);
//                    mRenderer.mObj.setModelMatrix(modelMatrix);

                    for (int i = 0; i < MAX; i++) {
                        mRenderer.arrayObj.get(i).setModelMatrix(modelArrayMatrix[i]);
                    }
                }
//                }


//                    mTouched = false;

                //Session으로부터 증강현실 속에서의 평면이나 점 객체를 얻을 수 있다.
                //                                   Plane   Point
                Collection<Plane> planes = mSession.getAllTrackables(Plane.class);


                boolean isPlaneDetected = false;

                //ARCore 상의 Plane 들을 얻는다.
                for (Plane plane : planes) {

                    // plane 이 정상이라면
                    if (plane.getTrackingState() == TrackingState.TRACKING &&
                            plane.getSubsumedBy() == null) { // 다른 평면이 존재하는지

                        isPlaneDetected = true;

                        //렌더링에서 plane 정보를 갱신하여 출력
//                        mRenderer.mPlane.update(plane);
                    }
                }
                float[] originalPicColor = new float[]{0.8f, 0.8f, 0.8f, 0.8f};

                if (mCatched) {
                    mCatched = false;

                    for(int i=0;i< MAX;i++){
                        mRenderer.picObjColor(originalPicColor, i);
                    }



                    splitEnglish();

                    for(int i=0;i< gljaIndex.size();i++){
                        float[] picColor = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
                        mRenderer.picObjColor(picColor, gljaIndex.get(i));
                    }



//                    results = frame.hitTest(mCatchX, mCatchY);
//
//                    for (HitResult result : results) {
//                        Pose pose = result.getHitPose(); // 증강 공간에서의 좌표
//                        if (catchCheck(pose.tx(), pose.ty(), pose.tz())) {
//                            // 클릭확인용
//                            float[] picColor = new float[]{0.2f, 0.2f, 0.2f, 0.8f};
//                            mRenderer.picObjColor(picColor, catchIDX);
//                            insertText += String.valueOf(catchIDX);
//                            answerTxtView.setText(insertText);
//                        } else {
//
//                        }
//                    }
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
        String[] ranNumEng = intent.getStringArrayExtra("RandomEng");
        String[] ranNumKor = intent.getStringArrayExtra("RandomKor");
        levelNum = intent.getIntExtra("Level", 0);

        for (String eng: ranNumEng) {
            englishSplit.add(eng);
        }

        questionTxtView.setText(String.format("[ %s ]", ranNumKor[0]));

        if (count < 10) {
            skipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    count++;

                    if (count < ranNumKor.length) {
                        questionTxtView.setText(String.format("[ %s ]", ranNumKor[count]));
                        incorrectCount++;
                        Log.d("답틀림 : ", incorrectCount + "");
    //                    Log.d("ranNumKor[count-1] : ", ranNumKor[count-1] + "");
                    } else {
                        incorrectCount++;
                        gameResultDialog();
                    }
                }
            });
        }

        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.setVolume(0.1f, 0.1f);
                hintDialogView = View.inflate(GameActivity.this, R.layout.activity_hint_dialog, null);
                AlertDialog.Builder hintDialogBuilder = new AlertDialog.Builder(GameActivity.this);
                AlertDialog hintDialog = hintDialogBuilder.create();
                hintDialog.setView(hintDialogView);
                hintDialog.show();

                String word = ranNumEng[count];

                String mosaic = "*";

                StringBuffer mosaicBuffer = new StringBuffer();

                for (int i = 2; i < word.length(); i++) {
                    mosaicBuffer.append(mosaic);
                }

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(word);
                stringBuffer.replace(1, word.length() - 1, mosaicBuffer.toString());


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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ranNumEng[count].equals(answerTxtView.getText().toString())) {
                    count++;
                    if (count < ranNumEng.length) {
                        Toast.makeText(getApplicationContext(), "정답입니다!!!", Toast.LENGTH_SHORT).show();
                        questionTxtView.setText(String.format("[ %s ]", ranNumKor[count]));
                        answerTxtView.setText("");
                        answerCount++;
                        Log.d("답맞힘 : ", answerCount + "");
                    } else {
                        answerCount++;
                        gameResultDialog();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "틀렸어요 ㅠㅠ", Toast.LENGTH_SHORT).show();
                }
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

//        mRenderer.mObj.setColorCorrection(colorCorrection);

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
//            mTouched = true;
            mCurrentX = event.getX();
            mCurrentY = event.getY();
        }
        return true;
    }

//    int catchIDX;


//    boolean catchCheck(float x, float y, float z) {
//
//        for (catchIDX = 0; catchIDX < MAX; catchIDX++) {
//
//            float[][] resAll = mRenderer.arrayObj.get(catchIDX).getMinMaxPoint();
//            float[] minPoint = resAll[0];
//            float[] maxPoint = resAll[1];
//
//            // 범위가 좁으므로 범위를 강제로 넓혀준다(민감도를 떨어뜨린다)
//            if (x >= minPoint[0] - 100.0f && x <= maxPoint[0] + 100.0f &&
//                    y >= minPoint[1] - 100.0f && y <= maxPoint[1] + 100.0f &&
//                    z >= minPoint[2] - 100.0f && z <= maxPoint[2] + 100.0f) {
//                return true;
//            }
//        }
//        return false;
//    }


    void randomNum() {
        int random;
        for (int i = 0; i < MAX; i++) {
            random = (int) (Math.random() * 50);
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

    void setColorCorrection(LightEstimate estimate) {
        colorCorrection = colorCorrections[i];

        mRenderer.setColorCorrection(colorCorrection);

        i++;
        i %= 4;
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
        //super.onBackPressed();
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

        englishSplit.clear();

        Toast.makeText(getApplicationContext(), "퀴즈를 모두 풀었어요.", Toast.LENGTH_SHORT).show();
        Log.d("답갯수 : ", (answerCount + incorrectCount) + "");
        resultDialogView = View.inflate(GameActivity.this, R.layout.activity_result_dialog, null);
        AlertDialog.Builder resultDialogBuilder = new AlertDialog.Builder(GameActivity.this);
        AlertDialog resultDialog = resultDialogBuilder.create();
        resultDialog.setView(resultDialogView);
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

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, SelectLevelMain.class);
                startActivity(intent);
            }
        });

        returnRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, ChartActivity.class);
                startActivity(intent);
                insertData(RecordSQLiteHelper.Table_Column_ID, currentTime, answerCount, timerValue, 90, levelNum);

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

    ArrayList<Integer> gljaIndex = new ArrayList<>();

    void splitEnglish(){
        if(gljaIndex != null) {
            gljaIndex.clear();
        }

        String[] glja = englishSplit.get(count).split("");

        HashSet<String> hashSet = new HashSet<>(Arrays.asList(glja));

        String[] resultGlja = hashSet.toArray(new String[0]);

        for(int i = 0 ; i < resultGlja.length; i++){
            for(int j = 0; j<alphabetArr.length;j++){
                if(resultGlja[i].equals(alphabetArr[j])){
                    gljaIndex.add(j);
                }
            }
        }
    }
}