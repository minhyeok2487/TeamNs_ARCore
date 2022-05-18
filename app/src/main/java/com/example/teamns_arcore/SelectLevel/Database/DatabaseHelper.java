package com.example.teamns_arcore.SelectLevel.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.teamns_arcore.SelectLevel.Model.StractEn;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DBNAME;
    public static String TABLE = "";
    //public static final String DBLOCAION = "/data/data/"+ G.context.getPackageName()+"/databases";
    public static String DBLOCAION = "";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context, String tableName, String DBname){
        super(context,DBNAME,null,1);
        DBNAME = DBname+".db";
        // TABLE = "levelone";
        TABLE = tableName;
        this.mContext = context;
        DBLOCAION = "/data/data/" + context.getPackageName() + "/databases/";


//        super(context,DBNAME,null,1); // 1은 데이터베이스 버전
//        DBNAME = DBname+".db";
//        TABLE = "levelone";
//        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void OpenDatabase(){
        String DBPath = mContext.getDatabasePath(DBNAME).getPath();
        if(mDatabase!=null && mDatabase.isOpen()){
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(DBPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void CloseDatabase(){
        if(mDatabase!=null){
            mDatabase.close();
        }
    }

    // get records
    public ArrayList<StractEn> getEnglish(){
        StractEn stractEn = null;
        ArrayList<StractEn> arrayListEng = new ArrayList<StractEn>();
        OpenDatabase();
        Cursor cursor = mDatabase.rawQuery("select * from "+TABLE, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            stractEn = new StractEn(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            // act / 행동하다 / 0
            arrayListEng.add(stractEn);
            cursor.moveToNext();
        }
        System.out.println("arrayListEng.get(0) : "+arrayListEng.get(0).getEnglish() + "," + arrayListEng.get(0).getMeans() + "," +arrayListEng.get(0).getFlagtime());
        // arrayListEng.get(0) : act,행동하다,행동,행위,100 들어옴
        cursor.close();
        CloseDatabase();
        return arrayListEng;
    }

}
