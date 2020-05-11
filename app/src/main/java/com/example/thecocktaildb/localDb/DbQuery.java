package com.example.thecocktaildb.localDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.example.thecocktaildb.model.Cocktail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DbQuery {

    private Context context;

    public DbQuery(Context context){
        this.context = context;
    }


    public long insertCocktail(Cocktail cocktail){
        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ID, cocktail.getIdDrink());
        contentValues.put(Config.COLUMN_TITLE, cocktail.getName());
        contentValues.put(Config.COLUMN_CATEGORY, cocktail.getCategory());
        contentValues.put(Config.COLUMN_ALCOHOLIC, cocktail.getAlcoholic());
        contentValues.put(Config.COLUMN_GLASS, cocktail.getGlass());
        contentValues.put(Config.COLUMN_INSTRUCTIONS, cocktail.getInstructions());
        contentValues.put(Config.COLUMN_THUMB, cocktail.getThumbnail());
        contentValues.put(Config.COLUMN_INGREDIENTS, cocktail.getIngredient1());
        contentValues.put(Config.COLUMN_DATE_MODIFIED, cocktail.getDateModified());
        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_COCKTAILS, null, contentValues);
            Log.e(getClass().getSimpleName(), "New cocktail added!");
        } catch (SQLiteException e){
            Log.e(getClass().getSimpleName(), e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
        return id;
    }
    public List<Cocktail> getAllCocktails(){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String SELECT_QUERY = String.format("SELECT * FROM %s ORDER BY %s DESC ", Config.TABLE_COCKTAILS, Config.COLUMN_DATE_MODIFIED);
            cursor = sqLiteDatabase.rawQuery(SELECT_QUERY, null);

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Cocktail> cocktails = new ArrayList<>();
                    do {
                        String id = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ID));
                        String title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_TITLE));
                        String category = cursor.getString(cursor.getColumnIndex(Config.COLUMN_CATEGORY));
                        String alcoholic = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ALCOHOLIC));
                        String glass = cursor.getString((cursor.getColumnIndex(Config.COLUMN_GLASS)));
                        String instructions = cursor.getString(cursor.getColumnIndex(Config.COLUMN_INSTRUCTIONS));
                        String thumb = cursor.getString(cursor.getColumnIndex(Config.COLUMN_THUMB));
                        String ingredient1 = cursor.getString(cursor.getColumnIndex(Config.COLUMN_INGREDIENTS));
                        String dateModified = cursor.getString((cursor.getColumnIndex(Config.COLUMN_DATE_MODIFIED)));
                        cocktails.add(new Cocktail(id, title, category, alcoholic, glass, instructions,
                                thumb, ingredient1, dateModified));
                    }   while (cursor.moveToNext());
                    return cocktails;
                }
        } catch (Exception e){
            Log.e(getClass().getSimpleName(), e.getMessage());
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return Collections.emptyList();
    }
    public Cocktail getCocktailById(String cocktailId){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = null;
        Cocktail cocktail = null;
        try {
            cursor = sqLiteDatabase.query(Config.TABLE_COCKTAILS, null,
                    Config.COLUMN_ID + " = ? ", new String[]{cocktailId},
                    null, null, null);

            if(cursor.moveToFirst()){
                String id = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_TITLE));
                String category = cursor.getString(cursor.getColumnIndex(Config.COLUMN_CATEGORY));
                String alcoholic = cursor.getString(cursor.getColumnIndex(Config.COLUMN_ALCOHOLIC));
                String glass = cursor.getString((cursor.getColumnIndex(Config.COLUMN_GLASS)));
                String instructions = cursor.getString(cursor.getColumnIndex(Config.COLUMN_INSTRUCTIONS));
                String thumb = cursor.getString(cursor.getColumnIndex(Config.COLUMN_THUMB));
                String ingredient1 = cursor.getString(cursor.getColumnIndex(Config.COLUMN_INGREDIENTS));
                String dateModified = cursor.getString((cursor.getColumnIndex(Config.COLUMN_DATE_MODIFIED)));
                cocktail = new Cocktail(id, title, category, alcoholic, glass, instructions,
                        thumb, ingredient1, dateModified);
            }
        } catch (Exception e){
            Log.e(getClass().getSimpleName(), "Operation failed");
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return cocktail;
    }
    public long updateCocktailInfo(Cocktail cocktail){
        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_DATE_MODIFIED, cocktail.getDateModified());
        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_COCKTAILS, contentValues,
                    Config.COLUMN_ID + " = ? ",
                    new String[] {cocktail.getIdDrink()});
            Log.e(getClass().getSimpleName(), "Data updated!");
        } catch (SQLiteException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return rowCount;
    }
    public long deleteCocktailById(String cocktailId) {
        long deletedRowCount = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        try {
            deletedRowCount = sqLiteDatabase.delete(Config.TABLE_COCKTAILS,
                    Config.COLUMN_ID + " = ? ", new String[]{cocktailId});
            Log.e(getClass().getSimpleName(), "Cocktail deleted!");
        } catch (SQLiteException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return deletedRowCount;
    }
    public boolean deleteAllCocktails(){
        boolean deleteStatus = false;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        try {
            sqLiteDatabase.delete(Config.TABLE_COCKTAILS, null, null);
            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_COCKTAILS);
            if(count==0)
                deleteStatus = true;
            Toast.makeText(context, "Deleted cocktails: " + count, Toast.LENGTH_LONG).show();
        } catch (SQLiteException e){
            Log.e(getClass().getSimpleName(), e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
        return deleteStatus;
    }
    public long getNumberOfCocktails(){
        long count = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        try {
            count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_COCKTAILS);
        } catch (SQLiteException e){
            Log.e(getClass().getSimpleName(), e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
        return count;
    }


}
