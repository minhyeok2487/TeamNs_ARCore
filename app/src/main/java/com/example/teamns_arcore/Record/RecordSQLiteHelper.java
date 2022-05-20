package com.example.teamns_arcore.Record;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.teamns_arcore.Record.Model.RecordModel;
import com.example.teamns_arcore.SelectLevel.Model.StractEn;

import java.util.ArrayList;

public class RecordSQLiteHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "record_data.db";

    public static final String TABLE_NAME = "Record_data";

    public static final String Table_Column_ID = "id"; // ID
    public static final String Table_Column_1_Date = "Date"; // 날짜
    public static final String Table_Column_2_CorrectNum = "CorrectNum"; // 맞은정답개수
    public static final String Table_Column_3_Timer = "Timer"; // 타이머
    public static final String Table_Column_4_Score = "Score"; // 점수
    public static final String Table_Column_5_Level = "Level"; // 레벨

    private Context mContext;
    public static String DBLOCAION = "";
    private SQLiteDatabase mDatabase;

    public RecordSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.mContext = context;
        DBLOCAION = "/data/data/" + context.getPackageName() + "/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + Table_Column_ID + " TEXT, " + Table_Column_1_Date + " TEXT, " + Table_Column_2_CorrectNum + " INTEGER, " + Table_Column_3_Timer + " INTEGER, " + Table_Column_4_Score + " INTEGER, " + Table_Column_5_Level + " INTEGER)";
        database.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void OpenDatabase() {
        String DBPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(DBPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void CloseDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    // get records
    public ArrayList<RecordModel> getData() {
        RecordModel model = null;
        ArrayList<RecordModel> arrayRecord = new ArrayList<RecordModel>();
        OpenDatabase();
        Cursor cursor = mDatabase.rawQuery("select * from " + TABLE_NAME + ";", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            model = new RecordModel(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
            // act / 행동하다 / 0
            arrayRecord.add(model);
            cursor.moveToNext();
        }
        cursor.close();
        CloseDatabase();
        return arrayRecord;
    }

}