package com.example.teamns_arcore.game;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.ar.core.PointCloud;

public class PointCloudRenderer {

    float[] mViewMatrix = new float[16];
    float[] mProjMatrix = new float[16];

    // GPU를 이용하여 고속 계산하여 화면처리 하기위한 코드
    String vertexShaderCode =
            "uniform mat4 uMvpMatrix;" +
                    "uniform vec4 uColor;" +
                    "uniform float uPointSize;" +
                    "attribute vec4 vPosition;" +
                    "varying vec4 vColor;" +
                    "void main () {" +
                    "vColor =  uColor;" +
                    "gl_Position =  uMvpMatrix * vec4(vPosition.xyz, 1.0);" +
                    "gl_PointSize =  uPointSize;" +
                    "}";

    String fragmentShaderCode =
            "precision mediump float;" // 정밀도
                    + "varying vec4 vColor;" // 4개의 원소(RGB alpha)
                    + "void main() {"
                    + "gl_FragColor = vColor; "
                    + "}";

    // 직사각형 점의 좌표
    static float[] QUARD_COORDS = {
            // x, y, z
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
    };

    static float[] QUARD_TEXCOORDS = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    int[] mVbo;
    int mProgram;

    int mNumPoints = 10;

    PointCloudRenderer() {

    }

    //초기화
    void init() {
        //점 생성
        mVbo = new int[1];
        GLES20.glGenBuffers(1, mVbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, 1000 * 16, null, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        //점쉐이더 생성
        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vShader, vertexShaderCode);

        //점쉐이더 컴파일
        GLES20.glCompileShader(vShader);

        //텍스처
        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShader, fragmentShaderCode);

        //텍스처 컴파일
        GLES20.glCompileShader(fShader);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vShader);
        GLES20.glAttachShader(mProgram, fShader);
        GLES20.glLinkProgram(mProgram);
    }

    //3차원 좌표 값을 갱신할 메소드
    void update(PointCloud pointCloud) {
        //점 위치 정보 받기 위한 바인딩
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVbo[0]);
        //그려야 할 점 갯수 갱신
        mNumPoints = pointCloud.getPoints().remaining() / 4;

        //점위치 정보 Buffer로 갱신
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, mNumPoints * 16, pointCloud.getPoints());

        //점 위치 정보 바인딩 해제
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }

    //그리기
    void draw() {
        float[] mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mViewMatrix, 0);


        //렌더링 계산식 정보 사용한다.
        GLES20.glUseProgram(mProgram);

        //      vPosition
        //mProgram ==> vertexShader
        int position = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int color = GLES20.glGetUniformLocation(mProgram, "uColor");
        int mvp = GLES20.glGetUniformLocation(mProgram, "uMvpMatrix");
        int size = GLES20.glGetUniformLocation(mProgram, "uPointSize");

        //GPU 활성화
        GLES20.glEnableVertexAttribArray(position);
        //바인딩
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVbo[0]);
        //점 포인터
        GLES20.glVertexAttribPointer(
                position, // 정점 속성의 인덱스 지정
                4, // 점속성 - 좌표계
                GLES20.GL_FLOAT, //점의 자료형 float
                false, //정규화 true, 직접 변환 false
                16, //점 속성의 stride(간격)
                0 //점 정보

        );

        //색
        GLES20.glUniform4f(color, 1.0f, 0.0f, 0.0f, 1.0f);


        //그려지는 곳에 위치, 보이는 정보를 적용한다.
        GLES20.glUniformMatrix4fv(mvp, 1, false, mMVPMatrix, 0);

        //점크기
        GLES20.glUniform1f(size, 5.0f);


        //점들을 그린다.
        GLES20.glDrawArrays(
                GLES20.GL_POINTS,
                0, mNumPoints
        );

        //닫는다.
        GLES20.glDisable(position);
        //바인딩 해제
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }


//    void updatematrix(float [] mViewMatrix, float [] mProjMatrix ){
//        //배열 복제         원본  시작점 복제할곳    시작점 갯수
//        System.arraycopy(mViewMatrix, 0 , this.mViewMatrix, 0, 16);
//        System.arraycopy(mProjMatrix, 0 , this.mProjMatrix, 0, 16);
//    }

    void updateProjMatrix(float[] projMatrix) {
        System.arraycopy(projMatrix, 0 , this.mProjMatrix, 0, 16);
    }

    void updateViewMatrix(float[] viewMatrix) {
        System.arraycopy(viewMatrix, 0 , this.mViewMatrix, 0, 16);
    }


}
