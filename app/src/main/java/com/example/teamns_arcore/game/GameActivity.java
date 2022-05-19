package com.example.teamns_arcore.game;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.teamns_arcore.R;
import com.example.teamns_arcore.SelectLevel.Database.DatabaseHelper;
import com.example.teamns_arcore.SelectLevel.Model.StractEn;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {


    TextView myTextView, myCatchView;

    ////타이머 관련 변수//////////
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    private int timerValue;
    //////////////////////////


    TextView answerTxtView, questionTxtView, hintTxtView;


    GLSurfaceView mSurfaceView;
    MainRenderer mRenderer;

    Session mSession;
    Config mConfig;

    boolean mUserRequestedInstall = true, isModelInit = false, mCatched = false, flag = true;

    float mCurrentX, mCurrentY, mCatchX, mCatchY;

    //이동, 회전 이벤트 처리할 객체
    GestureDetector mGestureDetector;

    DatabaseHelper databaseHelper;

    Button colorBtn, skipBtn, hintBtn;

    ArrayList<StractEn> seArrList;

    View dialogView;

    int count = 0;

    float[][] colorCorrections = new float[][]{
            {0.8f, 0.8f, 0.8f, 0.8f},
            {1.0f, 0.0f, 0.0f, 0.8f},
            {0.0f, 1.0f, 0.0f, 0.8f},
            {0.0f, 0.0f, 1.0f, 0.8f}

    };

    float[] colorCorrection = new float[4];

    final int MAX = 26;

//    float [] modelMatrix = new float[16];

    float[][] modelArrayMatrix = new float[MAX][16];
    float[][] modelTransArrayMatrix = new float[MAX][16];


    float[][] pixedMatrix = new float[][]{
            {0.3f, 3.2f, 2.3f},
            {0.4f, 1.4f, -3.1f},
            {0.5f, -2.2f, 3.3f},
            {0.7f, -0.2f, -0.3f},
            {0.9f, 1.2f, 1.3f},
            {1.1f, 2.4f, -1.6f},
            {1.3f, -2.5f, 0.3f},
            {1.5f, -1.4f, -2.4f},
            {1.8f, 0.2f, 2.4f},
            {1.9f, 1.8f, -2.9f},
            {2.3f, -2.2f, 1.7f},
            {2.4f, -1.3f, -0.4f},
            {2.7f, 0.1f, 1.3f},
            {2.8f, 2.4f, -0.7f},
            {3.1f, -0.6f, 1.9f},
            {3.3f, -1.9f, -2.5f},
            {3.4f, 1.5f, 2.5f},
            {3.6f, 3.0f, -2.7f},
            {3.8f, -2.3f, 1.2f},
            {3.9f, -1.2f, -3.2f},
            {4.0f, 2.9f, 2.9f},
            {4.3f, 1.7f, -0.7f},
            {4.5f, -0.8f, 1.3f},
            {4.7f, -3.2f, -3.1f},
            {4.1f, 2.7f, 2.7f},
            {-0.1f, 1.3f, -3.1f},
            {-0.3f, -1.8f, 1.3f},
            {-0.4f, -2.6f, -2.8f},
            {-0.7f, 2.9f, 2.7f},
            {-0.9f, 1.1f, -0.8f},
            {-1.1f, -0.4f, 0.9f},
            {-1.3f, -1.8f, -1.3f},
            {-1.5f, 1.7f, 2.6f},
            {-1.7f, 2.6f, -2.1f},
            {-1.9f, -0.7f, 2.2f},
            {-2.3f, -2.6f, -3.3f},
            {-2.4f, 2.5f, 1.7f},
            {-2.6f, 1.3f, -2.6f},
            {-2.8f, -0.8f, 1.9f},
            {-3.1f, -1.1f, -1.6f},
            {-3.3f, 1.6f, 0.4f},
            {-3.4f, 0.9f, -0.7f},
            {-3.6f, -2.5f, 1.4f},
            {-3.7f, -1.9f, -1.8f},
            {-3.9f, 0.7f, 0.7f},
            {-4.1f, 1.7f, -3.3f},
            {-4.3f, -3.1f, 3.3f},
            {-4.4f, -2.6f, -2.8f},
            {-4.6f, 2.2f, 2.8f},
            {-4.2f, 0.8f, -1.7f}
    };

    //    ArrayList<Integer> ranNum = new ArrayList<>();
    int[] ranNum = new int[MAX];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBarAndTitleBar();
        setContentView(R.layout.activity_game);

        mSurfaceView = findViewById(R.id.gl_surface_view);

        colorBtn = findViewById(R.id.colorBtn);

        skipBtn = findViewById(R.id.skipBtn);

        hintBtn = findViewById(R.id.hintBtn);

        answerTxtView = findViewById(R.id.answerTxtView);

        questionTxtView = findViewById(R.id.questionTxtView);

        //////타이머 관련////////
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");

        if (!running) { //running이 false이면 타이머 돌아감
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        } else {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            timerValue = getSecondsFromDurationString(chronometer.getText().toString());
            running = false;
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

                Log.d("확인 한번", event.getX() + " , " + event.getY());
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
                            modelTransArrayMatrix[i][0] = pose.tx() + pixedMatrix[ranNum[i]][0];
                            modelTransArrayMatrix[i][1] = pose.ty() + pixedMatrix[ranNum[i]][1];
                            modelTransArrayMatrix[i][2] = pose.tz() + pixedMatrix[ranNum[i]][2];
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

                if (mCatched) {
                    mCatched = false;

                    results = frame.hitTest(mCatchX, mCatchY);

                    String msg = "터키터키~";
                    answerTxtView.setText(msg);

                    for (HitResult result : results) {
                        Pose pose = result.getHitPose(); // 증강 공간에서의 좌표

//                        if (catchCheck(pose.tx(), pose.ty(), pose.tz())) {
//                            msg = "터키터키~";
//                            answerTxtView.setText(msg);
//                            Log.d("터치함", "좌표 : " + pose.tx() + pose.ty() + pose.tz());
//                        } else {
//                            msg = "못잡겠쥐~?";
//                            answerTxtView.setText(msg);
//                            Log.d("터치안함", "좌표 : " + pose.tx() + pose.ty() + pose.tz());
//                        }

                        nearPoint(pose.tx(), pose.ty(), pose.tz());

                        float[] picColor = new float[]{0.2f, 0.2f, 0.2f, 0.8f};
                        mRenderer.picObjColor(picColor, minIDX);
                        if (tooFar) {
                            tooFar = false;
                            mRenderer.picObjColor(picColor, minIDX);
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
        String[] ranNumEng = intent.getStringArrayExtra("RandomEng");

        String[] ranNumKor = intent.getStringArrayExtra("RandomKor");

        questionTxtView.setText(String.format("[ %s ]", ranNumKor[count]));


        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if (count < ranNumKor.length) {
                    questionTxtView.setText(String.format("[ %s ]", ranNumKor[count]));
                    Log.d("랜덤이다~" + "if문", ranNumKor[count] + "");
                } else {
                    Toast.makeText(getApplicationContext(), "시작단어입니다", Toast.LENGTH_SHORT).show();
                    count = 0;
                    questionTxtView.setText(String.format("[ %s ]", ranNumKor[count]));
                }
            }
        });

        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = View.inflate(GameActivity.this, R.layout.activity_hint_dialog, null);
                AlertDialog.Builder hintDialogBuilder = new AlertDialog.Builder(GameActivity.this);
                AlertDialog hintDialog = hintDialogBuilder.create();
                hintDialog.setView(dialogView);
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


                hintTxtView = dialogView.findViewById(R.id.hintTxtView);
                hintTxtView.setText(String.format("[ %s ]", stringBuffer));

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

//        if(event.getAction() == MotionEvent.ACTION_DOWN){
//            mTouched = true;
//            mCurrentX = event.getX();
//            mCurrentY = event.getY();
//        }
        return true;
    }

//    boolean catchCheck(float x, float y, float z) {
//
//        float[][] resAll = mRenderer.getMinMaxPoint();
//        float[] minPoint = resAll[0];
//        float[] maxPoint = resAll[1];
////        float [] minPoint = new float[]{-0.5f,-2.5f,-10.5f};
////        float [] maxPoint = new float[]{0.5f,2.5f,10.5f};
//
//        // 범위가 좁으므로 범위를 강제로 넓혀준다(민감도를 떨어뜨린다)
//        if (x >= minPoint[0] - 0.0f && x <= maxPoint[0] + 0.0f &&
//                y >= minPoint[1] - 0.0f && y <= maxPoint[1] + 0.0f &&
//                z >= minPoint[2] - 0.0f && z <= maxPoint[2] + 0.0f) {
//            return true;
//        }
//
//        return false;
//    }

    float[][] catchCheck() {


        float[][][] resAll = mRenderer.getMinMaxPoint();
        float[][] resXYZ = new float[MAX][3];

        for (int i = 0; i < MAX; i++) {
            resXYZ[i][0] = (resAll[i][1][0] - resAll[i][0][0]) / 2; // i번째 obj x의 중간값
            resXYZ[i][1] = (resAll[i][1][1] - resAll[i][0][1]) / 2; // i번째 obj y의 중간값
            resXYZ[i][2] = (resAll[i][1][2] - resAll[i][0][2]) / 2; // i번째 obj y의 중간값
        }
        return resXYZ;
    }

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

//    public void rand() {
//        int count = 10; // 난수 생성 갯수
//        int a[] = new int[count];
//        Random r = new Random();
//
//        for (int i = 0; i < count; i++) {
//            a[i] = r.nextInt(100); // 0~20까지의 난수
//            for (int j = 0; j < i; j++) {
//                if (a[i] == a[j]) {
//                    i--;
//                }
//            }
//        }
//
//        for (int i = 0; i < 10; i++) {
//            intRandom[i] = a[i];
//        }
//    }

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

    int minIDX = 0;
    boolean tooFar = false;

    void nearPoint(float clickX, float clickY, float clickZ) {

        // modelTransArrayMatrix 여기서 중간값 찾기

        minIDX = 0;
        PointXYZ pointAll = new PointXYZ(0, 0, 0);
        PointXYZ pointClick = new PointXYZ(clickX, clickY, clickZ);

        float minDistance = 100, newMinDistance = 0;
        float[][] resXYZ = catchCheck();

        for (int i = 0; i < MAX; i++) {
            Log.d("최근좌표 catchCheck", "요기" + resXYZ[i][0] + "," + resXYZ[i][1] + "," + resXYZ[i][2]);
        }

        for (int i = 0; i < MAX; i++) {
            pointAll.pointX = modelTransArrayMatrix[i][0];
            pointAll.pointY = modelTransArrayMatrix[i][1];
            pointAll.pointZ = modelTransArrayMatrix[i][2];

            newMinDistance = getDistPoint(pointAll, pointClick);
            if (minDistance > newMinDistance) {
                minIDX = i;
                Log.d("최근좌표 if 들어옴 minIDX", "요기" + minIDX);
                minDistance = newMinDistance;
                if (minIDX <= 12.0f) {
                    tooFar = true;
                }
            }
        }
        Log.d("최근좌표", "요기" + minIDX);
    }

    // 두 점 사이 거리의 제곱 계산 함수
    public float getDistPoint(PointXYZ p, PointXYZ q) {
        float near = (p.pointX - q.pointX) * (p.pointX - q.pointX) + (p.pointY - q.pointY) * (p.pointY - q.pointY) + (p.pointZ - q.pointZ) * (p.pointZ - q.pointZ);
        Log.d("최근좌표 near", "요기" + near);
        return near;
    }

}