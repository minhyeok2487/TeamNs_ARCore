package com.example.teamns_arcore.Record;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamns_arcore.R;

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

    private List<PaymentModel> getList(){
        List<PaymentModel> payment_list = new ArrayList<>();
        payment_list.add(new PaymentModel("1","Mickey1","10000"));
        payment_list.add(new PaymentModel("2","Mickey2","20000"));
        payment_list.add(new PaymentModel("3","Mickey3","30000"));
        payment_list.add(new PaymentModel("4","Mickey4","40000"));
        payment_list.add(new PaymentModel("5","Mickey5","50000"));
        payment_list.add(new PaymentModel("6","Mickey6","60000"));

        return payment_list;
    }
}
