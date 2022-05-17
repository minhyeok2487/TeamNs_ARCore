package com.example.teamns_arcore.Record;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.Model.RecordModel;

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
        adapter = new PaymentAdapter(this, getList());
        recycler_view.setAdapter(adapter);
    }

    private List<RecordModel> getList(){
        List<RecordModel> recordModels = new ArrayList<>();
        RecordModel model1 = new RecordModel("냐옹",Date.valueOf("2022-04-13"),8, 100, 0, 1);
        model1.setScore();
        recordModels.add(model1);

        RecordModel model2 = new RecordModel("냐옹",Date.valueOf("2022-04-17"),4, 50, 0,2);
        model2.setScore();
        recordModels.add(model2);

        RecordModel model3 = new RecordModel("냐옹",Date.valueOf("2022-05-05"),8, 130, 0,2);
        model3.setScore();
        recordModels.add(model3);

        RecordModel model4 = new RecordModel("냐옹",Date.valueOf("2022-05-10"),10, 150, 0,3);
        model4.setScore();
        recordModels.add(model4);

        RecordModel model5 = new RecordModel("냐옹",Date.valueOf("2022-05-11"),8, 110, 0,4);
        model5.setScore();
        recordModels.add(model5);

        return recordModels;
    }
}
