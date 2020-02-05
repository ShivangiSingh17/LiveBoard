package com.example.livewhiteboard;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.livewhiteboard.Constants.*;
public class DrawingsTable {

    public static final String TABLE_NAME = "drawings";

    public interface Columns {
        String ID = "id";
        String DRAWINGS = "drawing";
        String NAME = "drawingname";

    }

    public static final String CMD_CREATE_TABLE =
            CMD_CREATE_TABLE_INE + TABLE_NAME
                    + LBR
                    + Columns.ID + TYPE_INT + TYPE_PK_AI + COMMA
                    + Columns.NAME + TYPE_TEXT  + COMMA
                    + Columns.DRAWINGS + TYPE_BLOB
                    + RBR
                    + SEMI;

    public static long insertDrawing (String name, byte[] drawing, SQLiteDatabase db){

        ContentValues newDrawing = new ContentValues();
        newDrawing.put(Columns.NAME, name);
        newDrawing.put(Columns.DRAWINGS, drawing);

        return db.insert(TABLE_NAME, null, newDrawing);

    }

    public static ArrayList<DrawingModel> getAllDrawings (SQLiteDatabase db){

        Cursor c = db.query(
                TABLE_NAME,
                new String[]{Columns.ID, Columns.NAME, Columns.DRAWINGS},
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<DrawingModel> drawings = new ArrayList<>();
        int nameIndex = c.getColumnIndex(Columns.NAME);
        int idIndex = c.getColumnIndex(Columns.ID);
        int drawingIndex = c.getColumnIndex(Columns.DRAWINGS);

        c.moveToFirst();

        while (!c.isAfterLast()){
            drawings.add(new DrawingModel(
                    c.getInt(idIndex),
                    c.getString(nameIndex),
                    c.getBlob(drawingIndex)
            ));
            c.moveToNext();
        }

        return drawings;
    }

    public static DrawingModel getDrawing(int id, SQLiteDatabase db){

        Cursor c = db.query(
                TABLE_NAME,
                new String[]{Columns.ID, Columns.NAME, Columns.DRAWINGS},
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
                );

        int nameIndex = c.getColumnIndex(Columns.NAME);
        int idIndex = c.getColumnIndex(Columns.ID);
        int drawingIndex = c.getColumnIndex(Columns.DRAWINGS);

        c.moveToFirst();

        return new DrawingModel(
                c.getInt(idIndex),
                c.getString(nameIndex),
                c.getBlob(drawingIndex)
        );
    }
}
