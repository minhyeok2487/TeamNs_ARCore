package com.example.teamns_arcore.game;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.google.ar.core.Session;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "MainRenderer";

    CameraPreView mCamera;
    PointCloudRenderer mPointCloud;
    PlaneRenderer mPlane;
    ObjRenderer mObj, mObj2;

    boolean mViewportChanged;
    int mViewportWidth, mViewportHeight;
    RenderCallback mRenderCallback;

    boolean what = true;

    MainRenderer(Context context, RenderCallback callback) {
        mRenderCallback = callback;
        mCamera = new CameraPreView();
        mPointCloud = new PointCloudRenderer();
        mPlane = new PlaneRenderer(Color.BLUE, 0.7f);

        // Obj 생성
        mObj = new ObjRenderer(context, "andy.obj", "andy.png");
        mObj2 = new ObjRenderer(context, "ShieldCap.obj", "ShieldCap_low_Shield_BaseColor.jpg");
    }

    interface RenderCallback {
        void preRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);

        mCamera.init();
        mPointCloud.init();
        mPlane.init();
        mObj.init();
        mObj2.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mViewportChanged = true;
        mViewportWidth = width;
        mViewportHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mRenderCallback.preRender();
        GLES20.glDepthMask(false);
        mCamera.draw();
        GLES20.glDepthMask(true);
        mPointCloud.draw();

        mPlane.draw();
        if(what){
            mObj.draw();
        } else {
            mObj2.draw();
        }

    }

    void updateSession(Session session, int rotation) {
        if (mViewportChanged) {

            session.setDisplayGeometry(rotation, mViewportWidth, mViewportHeight);
            mViewportChanged = false;
            Log.d(TAG, "UpdateSession 실행");
        }
    }


    void setProjectionMatrix(float[] projMatrix) {
        mPointCloud.updateProjMatrix(projMatrix);
        mPlane.setProjectionMatrix(projMatrix);
        mObj.setProjectionMatrix(projMatrix);
        mObj2.setProjectionMatrix(projMatrix);
    }

    void setViewMatrix(float[] viewMatrix) {
        mPointCloud.updateViewMatrix(viewMatrix);
        mPlane.setViewMatrix(viewMatrix);
        mObj.setViewMatrix(viewMatrix);
        mObj2.setViewMatrix(viewMatrix);
    }

    int getTextureId() {
        return mCamera == null ? -1 : mCamera.mTextures[0];
    }
}
