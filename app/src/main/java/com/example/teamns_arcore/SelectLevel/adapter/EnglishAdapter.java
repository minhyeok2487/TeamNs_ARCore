package com.example.teamns_arcore.SelectLevel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.teamns_arcore.R;
import com.example.teamns_arcore.SelectLevel.Model.StractEn;

import java.util.ArrayList;


public class EnglishAdapter extends ArrayAdapter<StractEn> {
    public Context context;
    public LayoutInflater inflater;
    public EnglishAdapter(Context context, ArrayList<StractEn> array){
        super(context, R.layout.recorditem, array);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder{
        public TextView txtfname;
        public TextView txtlname;
        public TextView txtaname;

        public ViewHolder(View view){

            txtfname = view.findViewById(R.id.txtfname);
            txtlname = view.findViewById(R.id.txtlname);
            txtaname = view.findViewById(R.id.txtaname);

        }

        public void fill(final ArrayAdapter<StractEn> adapter , final StractEn item, final int position){
            txtfname.setText(item.getEnglish());
            txtlname.setText(item.getMeans());
            txtaname.setText(item.getFlagtime());
        }
    }
    @Override
    public View getView(int position, View convertview, ViewGroup parent){
        ViewHolder holder;
        StractEn item = getItem(position);
        if(convertview == null){
            //  .inflate( 1.객체화하고픈 xml파일, 2.객체화한 뷰를 넣을 부모 레이아웃/컨테이너, 3.true(바로 인플레이션 하고자 하는지))
            convertview = inflater.inflate(R.layout.recorditem, parent, false);
            holder = new ViewHolder(convertview);
            convertview.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertview.getTag();
        }
        holder.fill(this, item, position);
        return convertview;
    }

}
