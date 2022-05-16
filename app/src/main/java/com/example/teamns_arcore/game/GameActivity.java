package com.example.teamns_arcore.game;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Window;
import android.view.WindowManager;
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
    TextView myTextView;
    GLSurfaceView mSurfaceView;
    MainRenderer mRenderer;

    Session mSession;
    Config mConfig;

    boolean mUserRequestedInstall = true, mTouched = false, isModelSetting = false;
    float[] modelMatrix = new float[16];
    Frame frame = null;

    float mCurrentX, mCurrentY;
    float mRotateFactor = 0f;
    float mScaleFactor = 1.0f;

    //이동, 회전 이벤트 처리할 객체
    GestureDetector mGestureDetector;

    //크기조절 이벤트 처리할 객체
    ScaleGestureDetector mScaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        myTextView = (TextView) findViewById(R.id.my_textView);

        //제스처이벤트 콜백함수 객체를 생성자 매개변수로 처리(이벤트핸들러)
        mGestureDetector = new GestureDetector(GameActivity.this, new GestureDetector.SimpleOnGestureListener() {
            //따닥 처리(이동)
            @Override
            public boolean onDoubleTap(MotionEvent event) {
                mTouched = true;
                mCurrentX = event.getX();
                mCurrentY = event.getY();
                isModelSetting = false;
                Log.d("더블 클릭", mCurrentX + "," + mCurrentY);
                if(mRenderer.what){
                    mRenderer.what = false;
                } else {
                    mRenderer.what = true;
                }
                return true;
            }

            //드래그 처리(회전)
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("드래그", distanceX + "," + distanceY);
                if(isModelSetting){
                    mRotateFactor += -distanceX/5;
                    if(Math.abs(distanceX) > 1 && Math.abs(distanceY) <1){
                        Matrix.rotateM(modelMatrix,0,-distanceX/5,0f,1f,0f);
                    } else if(Math.abs(distanceY) > 1 && Math.abs(distanceX) <1){
                        Matrix.rotateM(modelMatrix,0,-distanceY/5,1f,0f,0f);
                    } else if(Math.abs(distanceY) > 1 && Math.abs(distanceX) >1){
                        if(Math.abs(distanceX) >= Math.abs(distanceY)){
                            Matrix.rotateM(modelMatrix,0,-distanceX/5,0f,1f,0f);
                        } else {
                            Matrix.rotateM(modelMatrix,0,-distanceY/5,1f,0f,0f);
                        }
                    }

                }
                return true;
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(GameActivity.this, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                //두 손가락으로 스케일 시 작동
                Log.d("스케일", detector.getScaleFactor() + ",");
                if(isModelSetting){
                    float size = detector.getScaleFactor();
                    mScaleFactor *= size;
                    Matrix.scaleM(modelMatrix,0,size,size,size);
                }
                return true;
            }
        });

        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (displayManager != null) {
            //화면에 대한 리스너 실행
            displayManager.registerDisplayListener(
                    //익명클래스로 정의
                    new DisplayManager.DisplayListener() {
                        @Override
                        public void onDisplayAdded(int i) {
                        }

                        @Override
                        public void onDisplayRemoved(int i) {
                        }

                        //화면이 변경되었다면
                        @Override
                        public void onDisplayChanged(int i) {
                            synchronized (GameActivity.this) {
                                mRenderer.mViewportChanged = true;
                            }
                        }
                    }, null
            );
        }

        mRenderer = new MainRenderer(GameActivity.this, new MainRenderer.RenderCallback() {
            @Override
            public void preRender() {
                if (mRenderer.mViewportChanged) {
                    Display display = getWindowManager().getDefaultDisplay();
                    int displayRotation = display.getRotation();
                    mRenderer.updateSession(mSession, displayRotation);
                }

                mSession.setCameraTextureName(mRenderer.getTextureId());


                try {
                    frame = mSession.update();
                } catch (CameraNotAvailableException e) {
                    e.printStackTrace();
                }

                if (frame.hasDisplayGeometryChanged()) {
                    mRenderer.mCamera.transformDisplayGeometry(frame);
                }
                PointCloud pointCloud = frame.acquirePointCloud();
                mRenderer.mPointCloud.update(pointCloud);
                pointCloud.release();

                //터치하였다면
                if (mTouched) {
                    List<HitResult> results = frame.hitTest(mCurrentX, mCurrentY);
                    for (HitResult result : results) {
                        Pose pose = result.getHitPose(); // 증강공간에서의 좌표

                        if(!isModelSetting){
                            pose.toMatrix(modelMatrix, 0); //좌표를 가지고 matrix화 함
                            isModelSetting = true;
                            //초기화시 기존의 회전값으로 회전한다.
                            Matrix.rotateM(modelMatrix,0,-mRotateFactor,0f,1f,0f);
                            Matrix.scaleM(modelMatrix,0,mScaleFactor,mScaleFactor,mScaleFactor);

                        }

                        //증강공간의 좌표에 객체있는지 받아온다.(Plane이 걸려있는가?)
                        Trackable trackable = result.getTrackable();


                        //좌표에 걸린 객체가 Plane 인가?
                        if (trackable instanceof Plane &&
                                //Plane 폴리곤(면)안에 좌표가 있는가?
                                ((Plane) trackable).isPoseInPolygon(pose)) {
                            mRenderer.mObj.setModelMatrix(modelMatrix);
                            mRenderer.mObj2.setModelMatrix(modelMatrix);
                        }
                    }
                    //mTouched = false;
                }


                //Session으로부터 증강현실 속에서의 평면이나 점 객체를 얻을 수 있다.
                Collection<Plane> planes = mSession.getAllTrackables(Plane.class);
                //ARCore 상의 Plane들을 얻는다.

                boolean isPlaneDetected = false;

                for (Plane plane : planes) {
                    //Plane이 정상이라면
                    if (plane.getTrackingState() == TrackingState.TRACKING
                            && plane.getSubsumedBy() == null) { //plane.getSubsumedBy() 다른 평면 존재여부
                        isPlaneDetected = true;
                        //렌더링에서 plane 정보를 갱신하여 출력
                        mRenderer.mPlane.update(plane);
                    }
                }

                if (isPlaneDetected) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myTextView.setText("평면입니다");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myTextView.setText("평?면 몰?루");
                        }
                    });
                }

                Camera camera = frame.getCamera();
                float[] projMatrix = new float[16];
                camera.getProjectionMatrix(projMatrix, 0, 0.1f, 100f);
                float[] viewMatrix = new float[16];
                camera.getViewMatrix(viewMatrix, 0);

                mRenderer.setProjectionMatrix(projMatrix);
                mRenderer.setViewMatrix(viewMatrix);
            }
        });


        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mSurfaceView.setRenderer(mRenderer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCameraPermission();
        try {
            if (mSession == null) {
                switch (ArCoreApk.getInstance().requestInstall(GameActivity.this, mUserRequestedInstall)) {
                    case INSTALLED: //정상적으로 설치되어있으면 Session을 생성한다
                        mSession = new Session(GameActivity.this);
                        break;
                    case INSTALL_REQUESTED: // ARCore 설치 필요
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
            Config ar_session_config = mSession.getConfig();
            ar_session_config.setFocusMode(Config.FocusMode.AUTO);
            mSession.configure(ar_session_config);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(GameActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GameActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event); //위임해서 받아옴
        mScaleGestureDetector.onTouchEvent(event);
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            mTouched = true;
//            mCurrentX = event.getX();
//            mCurrentY = event.getY();
//        }
        return true;
    }
}
