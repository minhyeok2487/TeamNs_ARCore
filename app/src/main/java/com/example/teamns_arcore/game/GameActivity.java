package com.example.teamns_arcore.game;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.teamns_arcore.R;
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

import java.util.Collection;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    TextView answerTxtView;

    GLSurfaceView mSurfaceView;
    MainRenderer mRenderer;

    Session mSession;
    Config mConfig;

    boolean mUserRequestedInstall = true, isModelInit = false, mCatched = false, flag = true;

    float mCurrentX, mCurrentY, mCatchX, mCatchY;

    //이동, 회전 이벤트 처리할 객체
    GestureDetector mGestureDetector;

    Button colorBtn;

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

        answerTxtView = findViewById(R.id.answerTxtView);

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

                        if (catchCheck(pose.tx(), pose.ty(), pose.tz())) {
                            msg = "터키터키~";
                            answerTxtView.setText(msg);
                        } else {
                            msg = "못잡겠쥐~?";
                            answerTxtView.setText(msg);
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

    boolean catchCheck(float x, float y, float z) {

        float[][] resAll = mRenderer.getMinMaxPoint();
        float[] minPoint = resAll[0];
        float[] maxPoint = resAll[1];
//        float [] minPoint = new float[]{-0.5f,-2.5f,-10.5f};
//        float [] maxPoint = new float[]{0.5f,2.5f,10.5f};

        // 범위가 좁으므로 범위를 강제로 넓혀준다(민감도를 떨어뜨린다)
        if (x >= minPoint[0] - 0.0f && x <= maxPoint[0] + 0.0f &&
                y >= minPoint[1] - 0.0f && y <= maxPoint[1] + 0.0f &&
                z >= minPoint[2] - 0.0f && z <= maxPoint[2] + 0.0f) {
            return true;
        }

        return false;
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

    int i = 0;

    void setColorCorrection(LightEstimate estimate) {
        colorCorrection = colorCorrections[i];

        mRenderer.setColorCorrection(colorCorrection);

        i++;
        i %= 4;
    }
}