package com.example.teamns_arcore.Record;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamns_arcore.R;
import com.example.teamns_arcore.Record.Model.RecordModel;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
    Context context;
    List<RecordModel> recordModelList;

    public PaymentAdapter(Context context, List<RecordModel> recordModelList){
        this.context = context;
        this.recordModelList = recordModelList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(recordModelList != null && recordModelList.size() >0){
            RecordModel model = recordModelList.get(position);
            holder.Date_Textview.setText(model.getDate());
            holder.CorrectNum_Textview.setText(model.getCorrectNum());
            holder.Timer_Textview.setText(model.getTimer());
            holder.Score_Textview.setText(model.getScore());
            holder.Level_Textview.setText(model.getLevel());
        } else {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return recordModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView Date_Textview, CorrectNum_Textview, Timer_Textview, Score_Textview,Level_Textview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Date_Textview = itemView.findViewById(R.id.Date_Textview);
            CorrectNum_Textview = itemView.findViewById(R.id.CorrectNum_Textview);
            Timer_Textview = itemView.findViewById(R.id.Timer_Textview);
            Score_Textview = itemView.findViewById(R.id.Score_Textview);
            Level_Textview = itemView.findViewById(R.id.Level_Textview);
        }
    }
}
