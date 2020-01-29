package com.itshiteshverma.attackerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.itshiteshverma.attackerapp.ui.gallery.GalleryFragment.MAX_X_BOUNDS;
import static com.itshiteshverma.attackerapp.ui.gallery.GalleryFragment.MAX_Y_BOUNDS;
import static com.itshiteshverma.attackerapp.ui.gallery.GalleryFragment.MIN_X_BOUNDS;
import static com.itshiteshverma.attackerapp.ui.gallery.GalleryFragment.MIN_Y_BOUNDS;


public class DatabaseHelper extends SQLiteOpenHelper {

    Context mContext;

    public DatabaseHelper(Context context) {
        super(context, Note.DATABASE_NAME, null, Note.MAIN_DATABASE_VERSION);
        mContext = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create notes table
        db.execSQL(Note.CREATE_TABLE_ADD_VALUES);
        // dbHelper.execSQL(Note.CREATE_MONTHLY_ROOM_TABLE_VIEW);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void setValue(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // if the data is new and not present // else update it above
        //Inserting New Data
        values.put(Note.TIME, note.getTimeDiff());
        values.put(Note.X_VALUE, note.getxAxis());
        values.put(Note.Y_VALUE, note.getyAxis());
        values.put(Note.Z_VALUE, note.getzAxis());
        values.put(Note.LABEL_TAG, note.getCurrentLabel());
        values.put(Note.SAMPLE_NAME, note.getSampleName());

        db.insert(Note.VALUE_TABLE, null, values);
        db.close();
        return;
    }

    public int getValue(ArrayList<XYValue> xyValueArray) {
        String query;
        query = "SELECT " + Note.COMMAND_ACTION + " FROM " + Note.COMMAND_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int count = cursor.getCount();
        int x = (int) MIN_X_BOUNDS;
        int y = (int) MIN_Y_BOUNDS;
        int offSetY = 10, offSetX = 10;

        boolean HorizontalFWD = false;
        boolean VerticalFwd = true;
        boolean LeftRight = true; //True for Right // False for Left
        boolean UpDown = true; //True for UP // False for Down

        xyValueArray.add(new XYValue(x, y)); //Add an Initial Point

        if (cursor.moveToFirst()) {
            do {
                String cmd = cursor.getString(cursor.getColumnIndex(Note.COMMAND_ACTION));
                //Wrong
                if (cmd.equals("Move Forward")) {
                    if (HorizontalFWD && LeftRight) {
                        x += offSetX;
                    } else if (HorizontalFWD && !LeftRight) {
                        x -= offSetX;
                    } else if (VerticalFwd && UpDown) {
                        y += offSetY;
                    } else if (VerticalFwd && !UpDown) {
                        y -= offSetY;
                    }
                    xyValueArray.add(new XYValue(x, y));

                } else if (cmd.equals("Right Turn")) {
                    if (VerticalFwd) {
                        VerticalFwd = false;
                        HorizontalFWD = true;
                        if (UpDown) { //Top to Bottom
                            LeftRight = true;
                        } else { //Bottom to Top
                            LeftRight = false;
                        }
                    } else if (HorizontalFWD) {
                        VerticalFwd = true;
                        HorizontalFWD = false;
                        if (LeftRight) { //Left to Right
                            UpDown = false;
                        } else {// Right to Left
                            UpDown = true;
                        }
                    }
                } else if (cmd.equals("Left Turn")) {
                    if (VerticalFwd) {
                        VerticalFwd = false;
                        HorizontalFWD = true;
                        if (UpDown) {
                            LeftRight = false;
                        } else {
                            LeftRight = true;
                        }
                    } else if (HorizontalFWD) {
                        VerticalFwd = true;
                        HorizontalFWD = false;
                        if (LeftRight) {
                            UpDown = true;
                        } else {
                            UpDown = false;
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return count;
    }

    public int getCommands() {
        String query;
        query = "SELECT " + Note.LABEL_TAG + " FROM " + Note.VALUE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        //Starting After First Step
        if (cursor.moveToFirst()) {
            for (int i = 60; i < count; cursor.moveToPosition(i)) {
                ContentValues values = new ContentValues();
                values.put(Note.COMMAND_ACTION, cursor.getString(cursor.getColumnIndex(Note.LABEL_TAG)));
                db.insert(Note.COMMAND_TABLE, null, values);
                i += 60;
            }
        }
        db.close();
        cursor.close();
        return count;
    }

    public void clearValueTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + Note.VALUE_TABLE);
        db.execSQL(Note.CREATE_TABLE_ADD_VALUES);
        db.close();
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Note.COMMAND_TABLE);
        db.execSQL(Note.CREATE_TABLE_COMMANDS);
        db.close();
    }
}
