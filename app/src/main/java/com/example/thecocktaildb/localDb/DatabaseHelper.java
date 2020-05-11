package com.example.thecocktaildb.localDb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = Config.DATABASE_NAME;
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if(databaseHelper==null){
            synchronized (DatabaseHelper.class) {
                if(databaseHelper==null)
                    databaseHelper = new DatabaseHelper(context);
            }
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables SQL execution
        String CREATE_BUDGET_TABLE = "CREATE TABLE " + Config.TABLE_COCKTAILS + "("
                + Config.COLUMN_ID + " INTEGER NOT NULL, "
                + Config.COLUMN_TITLE + " TEXT NOT NULL, "
                + Config.COLUMN_CATEGORY + " TEXT, "
                + Config.COLUMN_ALCOHOLIC + " TEXT, "
                + Config.COLUMN_GLASS + " TEXT, "
                + Config.COLUMN_INSTRUCTIONS + " TEXT, "
                + Config.COLUMN_THUMB + " TEXT, "
                + Config.COLUMN_INGREDIENTS + " TEXT, "
                + Config.COLUMN_DATE_MODIFIED + " DATETIME "
                + ")";

        db.execSQL(CREATE_BUDGET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_COCKTAILS);
        onCreate(db);
    }
}
