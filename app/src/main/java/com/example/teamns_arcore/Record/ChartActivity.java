package com.example.teamns_arcore.Record;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamns_arcore.DashboardActivity;
import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.Model.RecordModel;
import com.example.teamns_arcore.SelectLevel.Database.DatabaseHelper;
import com.example.teamns_arcore.SelectLevel.SelectLevelActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    BarChart barChart;
    LineChart lineChart;
    String[] items = {"4월", "5월"};
    Spinner spinner;
    TableLayout tableLayout;
    RecyclerView recycler_view;
    PaymentAdapter adapter;
    Button gotoTable;
    public static ArrayList<RecordModel> recordModels = new ArrayList<>();
    MediaPlayer mediaPlayer;
    int currentPosition = 12000;
    RecordSQLiteHelper recordSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        //배경음악
        mediaPlayer = MediaPlayer.create(this, R.raw.endding);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        //데이터 불러오기
        File database = getApplicationContext().getDatabasePath(RecordSQLiteHelper.DATABASE_NAME);
        recordSQLiteHelper = new RecordSQLiteHelper(ChartActivity.this);
        if (!database.exists()) {
            recordSQLiteHelper.getReadableDatabase();
            if (!copydatabase(ChartActivity.this)) {
                return;
            }
        }
        recordModels = recordSQLiteHelper.getData();

        gotoTable = findViewById(R.id.gotoTable);
        findViewById(R.id.gotoTable).setOnClickListener(onClickListener);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        recycler_view = findViewById(R.id.recycler_view);
        setRecyclerView();


        lineChart = (LineChart) findViewById(R.id.chart);


        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //textView.setText(items[position]);
                if (items[position].equals("4월")) {
                    barChart = (BarChart) findViewById(R.id.fragment_bluetooth_chat_barchart);
                    barChart.setTouchEnabled(false); //확대 방지
                    graphInitSetting4();       //그래프 기본 세팅
                } else if (items[position].equals("5월")) {
                    barChart = (BarChart) findViewById(R.id.fragment_bluetooth_chat_barchart);
                    barChart.setTouchEnabled(false); //확대 방지
                    graphInitSetting5();       //그래프 기본 세팅
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public void graphInitSetting4() {
        ArrayList<String> jsonList = new ArrayList<>(); // ArrayList 선언
        ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
        for (int i = 0; i < recordModels.size(); i++) {
            jsonList.add(recordModels.get(i).getScore());
            labelList.add(recordModels.get(i).getDate());
        }

        BarChartGraph(labelList, jsonList);
        LineGraph(labelList, jsonList);

    }

    public void graphInitSetting5() {
        ArrayList<String> jsonList = new ArrayList<>(); // ArrayList 선언
        ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언
        jsonList.add(recordModels.get(2).getScore());
        jsonList.add(recordModels.get(3).getScore());
        jsonList.add(recordModels.get(4).getScore());

        labelList.add(recordModels.get(2).getDate());
        labelList.add(recordModels.get(3).getDate());
        labelList.add(recordModels.get(4).getDate());
        BarChartGraph(labelList, jsonList);
        LineGraph(labelList, jsonList);
    }

    /**
     * 그래프함수
     */
    private void BarChartGraph(ArrayList<String> labelList, ArrayList<String> valList) {
        // BarChart 메소드
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry(Float.parseFloat(valList.get(i)), i));
        }

        BarDataSet depenses = new BarDataSet(entries, "점수"); // 변수로 받아서 넣어줘도 됨
        depenses.setAxisDependency(YAxis.AxisDependency.LEFT);
        barChart.setDescription(" ");

        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }

        BarData data = new BarData(labels, depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        depenses.setColors(ColorTemplate.LIBERTY_COLORS); //

        barChart.setData(data);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();


    }

    private void LineGraph(ArrayList<String> labelList, ArrayList<String> valList) {
        // BarChart 메소드
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new Entry(Float.parseFloat(valList.get(i)), i));
        }
        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < labelList.size(); i++) {
            labels.add((String) labelList.get(i));
        }
        LineDataSet depenses = new LineDataSet(entries, "점수");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(depenses);

        LineData data = new LineData(labels, dataSets);
        lineChart.setData(data);
        lineChart.animateXY(1000, 1000);
        lineChart.invalidate();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.gotoTable:
                    //myStartActivity(TableActivity.class);
                    if (tableLayout.getVisibility() == View.VISIBLE) {
                        gotoTable.setText("표로 보기");
                        tableLayout.setVisibility(View.GONE);
                        barChart.setVisibility(View.VISIBLE);
                        lineChart.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.VISIBLE);
                    } else {
                        gotoTable.setText("그래프로 보기");
                        tableLayout.setVisibility(View.VISIBLE);
                        barChart.setVisibility(View.GONE);
                        lineChart.setVisibility(View.GONE);
                        spinner.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };


    private void setRecyclerView() {
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter(this, getList());
        recycler_view.setAdapter(adapter);
    }

    //임시 데이터 생성
    //String id, String date, int correctNum, float timer, float score
    private List<RecordModel> getList() {


        return recordModels;
    }

    public Boolean copydatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(RecordSQLiteHelper.DATABASE_NAME);
            String OutFileName = RecordSQLiteHelper.DBLOCAION + RecordSQLiteHelper.DATABASE_NAME;
            File f = new File(OutFileName);
            f.getParentFile().mkdirs();
            OutputStream outputStream = new FileOutputStream(OutFileName);

            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
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

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();

        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer.seekTo(currentPosition);
        if (DashboardActivity.ismute) {
            mediaPlayer.start();
        } else {
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
}