package com.example.teamns_arcore.Record;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.Model.RecordModel;
import com.github.mikephil.charting.charts.BarChart;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity {
    RecyclerView recycler_view;
    PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_table);

        recycler_view = findViewById(R.id.recycler_view);
        setRecyclerView();
    }

    private void setRecyclerView() {
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter(this, ChartActivity.recordModels);
        recycler_view.setAdapter(adapter);
    }


}
