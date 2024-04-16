package com.dhay.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    SQLiteDatabase db = this.getWritableDatabase();

    public static final int version = 1;
    public static String dbName = "Courses.db";
    public static final String TABLE_NAME = "Courses";
    public static final String COL1 = "id";
    public static final String COL2 = "name";
    public static final String COL3 = "description";
    public static final String COL4 = "datestart";
    public static final String COL5 = "dateend";

    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME +
            "(" + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL2 +
            " TEXT NOT NULL," + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " TEXT );";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, dbName, null, version);
        context = this.context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public boolean InsertCourse(CoursesModel course) {
        ContentValues cv = new ContentValues();
        cv.put(COL2, course.getName());
        cv.put(COL3, course.getDescription());
        cv.put(COL4, course.getDateStart());
        cv.put(COL5, course.getDateEnd());
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1)
            return false;
        else
            return true;
    }

    public ArrayList<CoursesModel> getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<CoursesModel> dataList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                dataList.add(new CoursesModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return dataList;
    }
    public void deleteData(int id) {
        db.delete(TABLE_NAME,
                COL1 + "=?",
                new String[]{String.valueOf(id)});
    }

}